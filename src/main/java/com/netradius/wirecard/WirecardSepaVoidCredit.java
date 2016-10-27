package com.netradius.wirecard;

import com.netradius.wirecard.schema.Payment;
import com.netradius.wirecard.schema.PaymentMethodName;
import com.netradius.wirecard.schema.TransactionType;
import lombok.Data;
import lombok.experimental.Accessors;

import static com.netradius.wirecard.util.ValidationUtils.checkForEmpty;
import static com.netradius.wirecard.util.ValidationUtils.checkMaxLength;

/**
 * @author Erik R. Jensen
 */
@Data
@Accessors(chain = true)
public class WirecardSepaVoidCredit extends WirecardSepaPayment<WirecardSepaVoidCredit> {

	/**
	 * Transaction ID of the first transaction of a payment.
	 * This field is optional and limited to 36 characters.
	 */
	protected String parentTransactionId;

	protected WirecardSepaVoidCredit(WirecardClient client) {
		super(client);
	}

	@Override
	public TransactionType getTransactionType() {
		return TransactionType.VOID_CREDIT;
	}

	@Override
	public PaymentMethodName getPaymentMethodName() {
		return PaymentMethodName.SEPACREDIT;
	}

	@Override
	public void validate() {
		super.validate();
		checkForEmpty(parentTransactionId);
		checkMaxLength(parentTransactionId, 36);
	}

	@Override
	protected Payment getSepaPayment() {
		Payment payment = super.getSepaPayment();
		payment.setParentTransactionId(parentTransactionId);
		return payment;
	}

}
