package com.netradius.wirecard;

import com.netradius.wirecard.schema.MerchantAccountId;
import com.netradius.wirecard.schema.Money;
import com.netradius.wirecard.schema.Notifications;
import com.netradius.wirecard.schema.ObjectFactory;
import com.netradius.wirecard.schema.Payment;
import com.netradius.wirecard.schema.PaymentMethod;
import com.netradius.wirecard.schema.PaymentMethodName;
import com.netradius.wirecard.schema.PaymentMethods;
import com.netradius.wirecard.schema.TransactionState;
import com.netradius.wirecard.schema.TransactionType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;

import static com.netradius.wirecard.util.ValidationUtils.checkForEmpty;
import static com.netradius.wirecard.util.ValidationUtils.checkForNull;
import static com.netradius.wirecard.util.ValidationUtils.checkMaxLength;

/**
 * Represents a SEPA Payment transaction.
 *
 * @author Erik R. Jensen
 */
@Data
public abstract class WirecardSepaPayment<T> implements Validated {

  protected WirecardClient client;

  /**
   * Identification number created by the merchant. Must be unique for each request.
   * This is a required field and limited to 64 characters.
   */
  @Setter(AccessLevel.NONE)
  protected String requestId;

  /**
   * The amount in EUR.
   * This is a required field.
   */
  @Setter(AccessLevel.NONE)
  protected BigDecimal amount;

  @Setter(AccessLevel.NONE)
  protected List<WirecardNotification> notifications;

  protected WirecardSepaPayment(final WirecardClient client) {
    this.client = client;
  }

  @SuppressWarnings("unchecked")
  public T setRequestId(String requestId) {
    this.requestId = requestId;
    return (T) this;
  }

  @SuppressWarnings("unchecked")
  public T setAmount(BigDecimal amount) {
    this.amount = amount;
    return (T) this;
  }

  @SuppressWarnings("unchecked")
  public T setNotifications(List<WirecardNotification> notifications) {
    this.notifications = notifications;
    return (T) this;
  }

  @SuppressWarnings("unchecked")
  public T addNotification(WirecardNotification notification) {
    if (notifications == null) {
      notifications = new ArrayList<>(1);
    }
    notifications.add(notification);
    return (T) this;
  }

  public T addNotificationUrl(TransactionState transactionState, String url) {
    return addNotification(new WirecardNotification(transactionState, url));
  }

  public T addNotificationUrl(String url) {
    return addNotificationUrl(null, url);
  }

  public abstract TransactionType getTransactionType();

  public abstract PaymentMethodName getPaymentMethodName();

  public void validate() {
    // Validate arguments
    checkForNull(getTransactionType());
    checkForEmpty(requestId);
    checkMaxLength(requestId, 64);
    checkForNull(amount);

    if (notifications != null && !notifications.isEmpty()) {
      for (WirecardNotification notification : notifications) {
        notification.validate();
      }
    }
  }

  protected Payment getSepaPayment() {

    Payment payment = new Payment();

    // Set the merchant ID
    MerchantAccountId merchantAccountId = new MerchantAccountId();
    merchantAccountId.setValue(client.merchantAccountId);
    payment.setMerchantAccountId(merchantAccountId);

    // Set the payment method
    PaymentMethod paymentMethod = new PaymentMethod();
    paymentMethod.setName(getPaymentMethodName());
    PaymentMethods paymentMethods = new PaymentMethods();
    paymentMethods.getPaymentMethod().add(paymentMethod);
    payment.setPaymentMethods(paymentMethods);

    // Set the amount
    Money money = new Money();
    money.setCurrency("EUR");
    money.setValue(amount);
    payment.setRequestedAmount(money);

    // Set the transaction type
    payment.setTransactionType(getTransactionType());

    // Set the request ID
    payment.setRequestId(requestId);

    // Set notification URLs
    if (notifications != null && !notifications.isEmpty()) {
      Notifications notifications = new Notifications();
      for (WirecardNotification wirecardNotification : this.notifications) {
        notifications.getNotification().add(wirecardNotification.getNotification());
      }
      payment.setNotifications(notifications);
    }

    return payment;
  }

  public WirecardPaymentResponse submit() throws IOException {
    validate();
    Payment payment = getSepaPayment();
    ObjectFactory objectFactory = new ObjectFactory();
    JAXBElement<Payment> jaxbElement = objectFactory.createPayment(payment);
    Payment result = client.httpClient.postEntity(client.url, Payment.class, jaxbElement);
    return WirecardPaymentResponse.parse(result);
  }

}
