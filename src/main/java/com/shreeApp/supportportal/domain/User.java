package com.shreeApp.supportportal.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;


@Entity
public class User implements Serializable {
		
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(nullable=false,updatable=false)
	@JsonProperty(access=JsonProperty.Access.WRITE_ONLY)
	private Long Id;
		
	private String userId;
	private String firstName;
	private String lastName;
	private String userName;
	
	@JsonProperty(access=JsonProperty.Access.WRITE_ONLY)
	private String password;
	private String email;
	private String profileImageUrl;
	private Date lastLoginDate;
	private Date lastLoginDateDisplay;
	private Date joinDate;
	private String role;
	private String[] authorities;
	private boolean isActive;
	private boolean isNotLocked;
	
	public User() {}	
	
	
	public User(Long id, String userId, String firstName, String lastName, String userName, String password,
			String email, String profileImageUrl, Date lastLoginDate, Date lastLoginDateDisplay, Date joinDate,
			String role, String[] authorities, boolean isActive, boolean isNotLocked) {
		//super();
		this.Id = id;
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userName = userName;
		this.password = password;
		this.email = email;
		this.profileImageUrl = profileImageUrl;
		this.lastLoginDate = lastLoginDate;
		this.lastLoginDateDisplay = lastLoginDateDisplay;
		this.joinDate = joinDate;
		this.role = role;
		this.authorities = authorities;
		this.isActive = isActive;
		this.isNotLocked = isNotLocked;
	}



	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}
	public Date getLastLoginDate() {
		return lastLoginDate;
	}
	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	public Date getLastLoginDateDisplay() {
		return lastLoginDateDisplay;
	}
	public void setLastLoginDateDisplay(Date lastLoginDateDisplay) {
		this.lastLoginDateDisplay = lastLoginDateDisplay;
	}
	public Date getJoinDate() {
		return joinDate;
	}
	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}
	public String getRole() {
		return role;
	}
	public void setRoles(String role) {
		this.role = role;
	}
	public String[] getAuthorities() {
		return authorities;
	}
	public void setAuthorities(String[] authorities) {
		this.authorities = authorities;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public boolean isNotLocked() {
		return isNotLocked;
	}
	public void setNotLocked(boolean isNotLocked) {
		this.isNotLocked = isNotLocked;
	}

	@Override
	public String toString() {
		return "User [Id=" + Id + ", userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", userName=" + userName + ", password=" + password + ", email=" + email + ", profileImageUrl="
				+ profileImageUrl + ", lastLoginDate=" + lastLoginDate + ", lastLoginDateDisplay="
				+ lastLoginDateDisplay + ", joinDate=" + joinDate + ", role=" + role + ", authorities="
				+ Arrays.toString(authorities) + ", isActive=" + isActive + ", isNotLocked=" + isNotLocked + "]";
	}
}
