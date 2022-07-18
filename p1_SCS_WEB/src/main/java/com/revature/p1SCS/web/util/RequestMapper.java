package com.revature.p1SCS.web.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.revature.p1SCS.web.delegates.AccountDelegate;
import com.revature.p1SCS.web.delegates.ServletDelegate;
import com.revature.p1SCS.web.delegates.TransactionDelegate;
import com.revature.p1SCS.web.delegates.UserDelegate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RequestMapper {
	/*Class Variables*/
	private Map<String, ServletDelegate> delegateMap = new HashMap<>();
	
	/*Constructor*/
	public RequestMapper() {
		delegateMap.put("users", new UserDelegate());
		delegateMap.put("accounts", new AccountDelegate());
		delegateMap.put("transactions", new TransactionDelegate());
	}
	
	/*Returns a delegate based on the given request's URI*/
	public ServletDelegate get(HttpServletRequest req, HttpServletResponse resp) {
		/*Local Variables*/
		StringBuilder uriString = new StringBuilder(req.getRequestURI());
		
		/*Function*/
		//Returns null delegate if method is OPTIONS
		if (req.getMethod().equals("OPTIONS")) {
			return (request, response) -> {};
		}
		
		//Removes context path from URI
		uriString.replace(0, req.getContextPath().length() + 1, "");
		
		//Removes Home/ from URI
		uriString.replace(0, 5, "");
		
		//Testing for presence of path parameters
		if(uriString.indexOf("/") != -1) {
			//Placing path parameters into a request attribute
			req.setAttribute("pathParam", uriString.substring(uriString.indexOf("/") + 1));
			
			//Removing path parameters
			uriString.replace(uriString.indexOf("/"), uriString.length(), "");
		}
		
		return delegateMap.get(uriString.toString());
	}
}
