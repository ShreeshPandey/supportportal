package com.shreeApp.supportportal.exception.domain;

public class UserNotFoundException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public UserNotFoundException(String message) {
		super(message);		
	}

	public UserNotFoundException(String message, Throwable cause) {
		super(message, cause);		
	}
}
