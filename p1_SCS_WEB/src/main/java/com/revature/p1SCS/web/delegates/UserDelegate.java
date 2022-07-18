package com.revature.p1SCS.web.delegates;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;

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
	private List<String> nullList = new ArrayList<>(), validFieldNames = new ArrayList<>(),
			reqFieldNames = new ArrayList<>(), validStrArgs = new ArrayList<>(), validNumArgs = new ArrayList<>();

	/* Constructor */
	public UserDelegate() {
		// Setup validFieldNames
		validFieldNames.add("userid");
		validFieldNames.add("useremail");
		validFieldNames.add("userpassword");
		validFieldNames.add("userfname");
		validFieldNames.add("userminit");
		validFieldNames.add("userlname");

		// Setup reqFieldNames
		reqFieldNames.add("useremail");
		reqFieldNames.add("userpassword");

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
			in = new Query();
			response = "";
			writer = resp.getWriter();
			if (!(req.getInputStream().isFinished())) {// Reads the body as a JSON object if there is a body to read
				in = objMapper.readValue(req.getInputStream(), Query.class);
			}
			in.setTableName("tbl_users");
			in.setKeys(new String[] { "userid" });
			switch (req.getMethod()) {
			case "GET": // Will be a select statement. Filter is optional.
				if (in.getFieldNameList().equals(nullList)) {// Will return all fields if none are given
					in.setFieldNameList(validFieldNames);
				}
				if (validateFields(in.getFieldNameList())) {
					if (validateFields(in.getFilterList()) || in.getFilterList().equals(nullList)) {
						if (validateValues(in.getFilterValueList(), in.getFilterList())
								|| in.getFilterList().equals(nullList)) {
							if (validateArgs(in.getArgumentTypes(), in.getFilterList())
									|| in.getFilterList().equals(nullList)) {
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
								} else {
									response += "No results were found";
								}
							} else {
								resp.sendError(400, "Invalid Arguments: \n" + response);
							}
						} else {
							resp.sendError(400, "Invalid Filter Values: \n" + response);
						}
					} else {
						resp.sendError(400, "Invalid Filters: \n" + response);
					}
				} else {
					resp.sendError(400, "Invalid Fields: \n" + response);
				}
				break;
			case "POST":// Will be an insert statement. Filter is not used. useremail and userpassword
						// are required.
				if (in.getFieldNameList().equals(nullList)) {// Will return all fields if none are given
					in.setFieldNameList(validFieldNames);
				}
				if (validateFields(in.getFieldNameList())) {
					if (in.getFieldNameList().containsAll(reqFieldNames)) {// Testing for presence of required fields
						if (validateValues(in.getFieldValueList(), in.getFieldNameList())) {
							result = sql.insert(in);
							if (result != -1) {
								response += "POST successful: \nRows Affected: " + result;
							} else {
								resp.sendError(500, "The server failed to process your request");
							}
						} else {
							resp.sendError(400, "Invalid Values: \n" + response);
						}
					} else {
						resp.sendError(400,
								"Invalid Fields: \nuseremail and userpassword are required for this request."
										+ " Your request is missing one or both of them.");
					}
				} else {
					resp.sendError(400, "Invalid Fields: \n" + response);
				}
				break;
			case "PUT":// Will be an update statement. At least one field and filter is mandatory.
				if (validateFields(in.getFieldNameList()) && !(in.getFieldNameList().equals(nullList))) {
					if (validateFields(in.getFilterList()) && !(in.getFilterList().equals(nullList))) {
						if (validateValues(in.getFieldValueList(), in.getFieldNameList())) {
							if (validateValues(in.getFilterValueList(), in.getFilterList())) {
								if (validateArgs(in.getArgumentTypes(), in.getFilterList())) {
									result = sql.update(in);
									if (result != -1) {
										response += "PUT successful: \nRows Affected: " + result;
									} else {
										resp.sendError(500, "The server failed to process your request");
									}
								} else {
									resp.sendError(400, "Invalid Arguments: \n" + response);
								}
							} else {
								resp.sendError(400, "Invalid Filter Values: \n" + response);
							}
						} else {
							resp.sendError(400, "Invalid Update Values: \n" + response);
						}
					} else {
						if (in.getFilterList().equals(nullList)) {
							resp.sendError(400, "Invalid Filters: \nAt least one filter is required to update");
						} else {
							resp.sendError(400, "Invalid Filters: \n" + response);
						}
					}
				} else {
					if (in.getFieldNameList().equals(nullList)) {
						resp.sendError(400, "Invalid Fields: \nAt least one field is required to update");
					} else {
						resp.sendError(400, "Invalid Fields: \n" + response);
					}
				}
				break;
			case "DELETE":// Will be an delete statement. Filter is mandatory. Fields are not used.
				if (validateFields(in.getFilterList()) && !(in.getFilterList().equals(nullList))) {
					if (validateValues(in.getFilterValueList(), in.getFilterList())) {
						if (validateArgs(in.getArgumentTypes(), in.getFilterList())) {
							result = sql.delete(in);
							if (result != -1) {
								response += "DELETE successful: \nRows Affected: " + result;
							} else {
								resp.sendError(500, "The server failed to process your request");
							}
						} else {
							resp.sendError(400, "Invalid Arguments: \n" + response);
						}
					} else {
						resp.sendError(400, "Invalid Filter Values: \n" + response);
					}
				} else {
					if (in.getFilterList().equals(nullList)) {
						resp.sendError(400, "Invalid Filters: \nAt least one filter is required to update");
					} else {
						resp.sendError(400, "Invalid Filters: \n" + response);
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
		String datatype = "";

		/* Function */
		if (values.stream().filter(x -> !(x.equals(""))).toArray().length == fields.stream()
				.filter(x -> !(x.equals(""))).toArray().length) {// Nonmatching sizes cause problems
			for (String s : values) {
				if (valid) {// One failure causes test to stop
					switch (fields.get(index)) {
					case "userid": // integer
						valid = v.integerType(s);
						datatype = "integer";
						break;
					case "useremail": // varchar(50)
						valid = v.varcharType(s, 50);
						datatype = "varchar(50)";
						break;
					case "userpassword": // varchar(30)
						valid = v.varcharType(s, 30);
						datatype = "varchar(30)";
						break;
					case "userfname": // varchar(25)
						valid = v.varcharType(s, 25);
						datatype = "varchar(25)";
						break;
					case "userminit": // char(1)
						valid = v.charType(s, 1);
						datatype = "char(1)";
						break;
					case "userlname": // varchar(25)
						valid = v.varcharType(s, 25);
						datatype = "varchar(25)";
						break;
					default:
						valid = false;
						break;
					}
					if (!valid) {// Appends a response for the invalid input
						response += "'" + s + "' is an invalid value for the data type of " + fields.get(index) + ": "
								+ datatype;
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
		String datatype = "";

		/* Function */
		if (args.stream().filter(x -> !(x.equals(""))).toArray().length == fields.stream().filter(x -> !(x.equals("")))
				.toArray().length) {// Nonmatching sizes cause problems
			for (String s : args) {
				if (valid) {// One failure causes test to stop
					switch (fields.get(index)) {
					case "userid": // integer
						valid = validNumArgs.contains(s);
						datatype = "varchar(50)";
						break;
					case "useremail": // varchar(50)
						valid = validStrArgs.contains(s.toUpperCase());
						datatype = "varchar(50)";
						break;
					case "userpassword": // varchar(30)
						valid = validStrArgs.contains(s.toUpperCase());
						datatype = "varchar(50)";
						break;
					case "userfname": // varchar(25)
						valid = validStrArgs.contains(s.toUpperCase());
						datatype = "varchar(50)";
						break;
					case "userminit": // char(1)
						valid = validStrArgs.contains(s.toUpperCase());
						datatype = "varchar(50)";
						break;
					case "userlname": // varchar(25)
						valid = validStrArgs.contains(s.toUpperCase());
						datatype = "varchar(50)";
						break;
					default:
						valid = false;
						break;
					}
					if (!valid) {// Appends a response for the invalid input
						response += "'" + s + "' is an invalid argument for the data type of " + fields.get(index)
								+ ": " + datatype;
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
