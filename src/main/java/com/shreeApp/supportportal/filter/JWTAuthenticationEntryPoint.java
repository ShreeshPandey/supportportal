package com.shreeApp.supportportal.filter;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static com.shreeApp.supportportal.constants.SecurityConstant.FORBIDDEN_MESSAGE;

import com.shreeApp.supportportal.domain.HttpResponse;

@Component
public class JWTAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException {
		
		HttpResponse httpResponse = new HttpResponse(FORBIDDEN.value(),
													 FORBIDDEN,
													 FORBIDDEN.getReasonPhrase().toUpperCase(),
													 FORBIDDEN_MESSAGE);
		
		response.setContentType(APPLICATION_JSON_VALUE);
		response.setStatus(FORBIDDEN.value());
		
		OutputStream outputStream = response.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		
		mapper.writeValue(outputStream, httpResponse);
		outputStream.flush();
		//response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
	}

}
