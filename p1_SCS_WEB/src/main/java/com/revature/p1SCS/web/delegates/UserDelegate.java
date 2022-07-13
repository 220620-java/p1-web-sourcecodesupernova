package com.revature.p1SCS.web.delegates;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.p1SCS.orm.models.Query;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class UserDelegate implements ServletDelegate{
	private Query in = new Query();
	private ObjectMapper objMapper = new ObjectMapper();
	private List<Query> getResult = new ArrayList<>();

	@Override
	public void handle(HttpServletRequest req, HttpServletResponse resp) {
		try {
			Query in = objMapper.readValue(req.getInputStream(), Query.class);
			switch(req.getMethod()) {
			case "GET":
				break;
			case "POST":
				break;
			case "PUT":
				break;
			case "DELETE":
				break;
			}
		}
		catch (Exception e) {
			//TODO Exception Logger
		}
		
	}

}
