package com.netradius.wirecard;

import com.netradius.wirecard.http.HttpClient;
import com.netradius.wirecard.http.HttpURLConnectionClient;
import com.netradius.wirecard.schema.Payment;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * @author Erik R. Jensen
 */
public class WirecardClient {
	public static final String TESTING_URL = "https://api-test.wirecard.com/engine/rest/paymentmethods/";
	public static final String PRODUCTION_URL = "https://api.wirecard.com/engine/rest/paymentmethods/";

	protected String url;
	protected HttpClient httpClient;
	protected String merchantId;
	protected JAXBContext jaxbContext;

	public WirecardClient(String url, String username, String password, String merchantId) {
		this.url = url;
		this.merchantId = merchantId;
		httpClient = new HttpURLConnectionClient(username, password);
		try {
			jaxbContext = JAXBContext.newInstance(Payment.class);
		} catch (JAXBException x) {
			throw new IllegalStateException("Unable to create JAXBContext: " + x.getMessage(), x);
		}
	}

	public WirecardSepaPayment newSepaPayment() {
		return new WirecardSepaPayment(this);
	}

}
