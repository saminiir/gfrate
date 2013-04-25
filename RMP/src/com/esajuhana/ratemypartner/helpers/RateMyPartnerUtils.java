package com.esajuhana.ratemypartner.helpers;

public class RateMyPartnerUtils {
	
	/**
	 * Adds leading + sign if number to positive values
	 * @param number number to be checked
	 * @return String representation of number
	 */
	public static String addLeadingPlusSign(int number) {
		if( number > 0 ) {
			return "+" + number;
		} else {
			return "" + number;
		}
			
	}

}
