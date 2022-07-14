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

public class UserDelegate implements ServletDelegate {
	/* Class Variables */
	private Query in = new Query();
	private ORMDAO sql = new ORMDAO();
	private ObjectMapper objMapper = new ObjectMapper();
	private PrintWriter writer = null;
	private List<Query> getResult = null;
	private int result = -1;
	private String response = "";
	private List<String> validFieldNames = new ArrayList<>(),
			validStrArgs = new ArrayList<>(),
			validNumArgs = new ArrayList<>();;

	/* Constructor */
	public UserDelegate() {
		// Setup validFieldNames
		validFieldNames.add("userid");
		validFieldNames.add("useremail");
		validFieldNames.add("userpassword");
		validFieldNames.add("userfname");
		validFieldNames.add("userminit");
		validFieldNames.add("userlname");
		
		//Setup validStrArgs
		validStrArgs.add("LIKE");
		validStrArgs.add("=");
		
		//Setup validNumArgs
		validNumArgs.add(">");
		validNumArgs.add(">=");
		validNumArgs.add("<=");
		validNumArgs.add("<");
		validNumArgs.add("!=");
		validNumArgs.add("=");
		
	}

	/* Handles the request after validating its input */
	@Override
	public void handle(HttpServletRequest req, HttpServletResponse resp) {
		try {
			Query in = objMapper.readValue(req.getInputStream(), Query.class);
			in.setTableName("tbl_users");
			writer = resp.getWriter();
			switch (req.getMethod()) {
			case "GET": //Will be a select statement. Filter is optional.
				if (validateFields(in.getFieldNameList())) {
					if (validateFields(in.getFilterList()) || in.getFilterList().equals(new ArrayList<>())) {
						if (validateValues(in.getFieldValueList())) {
							if (validateValues(in.getFilterValueList()) || in.getFilterList().equals(new ArrayList<>())) {
								if (validateArgs(in.getArgumentTypes()) || in.getFilterList().equals(new ArrayList<>())) {
									getResult = sql.select(in);

									// Formatting the results of the sql
									if (getResult != null) {
										// Setting column names
											getResult.get(0).getFieldNameList().stream().forEach(x -> {
												response += "|\t" + x + "\t|";
											});

											// Setting values
											getResult.stream().forEach(x -> {
												response += "\n";
												for (String s : x.getFieldValueList()) {
													response += "|\t" + s + "\t|";
												}
											});
									}
								}
							}
						}
					}
				}
				break;
			case "POST"://Will be an insert statement. Filter is not used.
				if (validateFields(in.getFieldNameList())) {
					if (validateValues(in.getFieldValueList())) {
						result = sql.insert(in);
						response += "Rows Affected: " + result;
					}
				}
				break;
			case "PUT"://Will be an update statement. Filter is mandatory.
				if (validateFields(in.getFieldNameList())) {
					if (validateFields(in.getFilterList())) {
						if (validateValues(in.getFieldValueList())) {
							if (validateArgs(in.getArgumentTypes())) {
								result = sql.delete(in);
								response += "Rows Affected: " + result;
							}
						}
					}
				}
				break;
			case "DELETE"://Will be an delete statement. Filter is mandatory. Fields are not used.
				if (validateFields(in.getFilterList())) {
					if (validateValues(in.getFilterValueList())) {
						if (validateArgs(in.getArgumentTypes())) {
							result = sql.delete(in);
							response += "Rows Affected: " + result;
						}
					}
				}
				break;
			}
			writer.write(response);
		} catch (Exception e) {
			// TODO Exception Logger
		}

	}

	protected Boolean validateFields(List<String> fields) {
		/* Local Variables */
		Boolean valid = true;

		/* Function */
		for (String s : fields) {
			if (!validFieldNames.contains(s)) {
				valid = false;
				response += "\n\t" + s;
			}
		}
		if (!valid) {
			response = "The following fields are not valid for users:" + response;
		}
		return valid;
	}

	/* Validates values by comparing to the corresponding field's data type */
	protected Boolean validateValues(List<String> values) {
		/* Local Variables */
		Boolean valid = true;
		int index = 0;
		List<String> fields = in.getFieldNameList();

		/* Function */
		for (String s : values) {
			switch (fields.get(index)) {
			case "userid": // integer
				valid = ValidateData.integerType(s);
				break;
			case "useremail": // varchar(50)
				valid = ValidateData.varcharType(s, 50);
				break;
			case "userpassword": // varchar(30)
				valid = ValidateData.varcharType(s, 30);
				break;
			case "userfname": // varchar(25)
				valid = ValidateData.varcharType(s, 25);
				break;
			case "userminit": // char(1)
				valid = ValidateData.charType(s, 1);
				break;
			case "userlname": // varchar(25)
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
	
	/* Validates arguments by comparing to the corresponding field's data type */
	protected Boolean validateArgs(List<String> values) {
		/* Local Variables */
		Boolean valid = true;
		int index = 0;
		List<String> fields = in.getFilterList();

		/* Function */
		for (String s : values) {
			switch (fields.get(index)) {
			case "userid": // integer
				valid = validNumArgs.contains(s);
				break;
			case "useremail": // varchar(50)
				valid = validStrArgs.contains(s.toUpperCase());
				break;
			case "userpassword": // varchar(30)
				valid = validStrArgs.contains(s.toUpperCase());
				break;
			case "userfname": // varchar(25)
				valid = validStrArgs.contains(s.toUpperCase());
				break;
			case "userminit": // char(1)
				valid = validStrArgs.contains(s.toUpperCase());
				break;
			case "userlname": // varchar(25)
				valid = validStrArgs.contains(s.toUpperCase());
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
