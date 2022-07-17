package com.revature.p1SCS.web.delegates;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
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
	protected ValidateData v = new ValidateData();
	private ObjectMapper objMapper = new ObjectMapper();
	private PrintWriter writer = null;
	private List<Query> getResult = null;
	private int result = -1;
	private String response = "";
	private List<String> validFieldNames = new ArrayList<>(), validStrArgs = new ArrayList<>(),
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

		// Setup validStrArgs
		validStrArgs.add("LIKE");
		validStrArgs.add("=");

		// Setup validNumArgs
		validNumArgs.add(">");
		validNumArgs.add(">=");
		validNumArgs.add("<=");
		validNumArgs.add("<");
		validNumArgs.add("!=");
		validNumArgs.add("=");
	}

	/* Handles the request after validating its input */
	@Override
	public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			response = "";
			writer = resp.getWriter();
			in = objMapper.readValue(req.getInputStream(), Query.class);
			in.setTableName("tbl_users");
			switch (req.getMethod()) {
			case "GET": // Will be a select statement. Filter is optional.
				if (validateFields(in.getFieldNameList())) {
					if (validateFields(in.getFilterList()) || in.getFilterList().equals(new ArrayList<>())) {
						if (validateValues(in.getFilterValueList(), in.getFilterList())
								|| in.getFilterList().equals(new ArrayList<>())) {
							if (validateArgs(in.getArgumentTypes(), in.getFilterList())
									|| in.getFilterList().equals(new ArrayList<>())) {
								getResult = sql.select(in);

								// Formatting the results of the sql
								if (!getResult.equals(new ArrayList<Query>())) {
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
								else {
									response += "No results were found";
								}
							}
							else {
								response += "Invalid Arguments";
							}
						}
						else {
							response += "Invalid Filter Values";
						}
					}
					else {
						response += "Invalid Filters";
					}
				}
				else {
					response += "Invalid Fields";
				}
				break;
			case "POST":// Will be an insert statement. Filter is not used.
				if (validateFields(in.getFieldNameList())) {
					if (validateValues(in.getFieldValueList(), in.getFieldNameList())) {
						result = sql.insert(in);
						response += "Rows Affected: " + result;
					}
				}
				break;
			case "PUT":// Will be an update statement. Filter is mandatory.
				if (validateFields(in.getFieldNameList())) {
					if (validateFields(in.getFilterList())) {
						if (validateValues(in.getFieldValueList(), in.getFieldNameList())) {
							if (validateArgs(in.getArgumentTypes(), in.getFilterList())) {
								result = sql.delete(in);
								response += "Rows Affected: " + result;
							}
						}
					}
				}
				break;
			case "DELETE":// Will be an delete statement. Filter is mandatory. Fields are not used.
				if (validateFields(in.getFilterList())) {
					if (validateValues(in.getFilterValueList(), in.getFilterList())) {
						if (validateArgs(in.getArgumentTypes(), in.getFilterList())) {
							result = sql.delete(in);
							response += "Rows Affected: " + result;
						}
					}
				}
				break;
			}
			writer.write(response);
		} catch (Exception e) {
			e.printStackTrace();
			resp.sendError(400, e.getMessage());
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
	protected Boolean validateValues(List<String> values, List<String> fields) {
		/* Local Variables */
		Boolean valid = true;
		int index = 0;

		/* Function */
		if (values.stream().filter(x -> !(x.equals(""))).toArray().length == fields.stream()
				.filter(x -> !(x.equals(""))).toArray().length) {// Nonmatching sizes cause problems
			for (String s : values) {
				if (valid) {// One failure causes test to stop
					switch (fields.get(index)) {
					case "userid": // integer
						valid = v.integerType(s);
						break;
					case "useremail": // varchar(50)
						valid = v.varcharType(s, 50);
						break;
					case "userpassword": // varchar(30)
						valid = v.varcharType(s, 30);
						break;
					case "userfname": // varchar(25)
						valid = v.varcharType(s, 25);
						break;
					case "userminit": // char(1)
						valid = v.charType(s, 1);
						break;
					case "userlname": // varchar(25)
						valid = v.varcharType(s, 25);
						break;
					default:
						valid = false;
						break;
					}
					index++;
				}
			}
		} else {
			valid = false;
		}
		return valid;
	}

	/* Validates arguments by comparing to the corresponding field's data type */
	protected Boolean validateArgs(List<String> args, List<String> fields) {
		/* Local Variables */
		Boolean valid = true;
		int index = 0;

		/* Function */
		if (args.stream().filter(x -> !(x.equals(""))).toArray().length == fields.stream().filter(x -> !(x.equals("")))
				.toArray().length) {// Nonmatching sizes cause problems
			for (String s : args) {
				if (valid) {// One failure causes test to stop
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
			}
		} else {
			valid = false;
		}
		return valid;
	}

}
