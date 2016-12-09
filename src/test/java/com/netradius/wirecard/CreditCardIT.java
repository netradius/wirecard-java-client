package com.netradius.wirecard;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Dilip S Sisodia
 */
@Slf4j
public class CreditCardIT {

	/*
	 * This values are from Wirecard online guide
	 * Ref: https://guides.wirecard.at/wcp:test_mode
	 */
	private static final String CREDITCARD_TEST_URL = "https://c3-test.wirecard.com/secure/ssl-gateway";
	private static final String username = "00000031629CA9FA";
	private static final String password = "TestXAPTER";

	private static final String merchantAccountId = "c3671cf9-c775-4e39-8d67-31ce24094682";

	private static Random rand = new Random();

	private BigDecimal getAmount() {
		String amount = (rand.nextInt(10) + 1 )+ "." + rand.nextInt(10) + rand.nextInt(10);
		return new BigDecimal(amount);
	}

	private String getRequestId() {
		return UUID.randomUUID().toString();
	}

	private WirecardClient getClient() {
		return new WirecardClient(CREDITCARD_TEST_URL, username, password, merchantAccountId);
	}


	@Test
	public void testCreditCardAuth() throws IOException {
		String requestId = getRequestId();
		BigDecimal amount = getAmount();

		WirecardClient client = getClient();

		WirecardCreditCardAuth auth = client.newCreditCardAuth()
				.setRequestId(requestId)
				.setAmount(amount)
				.setCurrency("USD")
				.setAccountNumber("4200000000000018")
				.setExpirationMonth(Short.parseShort("01"))
				.setExpirationYear(Short.parseShort("2019"))
				.setCardSecurityCode("018")
				.setCardType("VISA")
				.setCurrency("USD")
				.setFirstName("John")
				.setLastName("Doe")
				.setEmail("john.doe@test.com")
				.setStreet1("123 anystreet")
				.setStreet2("2nd street")
				.setCity("Brantford")
				.setState("ON")
				.setCountry("CA");

		WirecardPaymentResponse response = auth.submit();
		assertThat(response.getRequestId(), equalTo(requestId));

	}

	@Test
	public void testCreditCardPurchase() throws IOException {
		String requestId = getRequestId();
		BigDecimal amount = getAmount();

		WirecardClient client = getClient();

		WirecardCreditCardPurchase purchase = client.newCreditCardPurchase()
				.setRequestId(requestId)
				.setAmount(amount)
				.setCurrency("USD")
				.setAccountNumber("4200000000000018")
				.setExpirationMonth(Short.parseShort("01"))
				.setExpirationYear(Short.parseShort("2019"))
				.setCardSecurityCode("018")
				.setCardType("VISA")
				.setCurrency("USD")
				.setFirstName("John")
				.setLastName("Doe")
				.setEmail("john.doe@test.com")
				.setStreet1("123 anystreet")
				.setStreet2("2nd street")
				.setCity("Brantford")
				.setState("ON")
				.setCountry("CA");

		WirecardPaymentResponse response = purchase.submit();
		assertThat(response.getRequestId(), equalTo(requestId));
	}

	@Test
	public void testVoidAuth() throws IOException {
		String requestId = getRequestId();
		BigDecimal amount = getAmount();

		WirecardClient client = getClient();

		WirecardCreditCardVoidAuth voidAuth = client.newCreditCardVoidAuth()
				.setAmount(amount)
				.setRequestId(requestId)
				.setParentTransactionId("9e2c1d8a-a672-11e1-b76c-005056ab0016")
				.setCurrency("USD");

		WirecardPaymentResponse response = voidAuth.submit();
	}
}
