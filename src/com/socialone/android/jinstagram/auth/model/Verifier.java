package com.socialone.android.jinstagram.auth.model;


import com.socialone.android.jinstagram.utils.Preconditions;

/**
 * Represents an OAuth verifier code.
 */
public class Verifier {
	private final String value;

	/**
	 * Default constructor.
	 * 
	 * @param value verifier value
	 */
	public Verifier(String value) {
		Preconditions.checkNotNull(value, "Must provide a valid string as verifier");

		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
