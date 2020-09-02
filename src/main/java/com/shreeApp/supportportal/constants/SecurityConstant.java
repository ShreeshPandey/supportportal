package com.shreeApp.supportportal.constants;

public class SecurityConstant {

	public static final long EXPIRATION_TIME = 432_000_000; //5 Days in milliseconds
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String JWT_TOKEN_HEADER = "Jwt-Token";
	public static final String TOKEN_CANNOT_BE_VERIFIED = "Token Cannot Be Verified";
	public static final String GET_ARRAYS_LLC = "Shree Technologies,LLC";
	public static final String GET_ARRAYS_ADMINISTRATION = "User Management Portal";
	public static final String AUTHORITIES = "authorities";
	public static final String FORBIDDEN_MESSAGE = "You need to Login to access this Page";
	public static final String ACCESS_DENIED_MESSAGE = "You Do not have Permission to Access this Page";
	public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
	public static final String[] PUBLIC_URLS = { "/user/login", "/user/register", "/user/image/**" };
	//public static final String[] PUBLIC_URLS = { "**" };
	
}
