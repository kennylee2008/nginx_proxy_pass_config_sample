package com.futlabs.nlpp.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class NginxLocationProxypassController {

	@RequestMapping("/**")
	@ResponseBody
	public String request(HttpServletRequest request) {

		return request.getRequestURL() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
	}

}
