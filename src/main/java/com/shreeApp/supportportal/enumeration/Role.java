package com.shreeApp.supportportal.enumeration;

import static com.shreeApp.supportportal.constants.Authority.*;

public enum Role {

	ROLE_USER(USER_AUTHORITES),
	ROLE_HR(HR_AUTHORITES),
	ROLE_MANAGER(MANAGER_AUTHORITES),
	ROLE_ADMIN(ADMIN_AUTHORITES),
	ROLE_SUPER_ADMIN(SUPER_ADMIN_AUTHORITES);
	
	private String[] authorities;
	
	Role(String... authorities)
	{
		this.authorities = authorities;
	}

	public String[] getAuthorities() {
		return authorities;
	}
			
}
