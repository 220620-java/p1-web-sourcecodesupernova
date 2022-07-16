package com.revature.p1SCS.web.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class ValidateDataTest {
	/*Class Variables*/
	private ValidateData valid = new ValidateData();
	
	@Test
	public void charTypeLengthMismatch() {
		/*Local Variables*/
		Boolean expected = false,
				actual;
		
		/*Function*/
		actual = valid.charType("12", 3);
		
		/*Test*/
		Assertions.assertEquals(expected, actual);
	}
	
	@Test
	public void charTypeSemicolon() {
		/*Local Variables*/
		Boolean expected = false,
				actual;
		
		/*Function*/
		actual = valid.charType(";", 1);
		

		/*Test*/
		Assertions.assertEquals(expected, actual);
	}
	
	@Test
	public void charTypePass() {
		/*Local Variables*/
		Boolean expected = true,
				actual;
		
		/*Function*/
		actual = valid.charType("12345", 5);

		/*Test*/
		Assertions.assertEquals(expected, actual);
	}
	
	@Test
	public void varcharTypeLengthMismatch() {
		/*Local Variables*/
		Boolean expected = false,
				actual;
		
		/*Function*/
		actual = valid.varcharType("1234", 3);
		
		/*Test*/
		Assertions.assertEquals(expected, actual);
	}
	
	@Test
	public void varcharTypeSemicolon() {
		/*Local Variables*/
		Boolean expected = false,
				actual;
		
		/*Function*/
		actual = valid.varcharType(";", 2);
		

		/*Test*/
		Assertions.assertEquals(expected, actual);
	}
	
	@Test
	public void varcharTypePass() {
		/*Local Variables*/
		Boolean expected = true,
				actual;
		
		/*Function*/
		actual = valid.varcharType("123", 5);

		/*Test*/
		Assertions.assertEquals(expected, actual);
	}
	
	@Test
	public void textTypeSemicolon() {
		/*Local Variables*/
		Boolean expected = false,
				actual;
		
		/*Function*/
		actual = valid.textType("Cant have this ---> ;");
		

		/*Test*/
		Assertions.assertEquals(expected, actual);
	}
	
	@Test
	public void textTypePass() {
		/*Local Variables*/
		Boolean expected = true,
				actual;
		
		/*Function*/
		actual = valid.textType("Any string at all would pass this as long as it does not have a semicolon");

		/*Test*/
		Assertions.assertEquals(expected, actual);
	}
	
	@Test
	public void integerTypeFail() {
		/*Local Variables*/
		Boolean expected = false,
				actual;
		
		/*Function*/
		actual = valid.integerType("0.01");

		/*Test*/
		Assertions.assertEquals(expected, actual);
	}
	
	@Test
	public void integerTypePass() {
		/*Local Variables*/
		Boolean expected = true,
				actual;
		
		/*Function*/
		actual = valid.integerType("42170"); //NOTE: It does not parse with commas. That is something we can change by filtering commas out

		/*Test*/
		Assertions.assertEquals(expected, actual);
	}
	
	@Test
	public void decimalTypeFail() {
		/*Local Variables*/
		Boolean expected = false,
				actual;
		
		/*Function*/
		actual = valid.decimalType("0.010.0");

		/*Test*/
		Assertions.assertEquals(expected, actual);
	}
	
	@Test
	public void decimalTypePass() {
		/*Local Variables*/
		Boolean expected = true,
				actual;
		
		/*Function*/
		actual = valid.decimalType("42170.42"); //NOTE: It does not parse with commas. That is something we can change by filtering commas out

		/*Test*/
		Assertions.assertEquals(expected, actual);
	}
	
	@Test
	public void dateTypeFail() {
		/*Local Variables*/
		Boolean expected = false,
				actual;
		
		/*Function*/
		actual = valid.dateType("01-01-2000"); //Only accepts YYYY-MM-DD. We could change that but it could be a bit complicated

		/*Test*/
		Assertions.assertEquals(expected, actual);
	}
	
	@Test
	public void dateTypePass() {
		/*Local Variables*/
		Boolean expected = true,
				actual;
		
		/*Function*/
		actual = valid.dateType("2000-1-1"); //Leading zeros are not always necessary

		/*Test*/
		Assertions.assertEquals(expected, actual);
	}
	
}
