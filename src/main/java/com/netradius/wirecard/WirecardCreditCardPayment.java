package com.netradius.wirecard;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.netradius.wirecard.schema.*;

import javax.xml.bind.JAXBElement;

import static com.netradius.wirecard.util.ValidationUtils.checkForEmpty;
import static com.netradius.wirecard.util.ValidationUtils.checkForNull;
import static com.netradius.wirecard.util.ValidationUtils.checkMaxLength;

/**
 * @author Dilip S Sisodia
 */
@Data
public abstract class WirecardCreditCardPayment<T> implements Validated {

	protected WirecardClient client;

	/**
	 * Identification number created by the merchant. Must be unique for each request.
	 * This is a required field and limited to 64 characters.
	 */
	@Setter(AccessLevel.NONE)
	protected String requestId;

	/**
	 * This the amount of transaction
	 * This is a required field.
	 */
	@Setter(AccessLevel.NONE)
	protected BigDecimal amount;

	/*
	 * This is currency of requested amount
	 * This is required field.
	 */
	@Setter(AccessLevel.NONE)
	protected String currency;

	@Setter(AccessLevel.NONE)
	protected List<WirecardNotification> notifications;

	protected WirecardCreditCardPayment(final WirecardClient client) {
		this.client = client;
	}

	@SuppressWarnings("unchecked")
	public T setRequestId(String requestId) {
		this.requestId = requestId;
		return (T)this;
	}

	@SuppressWarnings("unchecked")
	public T setAmount(BigDecimal amount) {
		this.amount = amount;
		return (T)this;
	}

	@SuppressWarnings("unchecked")
	public T setCurrency(String currency) {
		this.currency = currency;
		return (T)this;
	}

	@SuppressWarnings("unchecked")
	public T setNotifications(List<WirecardNotification> notifications) {
		this.notifications = notifications;
		return (T)this;
	}

	@SuppressWarnings("unchecked")
	public T addNotification(WirecardNotification notification) {
		if (notifications == null) {
			notifications = new ArrayList<>(1);
		}
		notifications.add(notification);
		return (T)this;
	}

	public T addNotificationUrl(TransactionState transactionState, String url) {
		return addNotification(new WirecardNotification(transactionState, url));
	}

	public T addNotificationUrl(String url) {
		return addNotificationUrl(null, url);
	}

	public abstract TransactionType getTransactionType();

	public PaymentMethodName getPaymentMethodName() {
		return PaymentMethodName.CREDITCARD;
	}

	@Override
	public void validate() {
		checkForNull(getTransactionType());
		checkForEmpty(requestId);
		checkMaxLength(requestId, 64);
		checkForNull(amount);
		checkForNull(currency);
		checkMaxLength(currency, 3);
	}

	protected Payment getCreditCardPayment() {
		Payment payment = new Payment();
		MerchantAccountId merchantAccountId = new MerchantAccountId();
		merchantAccountId.setValue(client.merchantAccountId);
		payment.setMerchantAccountId(merchantAccountId);

		PaymentMethod paymentMethod = new PaymentMethod();
		paymentMethod.setName(getPaymentMethodName());
		PaymentMethods paymentMethods = new PaymentMethods();
		paymentMethods.getPaymentMethod().add(paymentMethod);
		payment.setPaymentMethods(paymentMethods);

		payment.setTransactionType(getTransactionType());
		payment.setRequestId(requestId);

		return payment;
	}

	public WirecardPaymentResponse submit() throws IOException {
		validate();
		Payment payment = getCreditCardPayment();
		ObjectFactory objectFactory = new ObjectFactory();
		JAXBElement<Payment> jaxbElement = objectFactory.createPayment(payment);
		Payment result = client.httpClient.postEntity(client.url, Payment.class, jaxbElement);
		return WirecardPaymentResponse.parse(result);
	}

}
