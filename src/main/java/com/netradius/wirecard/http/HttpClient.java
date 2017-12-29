package com.netradius.wirecard.http;

import java.io.IOException;
import java.io.Serializable;
import javax.xml.bind.JAXBElement;

/**
 * Contract for all HttpClient implementations.
 *
 * @author Abhinav Nahar
 * @author Erik R. Jensen
 */
public interface HttpClient extends Serializable {

  <T> T postEntity(String url, Class<T> type, JAXBElement<T> entity) throws IOException;

}
