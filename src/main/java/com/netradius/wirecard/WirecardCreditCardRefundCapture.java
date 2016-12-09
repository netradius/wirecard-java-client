package com.netradius.wirecard;

import com.netradius.wirecard.schema.*;

import java.math.BigDecimal;

import static com.netradius.wirecard.util.ValidationUtils.checkForNull;
import static com.netradius.wirecard.util.ValidationUtils.checkMaxLength;

/**
 * @author Dilip S Sisodia
 */
public class WirecardCreditCardRefundCapture extends WirecardCreditCardPayment<WirecardCreditCardRefundCapture> {

	protected String parentTransactionId;

	protected WirecardCreditCardRefundCapture(WirecardClient client) {
		super(client);
	}

	@Override
	public TransactionType getTransactionType() {
		return TransactionType.REFUND_CAPTURE;
	}

	@Override
	public void validate() {
		super.validate();
		checkForNull(parentTransactionId);
		checkMaxLength(parentTransactionId, 36);
	}

	@Override
	public Payment getCreditCardPayment() {
		Payment payment = super.getCreditCardPayment();

		payment.setParentTransactionId(parentTransactionId);

		Money money = new Money();
		money.setCurrency(getCurrency());
		money.setValue(amount);
		payment.setRequestedAmount(money);

		return payment;
	}
}
