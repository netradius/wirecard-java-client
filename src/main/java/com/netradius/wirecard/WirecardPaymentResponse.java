package com.netradius.wirecard;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Erik R. Jensen
 */
@Data
public class WirecardPaymentResponse {

	private String transactionId;
	private String transactionState;
	private Date completionDate;
	private List<WirecardPaymentStatus> statuses;

	public boolean isSuccess() {
		return transactionState.equals("success");
	}

}
