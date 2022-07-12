package com.revature.p1SCS.web.servlets;

import java.io.IOException;

import org.apache.catalina.servlets.DefaultServlet;

import com.revature.p1SCS.web.delegates.ServletDelegate;
import com.revature.p1SCS.web.util.RequestMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontServlet extends DefaultServlet{
	/*Class Variables*/
	private RequestMapper mapper = new RequestMapper();
	
	private void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		ServletDelegate delegate = mapper.get(req, resp);
		if (delegate != null) {
			delegate.handle(req, resp);
		}
		else {
			resp.sendError(404);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req,resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req,resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req,resp);
	}	
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req,resp);
	}
	
	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req,resp);
	}
	
}
