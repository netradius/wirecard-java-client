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
public class WirecardCreditCardCapture extends WirecardCreditCardPayment<WirecardCreditCardCapture> {

	protected String parentTransactionId;

	protected WirecardCreditCardCapture(WirecardClient client) {
		super(client);
	}

	@Override
	public TransactionType getTransactionType() {
		return TransactionType.CAPTURE_AUTHORIZATION;
	}

	@Override
	public void validate() {
		super.validate();
		checkForNull(parentTransactionId);
		checkMaxLength(parentTransactionId, 36);
	}

	@Override
	protected Payment getCreditCardPayment() {
		Payment payment = super.getCreditCardPayment();
		payment.setParentTransactionId(parentTransactionId);

		Money money = new Money();
		money.setValue(amount);
		money.setCurrency(getCurrency());
		payment.setRequestedAmount(money);

		return payment;
	}

}
