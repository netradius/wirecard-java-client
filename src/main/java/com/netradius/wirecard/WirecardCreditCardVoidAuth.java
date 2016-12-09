package com.netradius.wirecard;

import com.netradius.wirecard.schema.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

import static com.netradius.wirecard.util.ValidationUtils.checkForNull;
import static com.netradius.wirecard.util.ValidationUtils.checkMaxLength;

/**
 * @author Dilip S Sisodia
 */
@Data
@Accessors(chain = true)
public class WirecardCreditCardVoidAuth extends WirecardCreditCardPayment<WirecardCreditCardVoidAuth> {

	protected String parentTransactionId;

	protected WirecardCreditCardVoidAuth(WirecardClient client) {
		super(client);
	}

	@Override
	public TransactionType getTransactionType() {
		return TransactionType.VOID_AUTHORIZATION;
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
		return payment;
	}
}
