package com.netradius.wirecard;

import lombok.Data;
import lombok.experimental.Accessors;
import com.netradius.wirecard.schema.*;

import java.math.BigDecimal;

import static com.netradius.wirecard.util.ValidationUtils.checkForNull;
import static com.netradius.wirecard.util.ValidationUtils.checkMaxLength;

/**
 * @author Dilip S Sisodia
 */
@Data
@Accessors(chain = true)
public class WirecardCreditCardPurchase extends WirecardCreditCardPayment<WirecardCreditCardPurchase> {

	protected WirecardCreditCardPurchase(WirecardClient client) {
		super(client);
	}

	//	Card Information
	protected String accountNumber;
	protected Short expirationMonth;
	protected Short expirationYear;
	protected String cardSecurityCode;
	protected String cardType;
	protected String cardLastFour;

	//	Account Holder Information
	protected String firstName;
	protected String lastName;
	protected String email;
	protected Gender gender;
	protected String dateOfBirth;
	protected String phone;
	protected String merchantCrmId;
	protected String socialSecurityNumber;

	//	Address
	protected String street1;
	protected String street2;
	protected String city;
	protected String state;
	protected String country;
	protected String postalCode;


	@Override
	public TransactionType getTransactionType() {
		return TransactionType.PURCHASE;
	}

	@Override
	public void validate() {
		super.validate();
		checkForNull(accountNumber);
		checkMaxLength(accountNumber, 36);
		checkForNull(expirationMonth);
		checkForNull(expirationYear);
		checkMaxLength(cardSecurityCode, 4);
		checkForNull(cardType);
		checkMaxLength(cardType, 15);
		checkMaxLength(cardLastFour, 4);
		checkForNull(firstName);
		checkForNull(lastName);
		checkMaxLength(firstName, 32);
		checkMaxLength(lastName, 32);
		checkMaxLength(email, 64);
		checkMaxLength(phone, 32);
		checkMaxLength(merchantCrmId, 64);
		checkMaxLength(socialSecurityNumber, 14);
		checkMaxLength(street1, 128);
		checkMaxLength(street2, 128);
		checkMaxLength(city, 32);
		checkMaxLength(state, 32);
		checkMaxLength(country, 3);
		checkMaxLength(postalCode, 16);
	}

	@Override
	public Payment getCreditCardPayment() {
		Payment payment = super.getCreditCardPayment();

		Address address = new Address();
		address.setStreet1(street1);
		address.setStreet2(street2);
		address.setCity(city);
		address.setState(state);
		address.setCountry(country);
		address.setPostalCode(postalCode);

		AccountHolder accountHolder = new AccountHolder();
		accountHolder.setAddress(address);
		accountHolder.setFirstName(firstName);
		accountHolder.setLastName(lastName);
		accountHolder.setEmail(email);
		accountHolder.setGender(gender);
		accountHolder.setDateOfBirth(dateOfBirth);
		accountHolder.setPhone(phone);
		accountHolder.setMerchantCrmId(merchantCrmId);
		accountHolder.setSocialSecurityNumber(socialSecurityNumber);

		payment.setAccountHolder(accountHolder);

		Money money = new Money();
		money.setCurrency(getCurrency());
		money.setValue(amount);
		payment.setRequestedAmount(money);

		Card card = new Card();
		card.setAccountNumber(accountNumber);
		card.setExpirationMonth(expirationMonth);
		card.setExpirationYear(expirationYear);
		card.setCardSecurityCode(cardSecurityCode);
		card.setCardType(CardType.valueOf(cardType));
		card.setCardSecurityCode(cardSecurityCode);

		payment.setCard(card);
		return payment;
	}

}
