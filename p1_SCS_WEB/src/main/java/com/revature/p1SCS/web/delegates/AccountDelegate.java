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
	public AccountDelegate() {
		// Setup validFieldNames
		validFieldNames.add("accountid");
		validFieldNames.add("accounttype");
		validFieldNames.add("accountnotes");
		validFieldNames.add("accountbalance");
		validFieldNames.add("userid");

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
	public void handle(HttpServletRequest req, HttpServletResponse resp) {
		try {
			writer = resp.getWriter();
			in = objMapper.readValue(req.getInputStream(), Query.class);
			in.setTableName("tbl_accounts");
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
					case "accountid": // integer
						valid = v.integerType(s);
						break;
					case "accounttype":
						valid = v.varcharType(s,25);
						break;
					case "accountbalance": // decimal
						valid = v.decimalType(s);
						break;
					case "accountnotes": // text
						valid = v.textType(s);
						break;
					case "userid": // integer
						valid = v.integerType(s);
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
					case "accountid": // integer
						valid = validNumArgs.contains(s);
						break;
					case "accounttype":
						valid = validStrArgs.contains(s.toUpperCase());
						break;
					case "accountbalance": // decimal
						valid = validNumArgs.contains(s);
						break;
					case "accountnotes": // text
						valid = validStrArgs.contains(s.toUpperCase());
						break;
					case "userid": // integer
						valid = validNumArgs.contains(s);
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
