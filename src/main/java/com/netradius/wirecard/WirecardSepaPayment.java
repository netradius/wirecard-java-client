package com.netradius.wirecard;

import com.netradius.wirecard.schema.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.xml.bind.JAXBElement;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * @author Erik R. Jensen
 */
@Data
@Accessors(chain = true)
public class WirecardSepaPayment {

	protected WirecardClient client;

	/**
	 * Identification number created by the merchant. Must be unique for each request.
	 * This is a required field.
	 */
	protected String requestId;

	/**
	 * The SEPA Direct Debit creditor ID used to identify the merchant.
	 * This is a required field.
	 */
	protected String creditorId;

	/**
	 * The amount in EUR.
	 * This is a required field.
	 */
	protected BigDecimal amount;

	/**
	 * Account holder's first name.
	 * This is a required field.
	 */
	protected String firstName;

	/**
	 * Account holder's last name.
	 * This is a required field.
	 */
	protected String lastName;

	/**
	 * The bank account number.
	 * This is a required field.
	 */
	protected String iban;

	/**
	 * The business identifier code of the bank.
	 * This is a required field.
	 */
	protected String bic;

	/**
	 * The ID of the signed mandate between the merchant and consumer generated by the merchant.
	 * This is a required field.
	 */
	protected String mandateId;

	/**
	 * The date the mandate was signed.
	 * This is a required field.
	 */
	protected Date signedDate;

	protected WirecardSepaPayment(WirecardClient client) {
		this.client = client;
	}

	public WirecardPaymentResponse submit() throws IOException {
		Payment payment = new Payment();

		MerchantAccountId merchantAccountId = new MerchantAccountId();
		merchantAccountId.setValue(client.merchantId);
		payment.setMerchantAccountId(merchantAccountId);

		AccountHolder accountHolder = new AccountHolder();
		accountHolder.setFirstName(firstName);
		accountHolder.setLastName(lastName);
		payment.setAccountHolder(accountHolder);

		BankAccount bankAccount = new BankAccount();
		bankAccount.setBic(bic);
		bankAccount.setIban(iban);
		payment.setBankAccount(bankAccount);

		Mandate mandate = new Mandate();
		mandate.setMandateId(mandateId);
		mandate.setSignedDate(new SimpleDateFormat("yyyy-MM-dd").format(signedDate));
		payment.setMandate(mandate);

		Money money = new Money();
		money.setCurrency("EUR");
		money.setValue(amount);
		payment.setRequestedAmount(money);

		PaymentMethods paymentMethods = new PaymentMethods();
		PaymentMethod paymentMethod = new PaymentMethod();
		paymentMethod.setName(PaymentMethodName.SEPADIRECTDEBIT);
		paymentMethods.getPaymentMethod().add(paymentMethod);
		payment.setPaymentMethods(paymentMethods);

		payment.setTransactionType(TransactionType.PENDING_DEBIT);
		payment.setCreditorId(creditorId);
		payment.setRequestId(requestId);

		ObjectFactory objectFactory = new ObjectFactory();
		JAXBElement<Payment> jaxbElement = objectFactory.createPayment(payment);
		Payment result = client.httpClient.postEntity(client.url, Payment.class, jaxbElement);

		// TODO We will want to abstract this once we add support for other payment types
		WirecardPaymentResponse response = new WirecardPaymentResponse();
		response.setTransactionId(response.getTransactionId());
		response.setTransactionState(response.getTransactionState());
		response.setCompletionDate(response.getCompletionDate());
		response.setStatuses(result.getStatuses().getStatus().stream()
				.map(s -> (new WirecardPaymentStatus(s.getCode(), s.getDescription(), s.getSeverity().value())))
				.collect(Collectors.toCollection(ArrayList::new)));
		return response;
	}

}