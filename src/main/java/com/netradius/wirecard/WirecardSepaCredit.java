package com.netradius.wirecard;

import com.netradius.wirecard.schema.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.netradius.wirecard.util.ValidationUtils.*;

/**
 * @author Erik R. Jensen
 */
@Data
@Accessors(chain = true)
public class WirecardSepaCredit extends WirecardSepaPayment<WirecardSepaCredit> {

	/**
	 * Account holder's city.
	 * This field is optional and limited to 32 characters.
	 */
	protected String city;

	/**
	 * Account holder's country.
	 * This field is optional and should be the 3 letter ISO code.
	 */
	protected String country;

	/**
	 * Account holder's postal code.
	 * This field is optional and limited to 16 characters.
	 */
	protected String postalCode;

	/**
	 * The account's holder state.
	 * This is optional and limited to 32 characters.
	 */
	protected String state;

	/**
	 * The account's holder address street1.
	 * This is optional and limited to 70 characters.
	 */
	protected String street1;

	/**
	 * The account's holder email street2.
	 * This is optional and limited to 128 characters.
	 */
	protected String street2;

	/**
	 * The account's holder email dateOfBirth.
	 * This is optional.
	 */
	protected Date dateOfBirth;

	/**
	 * The account's holder email address.
	 * This is optional and limited to 64 characters.
	 */
	protected String email;

	/**
	 * Account holder's first name.
	 * This is a required field and limited to 32 characters.
	 */
	protected String firstName;

	/**
	 * Account holder's gender.
	 * This field is optional.
	 */
	protected Gender gender;

	/**
	 * Account holder's last name.
	 * This is a required field and limited to 32 characters.
	 */
	protected String lastName;

	/**
	 * The account's holder phone.
	 * This is optional and limited to 32 characters.
	 */
	protected String phone;

	/**
	 * The business identifier code of the bank.
	 * This is a required field and must be 8 or 11 characters.
	 */
	protected String bic;

	/**
	 * The bank account number.
	 * This is a required field and limited to 34 characters.
	 */
	protected String iban;

	/**
	 * Custom fields associated with this request.
	 */
	protected List<WirecardCustomField> customFields;

	/**
	 * Description on the settlement of the account holder’s account about a
	 * transaction. For SEPA Credit Transfer transactions, it will be combined
	 * with the Provider Transaction Reference ID and the merchant’s static
	 * descriptor and will appear on the consumer’s bank account statement.
	 * This field is optional and limited to 100 characters.
	 */
	protected String descriptor;

	/**
	 * The IP address of the consumer's computer.
	 * This field is optional and limited to 15 characters.
	 */
	protected String ipAddress;

	/**
	 * This is a field for details of an order filled by the merchant.
	 * This field is optional and limited to 65535 characters.
	 */
	protected String orderDetail;

	/**
	 * This is the order number of the merchant.
	 * This field is optional and limited to 64 characters.
	 */
	protected String orderNumber;

	/**
	 * Transaction ID of the first transaction of a payment.
	 * This field is optional and limited to 36 characters.
	 */
	protected String parentTransactionId;

	protected WirecardSepaCredit(WirecardClient client) {
		super(client);
	}

	@Override
	public TransactionType getTransactionType() {
		return TransactionType.PENDING_CREDIT;
	}

	@Override
	public PaymentMethodName getPaymentMethodName() {
		return PaymentMethodName.SEPACREDIT;
	}

	@Override
	public void validate() {
		super.validate();
		checkMaxLength(city, 32);
		checkMaxLength(country, 3);
		checkMaxLength(postalCode, 16);
		checkMaxLength(state, 32);
		checkMaxLength(street1, 70);
		checkMaxLength(street2, 128);
		checkForEmpty(firstName);
		checkMaxLength(firstName, 32);
		checkForEmpty(lastName);
		checkMaxLength(lastName, 32);
		checkMaxLength(phone, 32);
		checkLengthEquals(bic, 8, 11);
		checkMaxLength(iban, 34);
		checkMaxLength(descriptor, 100);
		checkMaxLength(ipAddress, 15);
		checkMaxLength(orderDetail, 65535);
		checkMaxLength(orderNumber, 64);
		checkMaxLength(parentTransactionId, 36);

		if (customFields != null && !customFields.isEmpty()) {
			for (WirecardCustomField wcf : customFields) {
				wcf.validate();
			}
		}
	}

	@Override
	protected Payment getSepaPayment() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Payment payment = super.getSepaPayment();

		AccountHolder accountHolder = new AccountHolder();
		payment.setAccountHolder(accountHolder);

		Address address = new Address();
		accountHolder.setAddress(address);
		address.setCity(city);
		address.setCountry(country);
		address.setPostalCode(postalCode);
		address.setState(state);
		address.setStreet1(street1);
		address.setStreet2(street2);

		if (dateOfBirth != null) {
			accountHolder.setDateOfBirth(sdf.format(dateOfBirth));
		}
		accountHolder.setEmail(email);
		accountHolder.setFirstName(firstName);
		accountHolder.setGender(gender);
		accountHolder.setLastName(lastName);
		accountHolder.setPhone(phone);

		BankAccount bankAccount = new BankAccount();
		payment.setBankAccount(bankAccount);
		bankAccount.setBic(bic);
		bankAccount.setIban(iban);

		if (customFields != null && !customFields.isEmpty()) {
			CustomFields customFields = new CustomFields();
			for (WirecardCustomField wcf : this.customFields) {
				customFields.getCustomField().add(wcf.getCustomField());
			}
		}

		payment.setIpAddress(ipAddress);
		payment.setOrderDetail(orderDetail);
		payment.setOrderNumber(orderNumber);
		payment.setParentTransactionId(parentTransactionId);

		return payment;
	}
}
