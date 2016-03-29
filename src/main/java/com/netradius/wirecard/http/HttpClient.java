package com.netradius.wirecard.http;

/**
 * @author Abhinav Nahar
 */

import javax.xml.bind.JAXBElement;
import java.io.IOException;
import java.io.Serializable;

/**
 * Contract for all HttpClient implementations.
 *
 * @author Erik R. Jensen
 */
public interface HttpClient extends Serializable {

	<T> T postEntity(String url, Class<T> type, JAXBElement<T> entity) throws IOException;

}
