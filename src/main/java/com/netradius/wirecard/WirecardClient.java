package com.netradius.wirecard;

import com.netradius.wirecard.http.HttpClient;
import com.netradius.wirecard.http.HttpURLConnectionClient;
import com.netradius.wirecard.schema.Payment;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * Client to talk to Wirecard.
 *
 * @author Erik R. Jensen
 */
public class WirecardClient {

  public static final String TESTING_URL = "https://api-test.wirecard.com/engine/rest/paymentmethods/";
  public static final String PRODUCTION_URL = "https://api.wirecard.com/engine/rest/paymentmethods/";

  protected String url;
  protected HttpClient httpClient;
  protected String merchantAccountId;
  protected JAXBContext jaxbContext;

  protected void initJaxb() {
    try {
      jaxbContext = JAXBContext.newInstance(Payment.class);
    } catch (JAXBException x) {
      throw new IllegalStateException("Unable to create JAXBContext: " + x.getMessage(), x);
    }
  }

  public WirecardClient(String url, String username, String password, String merchantAccountId) {
    this.url = url;
    this.merchantAccountId = merchantAccountId;
    httpClient = new HttpURLConnectionClient(username, password);
    initJaxb();
  }

  public WirecardSepaDebit newSepaDebit() {
    return new WirecardSepaDebit(this);
  }

  public WirecardSepaVoidDebit newSepaVoidDebit() {
    return new WirecardSepaVoidDebit(this);
  }

  public WirecardSepaAuth newSepaAuth() {
    return new WirecardSepaAuth(this);
  }

  public WirecardSepaCredit newSepaCredit() {
    return new WirecardSepaCredit(this);
  }

  public WirecardSepaVoidCredit newSepaVoidCredit() {
    return new WirecardSepaVoidCredit(this);
  }

}
