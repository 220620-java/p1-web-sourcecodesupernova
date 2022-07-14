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

import com.revature.p1SCS.web.service.ValidateData;

import org.mockito.InjectMocks;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class UserDelegateTest {
	@InjectMocks
	private UserDelegate userDelegate = new UserDelegate();
	
	@Mock
	private ValidateData v = new ValidateData();
	
	@Test
	public void validateValuesTest() {
		/*Local Variables*/
		List<String> values = new ArrayList<>();
		List<String> fields = new ArrayList<>();
		Boolean expected = true,
				actual;
		
		/*Variable Setup*/
		//values
		values.add("asdf");
		values.add("asdf");
		values.add("asdf");
		values.add("asdf");
		values.add("asdf");
		values.add("asdf");
		
		//fields
		fields.add("userid");
		fields.add("userpassword");
		fields.add("useremail");
		fields.add("userminit");
		fields.add("userlname");
		fields.add("userfname");
		
		/*Mocks*/
		Mockito.when(v.varcharType("", 0)).thenReturn(true);
		Mockito.when(v.integerType("")).thenReturn(true);
		Mockito.when(v.charType("", 0)).thenReturn(true);
		
		/*Function*/
		actual = userDelegate.validateValues(values, fields);
		
		/*Test*/
		Assertions.assertEquals(expected, actual);
	}
	
	@Test
	public void validateFieldsTest() {
		/*Local Variables*/
		List<String> fields = new ArrayList<>();

		/*Variable Setup*/
	}
	
	@Test
	public void validateArgsTest() {
		/*Local Variables*/
		List<String> fields = new ArrayList<>();
		List<String> args = new ArrayList<>();

		/*Variable Setup*/
	}
}
