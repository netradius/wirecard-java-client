package com.netradius.wirecard.http;

import com.netradius.wirecard.util.LogFilter;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Implementation of HttpClient which uses HttpURLConnection underneath.
 * Be aware that turning on debug logging for this class will decrease performance.
 *
 * @author Erik R. Jensen
 */
public class HttpURLConnectionClient implements HttpClient {

	protected final LogFilter log;
	protected String authorization;

	public HttpURLConnectionClient(String username, String password) {
		log = new LogFilter(LoggerFactory.getLogger(getClass()));
		authorization = username + ":" + password;
		authorization = "Basic " + DatatypeConverter.printBase64Binary(authorization.getBytes(Charset.forName("UTF-8")));
	}

	/**
	 * Attempts to generate pretty xml from a String.
	 *
	 * @param xml the xml to beautify
	 * @return the pretty xml or null if it was unable to parse the input
	 */
	protected String prettyXml(String xml) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(xml)));

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(document);
			transformer.transform(source, result);
			return result.getWriter().toString();
		} catch (IOException | SAXException | ParserConfigurationException | TransformerException x) {
			return null;
		}
	}

	protected void logRequest(HttpURLConnection conn, String body) {
		StringBuilder sb = new StringBuilder("\nHTTP Request:\n")
				.append("  URL: ").append(conn.getURL()).append("\n")
				.append("  Request Method: ").append(conn.getRequestMethod()).append("\n");
		Map<String, List<String>> headers = conn.getRequestProperties();
		sb.append("  Request Headers:\n");
		headers.keySet().stream().filter(key -> key != null).forEach(
				key -> sb.append("    ").append(key).append(": ").append(headers.get(key)).append("\n"));
		String xml = prettyXml(body);
		sb.append("  Request Body:\n").append(xml != null ? xml : body);
		log.debug(sb.toString());
	}

	protected void logResponse(HttpURLConnection conn, String body) throws IOException {
		StringBuilder sb = new StringBuilder("\nHTTP Response:\n")
				.append("  Response Code: ").append(conn.getResponseCode()).append("\n");
		Map<String, List<String>> headers = conn.getHeaderFields();
		sb.append("  Response Headers:\n");
		headers.keySet().stream().filter(key -> key != null).forEach(
				key -> sb.append("    ").append(key).append(": ").append(headers.get(key)).append("\n"));
		String xml = prettyXml(body);
		sb.append("  Response Body:\n").append(xml != null ? xml : body);
		log.debug(sb.toString());
	}

	/**
	 * Helper method to setup the connection.
	 *
	 * @param url the URL to query
	 * @return the configured connection
	 * @throws IOException if an I/O error occurs
	 */
	protected HttpURLConnection setup(String url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestProperty("Authorization", authorization);
		conn.setRequestProperty("Content-Type", "application/xml");
		conn.setRequestProperty("Accept", "application/xml");
		conn.setRequestMethod("POST");
		conn.setConnectTimeout(120 * 1000); // 2 minutes // TODO Make configurable
		conn.setReadTimeout(120 * 1000); // 2 minutes // TODO Make configurable
		return conn;
	}

	/**
	 * Helper method to read the contents of an InputStream to a String.
	 * This method will not close the stream.
	 *
	 * @param in the InputStream to rea
	 * @return the contents of the stream as a String
	 * @throws IOException if an I/O error occurs
	 */
	protected String readString(InputStream in) throws IOException {
		InputStreamReader reader = new InputStreamReader(in, Charset.forName("UTF-8"));
		StringBuilder sb = new StringBuilder();
		char[] buf = new char[8192];
		for (int read = reader.read(buf); read >= 0; read = reader.read(buf)) {
			sb.append(buf, 0, read);
		}
		return sb.toString();
	}

	/**
	 * Helper method to completely read the error stream.
	 *
	 * @param conn the connection
	 * @return the error message or null
	 */
	protected String readError(HttpURLConnection conn) {
		InputStream err = null;
		try {
			err = conn.getErrorStream();
			if (err != null) {
				return readString(err);
			}
		} catch (IOException x) {
			log.warn("An I/O error occurred reading the HTTP error stream: " + x.getMessage(), x);
		} finally {
			if (err != null) {
				try {
					err.close();
				} catch (IOException x) { /* do nothing */ }
			}
		}
		return null;
	}

	/**
	 * Helper method to cleanup connection resources after use.
	 *
	 * @param conn the connection or null
	 * @param in the input stream or null
	 * @param out the output stream or null
	 */
	protected void cleanup(HttpURLConnection conn, InputStream in, OutputStream out) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException x) { /* do nothing */ }
		}
		if (out != null) {
			try {
				out.close();
			} catch (IOException x) { /* do nothing */ }
		}
		if (conn != null) {
			conn.disconnect();
		}
	}

	/**
	 * Used for error handling.
	 *
	 * @param x the IO error that occurred
	 * @param conn the connection
	 * @return the exception containing the parsed error message and HTTP status code if any
	 * @throws IOException if an I/O error occurs
	 */
	protected HttpClientException getError(IOException x, HttpURLConnection conn) throws IOException {
		if (conn != null) {
			String error = readError(conn);
			if (log.isDebugEnabled()) {
				logResponse(conn, error);
			}
			int status = conn.getResponseCode();
			return new HttpClientException(status, error, x);
		}
		throw x;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T postEntity(String url, Class<T> type, JAXBElement<T> entity)
			throws IOException {
		HttpURLConnection conn = null;
		InputStream in = null;
		OutputStream out = null;
		try {
			conn = setup(url);
			conn.setDoOutput(true);

			// Write request
			JAXBContext jaxbContext = JAXBContext.newInstance(type);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			if (log.isDebugEnabled()) {
				StringWriter writer = new StringWriter();
				jaxbMarshaller.marshal(entity, writer);
				logRequest(conn, writer.toString());
				conn.connect();
				out = conn.getOutputStream();
				out.write(writer.toString().getBytes(Charset.forName("UTF-8")));
			} else {
				conn.connect();
				out = conn.getOutputStream();
				jaxbMarshaller.marshal(entity, out);
			}

			// Read response
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			in = conn.getInputStream();
			JAXBElement<T> result;
			if (log.isDebugEnabled()) {
				String res = readString(in);
				logResponse(conn, res);
				result = unmarshaller.unmarshal(new StreamSource(new StringReader(res)), type);
			} else {
				result = unmarshaller.unmarshal(new StreamSource(in), type);
			}

			return result.getValue();
		} catch (IOException x) {
			throw getError(x, conn);
		} catch (JAXBException x) {
			throw new IOException("Unable to parse response: " + x.getMessage(), x);
		} finally {
			cleanup(conn, in, out);
		}
	}
}
