package com.netradius.wirecard;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static org.hamcrest.Matchers.*;

/**
 * Integration test for sepa transactions.
 *
 * @author Erik R. Jensen
 */
@Slf4j
public class SepaIT {

	// The following values came from the wirecard-payment-processing-api-1.5.1.pdf file
	private static final String username = "70000-APITEST-AP";
	private static final String password = "qD2wzQ_hrc!8";
	private static final String merchantAccountId = "4c901196-eff7-411e-82a3-5ef6b6860d64";

	private static Random rand = new Random();

	private BigDecimal getAmount() {
		String amount = (rand.nextInt(10) + 1 )+ "." + rand.nextInt(10) + rand.nextInt(10);
		return new BigDecimal(amount);
	}

	private String getRequestId() {
		return UUID.randomUUID().toString();
	}

	private WirecardClient getClient() {
		return new WirecardClient(WirecardClient.TESTING_URL, username, password, merchantAccountId);
	}

	@Test
	public void testAuthAndDebit() throws IOException {
		String requestId = getRequestId();
		BigDecimal amount = getAmount();

		WirecardClient client = getClient();
		WirecardSepaAuth authRequest = client.newSepaAuth()
				.setRequestId(requestId)
				.setAmount(amount)
				.setBic("WIREDEMMXXX") // from examples in doc
				.setIban("DE42512308000000060004") // from examples in doc
				.setFirstName("John")
				.setLastName("Doe");

		WirecardPaymentResponse authResponse = authRequest.submit();

		assertThat(authResponse.getRequestId(), equalTo(requestId));
		assertThat(authResponse.getCurrency(), equalTo("EUR"));
		assertThat(authResponse.getMerchantAccountId(), equalTo(merchantAccountId));
		assertThat(authResponse.getTransactionType(), equalTo(authRequest.getTransactionType()));
		assertThat(authResponse.getRequestedAmount(), equalTo(authRequest.getAmount()));
		assertTrue(authResponse.isSuccess());

		requestId = getRequestId();

		WirecardSepaDebit debitRequest = client.newSepaDebit()
				.setRequestId(requestId)
				.setParentTransactionId(authResponse.getTransactionId())
				.setAmount(amount)
				.setBic("WIREDEMMXXX") // from examples in doc
				.setIban("DE42512308000000060004") // from examples in doc
				.setCreditorId("DE98ZZZ09999999999") // from examples in doc
				.setFirstName("John")
				.setLastName("Doe")
				.setMandateId("12345678") // from examples in doc
				.setSignedDate(new Date());

		WirecardPaymentResponse debitResponse = debitRequest.submit();

		assertThat(debitResponse.getRequestId(), equalTo(requestId));
		assertThat(debitResponse.getCurrency(), equalTo("EUR"));
		assertThat(debitResponse.getMerchantAccountId(), equalTo(merchantAccountId));
		assertThat(debitResponse.getTransactionType(), equalTo(debitRequest.getTransactionType()));
		assertThat(debitResponse.getRequestedAmount(), equalTo(debitRequest.getAmount()));
		assertTrue(debitResponse.isSuccess());
	}

	@Test
	public void testDebit() throws IOException {
		String requestId = getRequestId();
		BigDecimal amount = getAmount();

		WirecardClient client = getClient();
		WirecardSepaDebit debitRequest = client.newSepaDebit()
				.setRequestId(requestId)
				.setAmount(amount)
				.setBic("WIREDEMMXXX") // from examples in doc
				.setIban("DE42512308000000060004") // from examples in doc
				.setCreditorId("DE98ZZZ09999999999") // from examples in doc
				.setFirstName("John")
				.setLastName("Doe")
				.setMandateId("12345678") // from examples in doc
				.setSignedDate(new Date());

		WirecardPaymentResponse response = debitRequest.submit();

		assertThat(response.getRequestId(), equalTo(requestId));
		assertThat(response.getCurrency(), equalTo("EUR"));
		assertThat(response.getMerchantAccountId(), equalTo(merchantAccountId));
		assertThat(response.getTransactionType(), equalTo(debitRequest.getTransactionType()));
		assertThat(response.getRequestedAmount(), equalTo(debitRequest.getAmount()));
		assertTrue(response.isSuccess());
	}

	@Test
	public void testDebitAndVoid() throws IOException {
		String requestId = getRequestId();
		BigDecimal amount = getAmount();

		WirecardClient client = getClient();
		WirecardSepaDebit debitRequest = client.newSepaDebit()
				.setRequestId(requestId)
				.setAmount(amount)
				.setBic("WIREDEMMXXX") // from examples in doc
				.setIban("DE42512308000000060004") // from examples in doc
				.setCreditorId("DE98ZZZ09999999999") // from examples in doc
				.setFirstName("John")
				.setLastName("Doe")
				.setMandateId("12345678") // from examples in doc
				.setSignedDate(new Date());

		WirecardPaymentResponse debitResponse = debitRequest.submit();

		assertThat(debitResponse.getRequestId(), equalTo(requestId));
		assertThat(debitResponse.getCurrency(), equalTo("EUR"));
		assertThat(debitResponse.getMerchantAccountId(), equalTo(merchantAccountId));
		assertThat(debitResponse.getTransactionType(), equalTo(debitRequest.getTransactionType()));
		assertThat(debitResponse.getRequestedAmount(), equalTo(debitRequest.getAmount()));
		assertTrue(debitResponse.isSuccess());

		requestId = getRequestId();

		WirecardSepaVoidDebit voidRequest = client.newSepaVoidDebit()
				.setParentTransactionId(debitResponse.getTransactionId())
				.setAmount(amount)
				.setRequestId(requestId);

		WirecardPaymentResponse voidResponse = voidRequest.submit();
		assertThat(voidResponse.getRequestId(), equalTo(requestId));
		assertThat(voidResponse.getCurrency(), equalTo("EUR"));
		assertThat(voidResponse.getMerchantAccountId(), equalTo(merchantAccountId));
		assertThat(voidResponse.getTransactionType(), equalTo(voidRequest.getTransactionType()));
		assertThat(voidResponse.getRequestedAmount(), equalTo(voidRequest.getAmount()));
		assertTrue(voidResponse.isSuccess());
	}

	@Test
	public void testCredit() throws IOException {
		String requestId = getRequestId();
		BigDecimal amount = getAmount();

		WirecardClient client = getClient();
		WirecardSepaCredit creditRequest = client.newSepaCredit()
				.setRequestId(requestId)
				.setAmount(amount)
				.setBic("WIREDEMMXXX") // from examples in doc
				.setIban("DE42512308000000060004")
				.setFirstName("John")
				.setLastName("Doe");

		WirecardPaymentResponse creditResponse = creditRequest.submit();

		assertThat(creditResponse.getRequestId(), equalTo(requestId));
		assertThat(creditResponse.getCurrency(), equalTo("EUR"));
		assertThat(creditResponse.getMerchantAccountId(), equalTo(merchantAccountId));
		assertThat(creditResponse.getTransactionType(), equalTo(creditRequest.getTransactionType()));
		assertThat(creditResponse.getRequestedAmount(), equalTo(creditRequest.getAmount()));
		assertTrue(creditResponse.isSuccess());
	}

	@Test
	public void testCreditVoid() throws IOException {

		String requestId = getRequestId();
		BigDecimal amount = getAmount();

		WirecardClient client = getClient();
		WirecardSepaCredit creditRequest = client.newSepaCredit()
				.setRequestId(requestId)
				.setAmount(amount)
				.setBic("WIREDEMMXXX") // from examples in doc
				.setIban("DE42512308000000060004")
				.setFirstName("John")
				.setLastName("Doe");

		WirecardPaymentResponse creditResponse = creditRequest.submit();

		assertThat(creditResponse.getRequestId(), equalTo(requestId));
		assertThat(creditResponse.getCurrency(), equalTo("EUR"));
		assertThat(creditResponse.getMerchantAccountId(), equalTo(merchantAccountId));
		assertThat(creditResponse.getTransactionType(), equalTo(creditRequest.getTransactionType()));
		assertThat(creditResponse.getRequestedAmount(), equalTo(creditRequest.getAmount()));
		assertTrue(creditResponse.isSuccess());

		requestId = getRequestId();

		WirecardSepaVoidCredit voidRequest = client.newSepaVoidCredit()
				.setParentTransactionId(creditResponse.getTransactionId())
				.setAmount(amount)
				.setRequestId(requestId);

		WirecardPaymentResponse voidResponse = voidRequest.submit();

		assertThat(voidResponse.getRequestId(), equalTo(requestId));
		assertThat(voidResponse.getCurrency(), equalTo("EUR"));
		assertThat(voidResponse.getMerchantAccountId(), equalTo(merchantAccountId));
		assertThat(voidResponse.getTransactionType(), equalTo(voidRequest.getTransactionType()));
		assertThat(voidResponse.getRequestedAmount(), equalTo(voidRequest.getAmount()));
		assertTrue(voidResponse.isSuccess());
	}

}
