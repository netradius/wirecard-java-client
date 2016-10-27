package com.netradius.wirecard;

import com.netradius.wirecard.schema.Payment;
import com.netradius.wirecard.schema.TransactionState;
import com.netradius.wirecard.schema.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Erik R. Jensen
 */
@Data
public class WirecardPaymentResponse {

	protected String merchantAccountId;
	protected String transactionId;
	protected String requestId;
	protected TransactionType transactionType;
	protected TransactionState transactionState;
	protected Date completionTimeStamp;
	protected List<WirecardPaymentStatus> statuses;
	protected BigDecimal requestedAmount;
	protected String currency;
	protected String paymentMethodName;
	protected String providerTransactionReferenceId;

	public boolean isSuccess() {
		return "success".equalsIgnoreCase(transactionState.value());
	}

	public static WirecardPaymentResponse parse(Payment payment) {
		WirecardPaymentResponse response = new WirecardPaymentResponse();
		response.merchantAccountId = payment.getMerchantAccountId().getValue();
		response.transactionId = payment.getTransactionId();
		response.requestId = payment.getRequestId();
		response.transactionType = payment.getTransactionType();
		response.transactionState = TransactionState.fromValue(payment.getTransactionState().value());
		if (payment.getCompletionTimeStamp() != null) {
			response.completionTimeStamp = payment.getCompletionTimeStamp().toGregorianCalendar().getTime();
		}
		response.requestedAmount = payment.getRequestedAmount().getValue();
		response.currency = payment.getRequestedAmount().getCurrency();
		response.providerTransactionReferenceId = payment.getProviderTransactionReferenceId();
		response.setStatuses(payment.getStatuses().getStatus().stream()
				.map(s -> (new WirecardPaymentStatus(s.getCode(), s.getDescription(), s.getSeverity().value())))
				.collect(Collectors.toCollection(ArrayList::new)));
		response.paymentMethodName = payment.getPaymentMethods().getPaymentMethod().get(0).getName().name();
		return response;
	}

}
