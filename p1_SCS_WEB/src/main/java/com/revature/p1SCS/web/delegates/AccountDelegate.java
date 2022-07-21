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

public class AccountDelegate implements ServletDelegate {
	/* Class Variables */
	private Query in = new Query(),
			validateFK = new Query();
	private ORMDAO sql = new ORMDAO();
	protected ValidateData v = new ValidateData();
	private ObjectMapper objMapper = new ObjectMapper();
	private PrintWriter writer = null;
	private List<Query> getResult = null;
	private int result = -1;
	private String response = "";
	private List<String> nullList = new ArrayList<>(), validFieldNames = new ArrayList<>(), validStrArgs = new ArrayList<>(),
			reqFieldNames = new ArrayList<>(), validNumArgs = new ArrayList<>(), 
			fkField = new ArrayList<>(), fkValue = new ArrayList<>(), fkArgs = new ArrayList<>();

	/* Constructor */
	public AccountDelegate() {
		// Setup validFieldNames
		validFieldNames.add("accountid");
		validFieldNames.add("accounttype");
		validFieldNames.add("accountnotes");
		validFieldNames.add("accountbalance");
		validFieldNames.add("userid");
		
		//Setup reqFieldNames
		reqFieldNames.add("accounttype");
		reqFieldNames.add("accountbalance");
		reqFieldNames.add("userid");

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
		
		//Setup fkField
		fkField.add("userid");
		
		//Setup fkArgs
		fkArgs.add("=");
	}

	/** Handles the request based on the given verb (i.e. GET, POST, PUT, DELETE, etc.)
	 * after validating its input 
	 * 
	 * @param req (HttpServletRequest): An object representing a HTTP request
	 * @param resp (HttpServletResponse): An object representing a HTTP response*/
	@Override
	public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			/*Variable Setup*/
			validateFK = new Query();
			validateFK.setTableName("tbl_users");
			fkValue = new ArrayList<>();
			in = new Query();
			response = "";
			writer = resp.getWriter();
			if (!(req.getInputStream().isFinished())) {// Reads the body as a JSON object if there is a body to read
				in = objMapper.readValue(req.getInputStream(), Query.class);
			}
			in.setTableName("tbl_accounts");
			in.setKeys(new String[] { "accountid" });
			
			/*Function*/
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
			case "POST":// Will be an insert statement. Filter is not used. accounttype, accountbalance, and userid
						// are required.
				if (in.getFieldNameList().equals(nullList)) {// Will return all fields if none are given
					in.setFieldNameList(validFieldNames);
				}
				if (validateFields(in.getFieldNameList())) {
					if (in.getFieldNameList().containsAll(reqFieldNames)) {// Testing for presence of required fields
						if (validateValues(in.getFieldValueList(), in.getFieldNameList())) {
							//Reference validation Setup
							fkValue.add(in.getFieldValueList().get(in.getFieldNameList().indexOf("userid")));
							validateFK.setFieldNameList(fkField);
							validateFK.setFilterList(fkField);
							validateFK.setArgumentTypes(fkArgs);
							validateFK.setFilterValueList(fkValue);
							if (!sql.select(validateFK).equals(new ArrayList<>())) {
								result = sql.insert(in);
								if (result != -1) {
									response += "POST successful: \nRows Affected: " + result;
								} else {
									resp.sendError(500, "The server failed to process your request");
								}
							} else {
								resp.sendError(400, "Invalid Values: \n'" 
										+ in.getFieldValueList().get(in.getFieldNameList().indexOf("userid"))
										+ "' does not exist in the users table");
							}
						} else {
							resp.sendError(400, "Invalid Values: \n" + response);
						}
					} else {
						resp.sendError(400,
								"Invalid Fields: \naccounttype, accountbalance, and userid are required for this request."
										+ " Your request is missing one or more of these.");
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

	/** Validates the fields of the input by cross-referencing
	 * them with the validFieldNames variable
	 * 
	 * Will append to the response variable if the input does not pass validation
	 * 
	 * @param fields (List<String>): A list of field names to be checked for validation
	 * @return A Boolean representing the input's validity
	 * */
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
			response = "The following fields are not valid for accounts:" + response;
		}
		return valid;
	}
	
	/** Validates the values of the input by cross-referencing
	 * them with the data types of the fields variable. 
	 * 
	 * Data is validated through the ValidateData class. 
	 * 
	 * Lists with differing sizes will also return as invalid. 
	 * 
	 * Will append to the response variable if the input does not pass validation. 
	 * 
	 * This method is intended to be called only after the fields have been validated using
	 * validateFields(), so it will return invalid if the field is not recognized.
	 * 
	 * @param values (List<String>): A list of values to be checked for validation
	 * @param fields (List<String>): A list of field names to be referenced during validation
	 * @return A Boolean representing the input's validity
	 * */
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
					case "accountid": // integer
						valid = v.integerType(s);
						datatype = "integer";
						break;
					case "accounttype":// varchar(25)
						valid = v.varcharType(s,25);
						datatype = "varchar(25)";
						break;
					case "accountbalance": // decimal
						valid = v.decimalType(s);
						datatype = "decimal";
						break;
					case "accountnotes": // text
						valid = v.textType(s);
						datatype = "text";
						break;
					case "userid": // integer
						valid = v.integerType(s);
						datatype = "integer";
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

	/** Validates the arguments of the input by cross-referencing
	 * them with the data types of the fields variable. 
	 * 
	 * Lists with differing sizes will also return as invalid. 
	 * 
	 * Will append to the response variable if the input does not pass validation. 
	 * 
	 * This method is intended to be called only after the fields have been validated using
	 * validateFields(), so it will return invalid if the field is not recognized.
	 * 
	 * @param args (List<String>): A list of arguments to be checked for validation
	 * @param fields (List<String>): A list of field names to be referenced during validation
	 * @return A Boolean representing the input's validity
	 * */
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
					case "accountid": // integer
						valid = validNumArgs.contains(s);
						datatype = "integer";
						break;
					case "accounttype":// varchar(25)
						valid = validStrArgs.contains(s.toUpperCase());
						datatype = "varchar(25)";
						break;
					case "accountbalance": // decimal
						valid = validNumArgs.contains(s);
						datatype = "decimal";
						break;
					case "accountnotes": // text
						valid = validStrArgs.contains(s.toUpperCase());
						datatype = "text";
						break;
					case "userid": // integer
						valid = validNumArgs.contains(s);
						datatype = "integer";
						break;
					default:
						valid = false;
						break;
					}
					if (!valid) {// Appends a response for the invalid input
						response += "'" + s + "' is an invalid argument for the data type of " + fields.get(index) + ": "
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

}
