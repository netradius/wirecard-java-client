package com.netradius.wirecard;

import com.netradius.wirecard.schema.*;

import java.math.BigDecimal;

import static com.netradius.wirecard.util.ValidationUtils.checkForNull;
import static com.netradius.wirecard.util.ValidationUtils.checkMaxLength;

/**
 * @author Dilip S Sisodia
 */
public class WirecardCreditCardRefundPurchase extends WirecardCreditCardPayment<WirecardCreditCardRefundPurchase> {

	protected String parentTransactionId;

	protected WirecardCreditCardRefundPurchase(WirecardClient client) {
		super(client);
	}

	@Override
	public void validate() {
		super.validate();
		checkForNull(parentTransactionId);
		checkMaxLength(parentTransactionId, 36);
	}

	@Override
	public TransactionType getTransactionType() {
		return TransactionType.REFUND_PURCHASE;
	}

}
