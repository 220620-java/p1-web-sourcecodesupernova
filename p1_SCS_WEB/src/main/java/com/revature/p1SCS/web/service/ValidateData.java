package com.revature.p1SCS.web.service;

public class ValidateData {
	/*Data with the char type should have the same length as a given size
	 * Also, if the input contains a ';' it would break the SQL */
	public Boolean charType(String input, int size) {
		Boolean valid = true;
		if (input.contains(";") || input.length() != size) {
			valid = false;
		}
		return valid;
	}
	
	/*Data with the varchar type should have a length less than or equal to a given size
	 * Also, if the input contains a ';' it would break the SQL*/
	public Boolean varcharType(String input, int size) {
		Boolean valid = true;
		if (input.contains(";") || input.length() > size) {
			valid = false;
		}
		return valid;
	}
	
	/*Data with the text type has no set size limit
	 * But if the input contains a ';' it would still break the SQL*/
	public Boolean textType(String input) {
		Boolean valid = true;
		if (input.contains(";")) {
			valid = false;
		}
		return valid;
	}
	
	/*Data with the integer type should be able to parse into an integer*/
	public Boolean integerType(String input) {
		Boolean valid = true;
		try {
			Integer.parseInt(input);
		}
		catch (Exception e) {
			//TODO Exception Logger
			valid = false;
		}
		return valid;
	}
	
	/*Data with the double type should be able to parse into a double*/
	public Boolean decimalType(String input) {
		Boolean valid = true;
		try {
			Double.parseDouble(input);
		}
		catch (Exception e) {
			//TODO Exception Logger
			valid = false;
		}
		return valid;
	}
}
