package com.netradius.wirecard.integration;

import com.netradius.wirecard.WirecardClient;
import com.netradius.wirecard.WirecardPaymentResponse;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Integration test for sepa transactions.
 *
 * @author Erik R. Jensen
 */
public class SepaPaymentTest {

	// The following values came from the wirecard-payment-processing-api-1.5.1.pdf file
	private static final String username = "70000-APITEST-AP";
	private static final String password = "qD2wzQ_hrc!8";
	private static final String merchantId = "4c901196-eff7-411e-82a3-5ef6b6860d64";

	@Test
	public void testSepa() throws IOException {
		WirecardClient client = new WirecardClient(WirecardClient.TESTING_URL, username, password, merchantId);
		WirecardPaymentResponse response = client.newSepaPayment()
				.setRequestId(UUID.randomUUID().toString()) // random request ID
				.setBic("WIREDEMMXXX") // from examples in doc
				.setIban("DE42512308000000060004") // from examples in doc
				.setCreditorId("DE98ZZZ09999999999") // from examples in doc
				.setFirstName("John")
				.setLastName("Doe")
				.setMandateId("12345678") // from examples in doc
				.setSignedDate(new Date())
				.setAmount(new BigDecimal("1.00"))
				.submit();
	}

}
