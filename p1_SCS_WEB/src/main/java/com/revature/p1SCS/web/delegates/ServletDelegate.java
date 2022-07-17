package com.revature.p1SCS.web.delegates;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface ServletDelegate {
	public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException;
}
