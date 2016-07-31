package com.netradius.wirecard;

/**
 * Created by anahar on 7/30/16.
 */
public enum WirecardTransactionState {
	SUCCESS("success"),
	FAILED("failed"),
	IN_PROGRESS("in-progress");

	private final String value;

	WirecardTransactionState(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static WirecardTransactionState fromValue(String v) {
		for (WirecardTransactionState c: WirecardTransactionState.values()) {
			if (c.value.equalsIgnoreCase(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}
}
