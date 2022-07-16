package com.revature.p1SCS.web.delegates;

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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.revature.p1SCS.web.service.ValidateData;

import org.mockito.InjectMocks;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class TransactionDelegateTest {
	@InjectMocks
	private TransactionDelegate transactionDelegate = new TransactionDelegate();
	
	@Mock
	private ValidateData v = new ValidateData();
	
	/*Testing the method's recognition of field names*/
	@Test
	public void validateValuesRecognizeFields() {
		/*Local Variables*/
		List<String> values = new ArrayList<>();
		List<String> fields = new ArrayList<>();
		Boolean expected = true,
				actual;
		
		/*Variable Setup*/
		//values
		values.add("0");
		values.add("2000-01-01");
		values.add("asdf");
		values.add("0.0");
		values.add("0.0");
		values.add("asdf");
		values.add("0");
		
		//fields
		fields.add("transid");
		fields.add("transdate");
		fields.add("transtype");
		fields.add("transprebalance");
		fields.add("transpostbalance");
		fields.add("transnotes");
		fields.add("accountid");
		
		/*Mocks*/
		//NOTE: If the method calls the mock more than once with different params.
		//you will need to mock each combination
		Mockito.when(v.dateType("2000-01-01")).thenReturn(true);
		Mockito.when(v.textType("asdf")).thenReturn(true);
		Mockito.when(v.varcharType("asdf", 25)).thenReturn(true);
		Mockito.when(v.integerType("0")).thenReturn(true);
		Mockito.when(v.decimalType("0.0")).thenReturn(true);
		
		/*Function*/
		actual = transactionDelegate.validateValues(values, fields);
		
		/*Test*/
		Assertions.assertEquals(expected, actual);
	}
	
	/*Testing the method's recognition of field names*/
	@Test
	public void validateValuesInvalidField() {
		/*Local Variables*/
		List<String> values = new ArrayList<>();
		List<String> fields = new ArrayList<>();
		Boolean expected = false,
				actual;
		
		/*Variable Setup*/
		//values
		values.add("0");
		values.add("asdf");
		values.add("asdf");
		values.add("asdf");
		values.add("a");
		values.add("asdf");
		
		//fields
		fields.add("tid"); //Invalid Field
		fields.add("transdate");
		fields.add("transtype");
		fields.add("transprebalance");
		fields.add("transpostbalance");
		fields.add("transnotes");
		fields.add("accountid");
		
		/*Function*/
		actual = transactionDelegate.validateValues(values, fields);
		
		/*Test*/
		Assertions.assertEquals(expected, actual);
	}
	
	/*Testing the method's function for when input lists have different from each other*/
	@Test
	public void validateValuesSizeMismatch() {
		/*Local Variables*/
		List<String> values = new ArrayList<>();
		List<String> fields = new ArrayList<>();
		Boolean expected = false,
				actual;
		
		/*Variable Setup*/
		//values SIZE: 6
		values.add("0");
		values.add("asdf");
		values.add("asdf");
		values.add("asdf");
		values.add("a");
		values.add("asdf");
		
		//fields SIZE: 7
		fields.add("transid");
		fields.add("transdate");
		fields.add("transtype");
		fields.add("transprebalance");
		fields.add("transpostbalance");
		fields.add("transnotes");
		fields.add("accountid");
		
		/*Function*/
		actual = transactionDelegate.validateValues(values, fields);
		
		/*Test*/
		Assertions.assertEquals(expected, actual);
	}
	
	@Test
	public void validateFieldsTest() {
		/*Local Variables*/
		List<String> fields = new ArrayList<>();
		Boolean expected = true,
				actual;

		/*Variable Setup*/
		//fields
		fields.add("transid");
		fields.add("transdate");
		fields.add("transtype");
		fields.add("transprebalance");
		fields.add("transpostbalance");
		fields.add("transnotes");
		fields.add("accountid");
		
		/*Function*/
		actual = transactionDelegate.validateFields(fields);
		
		/*Test*/
		Assertions.assertEquals(expected, actual);
	}
	
	/*Testing the method's function for when input lists have different from each other*/
	@Test
	public void validateArgsSizeMismatch() {
		/*Local Variables*/
		List<String> args = new ArrayList<>();
		List<String> fields = new ArrayList<>();
		Boolean expected = false,
				actual;
		
		/*Variable Setup*/
		//args SIZE: 6
		args.add("=");
		args.add("=");
		args.add("=");
		args.add("=");
		args.add("=");
		args.add("=");
		
		//fields SIZE: 7
		fields.add("transid");
		fields.add("transdate");
		fields.add("transtype");
		fields.add("transprebalance");
		fields.add("transpostbalance");
		fields.add("transnotes");
		fields.add("accountid");
		
		/*Function*/
		actual = transactionDelegate.validateArgs(args, fields);
		
		/*Test*/
		Assertions.assertEquals(expected, actual);
	}
	
	/*Testing the method's recognition of valid arguments based on the given field*/
	@Test
	public void validateArgsRecognizeArgs() {
		/*Local Variables*/
		List<String> args = new ArrayList<>();
		List<String> fields = new ArrayList<>();
		Boolean expected = true,
				actual;
		
		/*Variable Setup*/
		//args
		args.add(">=");
		args.add("=");
		args.add("like");
		args.add("<=");
		args.add("<");
		args.add("LiKe");
		args.add(">");
		
		//fields
		fields.add("transid");
		fields.add("transdate");
		fields.add("transtype");
		fields.add("transprebalance");
		fields.add("transpostbalance");
		fields.add("transnotes");
		fields.add("accountid");
		
		/*Function*/
		actual = transactionDelegate.validateArgs(args, fields);
		
		/*Test*/
		Assertions.assertEquals(expected, actual);
	}
	
	/*Testing the method's recognition of valid arguments based on the given field*/
	@Test
	public void validateArgsInvalidArgs() {
		/*Local Variables*/
		List<String> args = new ArrayList<>();
		List<String> fields = new ArrayList<>();
		Boolean expected = false,
				actual;
		
		/*Variable Setup*/
		//args
		args.add("=");
		args.add("Like"); //Invalid for dates
		args.add("like");
		args.add("LiKe");
		args.add("=");
		args.add("=");
		args.add("=");
		
		//fields
		fields.add("transid");
		fields.add("transdate");
		fields.add("transtype");
		fields.add("transprebalance");
		fields.add("transpostbalance");
		fields.add("transnotes");
		fields.add("accountid");
		
		/*Function*/
		actual = transactionDelegate.validateArgs(args, fields);
		
		/*Test*/
		Assertions.assertEquals(expected, actual);
	}
}
