package com.netradius.wirecard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Erik R. Jensen
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WirecardPaymentStatus {

	private String code;
	private String description;
	private String severity;

}
