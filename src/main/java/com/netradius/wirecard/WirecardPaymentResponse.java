package com.netradius.wirecard;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Erik R. Jensen
 */
@Data
public class WirecardPaymentResponse {

//	TODO
//	provider-transaction-reference-id
	private String providerTransactionReferenceId;
	private String transactionId;
	private WirecardTransactionState transactionState;
	private String transactionType;
	private Date completionDate;
	private List<WirecardPaymentStatus> statuses;

	public boolean isSuccess() {
		return transactionState.equals(WirecardTransactionState.SUCCESS);
	}

}
