package com.netradius.wirecard;

import com.netradius.wirecard.schema.AccountHolder;
import com.netradius.wirecard.schema.BankAccount;
import com.netradius.wirecard.schema.Payment;
import com.netradius.wirecard.schema.PaymentMethodName;
import com.netradius.wirecard.schema.TransactionType;
import lombok.Data;
import lombok.experimental.Accessors;

import static com.netradius.wirecard.util.ValidationUtils.checkForEmpty;
import static com.netradius.wirecard.util.ValidationUtils.checkLengthEquals;
import static com.netradius.wirecard.util.ValidationUtils.checkMaxLength;

/**
 * Represents a SEPA Authorization request is used to validate information. A transaction
 * with type authorization is not sent on for further processing to the provider.
 *
 * @author Erik R. Jensen
 */
@Data
@Accessors(chain = true)
public class WirecardSepaAuth extends WirecardSepaPayment<WirecardSepaAuth> {

  /**
   * Account holder's first name.
   * This is a required field and limited to 32 characters.
   */
  protected String firstName;

  /**
   * Account holder's last name.
   * This is a required field and limited to 32 characters.
   */
  protected String lastName;

  /**
   * The business identifier code of the bank.
   * This is optional and must be 8 or 11 characters.
   */
  protected String bic;

  /**
   * The bank account number.
   * This is optional and limited to 34 characters.
   */
  protected String iban;

  /**
   * Transaction ID of the first transaction of a payment.
   * This field is optional and limited to 36 characters.
   */
  protected String parentTransactionId;

  /**
   * Creates a new SEPA Authorization request.
   *
   * @param client the client to use
   */
  protected WirecardSepaAuth(final WirecardClient client) {
    super(client);
  }

  @Override
  public TransactionType getTransactionType() {
    return TransactionType.AUTHORIZATION;
  }

  @Override
  public PaymentMethodName getPaymentMethodName() {
    return PaymentMethodName.SEPADIRECTDEBIT;
  }

  @Override
  public void validate() {
    super.validate();
    checkForEmpty(firstName);
    checkMaxLength(firstName, 32);
    checkForEmpty(lastName);
    checkMaxLength(lastName, 32);
    checkLengthEquals(bic, 8, 11);
    checkMaxLength(iban, 34);
    checkMaxLength(parentTransactionId, 36);
  }

  @Override
  protected Payment getSepaPayment() {
    Payment payment = super.getSepaPayment();

    AccountHolder accountHolder = new AccountHolder();
    accountHolder.setFirstName(firstName);
    accountHolder.setLastName(lastName);
    payment.setAccountHolder(accountHolder);

    BankAccount bankAccount = new BankAccount();
    bankAccount.setBic(bic);
    bankAccount.setIban(iban);
    payment.setBankAccount(bankAccount);

    return payment;
  }

}
