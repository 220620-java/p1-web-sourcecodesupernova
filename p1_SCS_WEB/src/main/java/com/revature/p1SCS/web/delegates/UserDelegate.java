package com.revature.p1SCS.web.delegates;

import java.util.ArrayList;
import java.util.List;

import java.io.PrintWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.p1SCS.orm.data.ORMDAO;
import com.revature.p1SCS.orm.models.Query;
import com.revature.p1SCS.web.service.ValidateData;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class UserDelegate implements ServletDelegate{
	/*Class Variables*/
	private Query in = new Query();
	private ORMDAO sql = new ORMDAO();
	private ObjectMapper objMapper = new ObjectMapper();
	private PrintWriter writer = null;
	private List<Query> getResult = null;
	private int result = -1;
	private String response = "";
	private List<String> validFieldNames = new ArrayList<>();
	
	/*Constructor*/
	public UserDelegate() {
		//Setup validFieldNames
		validFieldNames.add("userid");
		validFieldNames.add("useremail");
		validFieldNames.add("userpassword");
		validFieldNames.add("userfname");
		validFieldNames.add("userminit");
		validFieldNames.add("userlname");
	}

	/*Handles the request after validating its input*/
	@Override
	public void handle(HttpServletRequest req, HttpServletResponse resp) {
		try {
			Query in = objMapper.readValue(req.getInputStream(), Query.class);
			in.setTableName("tbl_users");
			writer = resp.getWriter();
			switch(req.getMethod()) {
			case "GET":
				getResult = sql.select(in);
				//Formatting the results of the sql
				if (getResult != null) {
					//Setting column names
					getResult.get(0).getFieldNameList().stream().forEach(x -> {
						response += "|\t" + x + "\t|";
					});
					
					//Setting values
					getResult.stream().forEach(x -> {
						response += "\n";
						for(String s : x.getFieldValueList()) {
							response += "|\t" + s + "\t|";
						}
					});
					writer.write(response);
				}
				break;
			case "POST":
				result = sql.insert(in);
				break;
			case "PUT":
				result = sql.update(in);
				break;
			case "DELETE":
				result = sql.delete(in);
				break;
			}
		}
		catch (Exception e) {
			//TODO Exception Logger
		}
		
	}
	
	protected Boolean validateFields(Query obj) {
		/*Local Variables*/
		Boolean valid = true;
		
		/*Function*/
		for (String s : obj.getFieldNameList()) {
			if (!validFieldNames.contains(s)) {
				valid = false;
				response += "\n\t" + s;
			}
		}
		if (!valid) {
			response = "The following fields are not valid in for users:" + response;
		}
		return valid;
	}
	
	/*Validates values by comparing to the corresponding field's data type*/
	protected Boolean validateValues(Query obj) {
		/*Local Variables*/
		Boolean valid = true;
		int index = 0;
		List<String> fields = obj.getFieldNameList();
		
		/*Function*/
		for (String s : obj.getFieldValueList()) {
			switch(fields.get(index)) {
			case "userid": //integer
				valid = ValidateData.integerType(s);
				break;
			case "useremail": //varchar(50)
				valid = ValidateData.varcharType(s, 50);
				break;
			case "userpassword": //varchar(30)
				valid = ValidateData.varcharType(s, 30);
				break;
			case "userfname": //varchar(25)
				valid = ValidateData.varcharType(s, 25);
				break;
			case "userminit": //char(1)
				valid = ValidateData.charType(s, 1);
				break;
			case "userlname": //varchar(25)
				valid = ValidateData.varcharType(s, 25);
				break;
			default:
				valid = false;
				break;				
			}
			index++;
		}
		return valid;
	}

}
