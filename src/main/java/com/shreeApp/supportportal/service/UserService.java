package com.shreeApp.supportportal.service;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.web.multipart.MultipartFile;

import com.shreeApp.supportportal.domain.User;
import com.shreeApp.supportportal.exception.domain.EmailExistException;
import com.shreeApp.supportportal.exception.domain.EmailNotFoundException;
import com.shreeApp.supportportal.exception.domain.NotAnImageFileException;
import com.shreeApp.supportportal.exception.domain.UserNameExistException;
import com.shreeApp.supportportal.exception.domain.UserNotFoundException;

public interface UserService {
	// to register a new User
	User register(String firstName, String lastName, String userName, String email)
			throws UserNotFoundException, UserNameExistException, EmailExistException, MessagingException;

	List<User> getUsers();

	User findUserByUserName(String userName);

	User findUserByEmail(String email);

	// ADMIN will use this method to add New User
	User addNewUser(String firstName, String lastName, String userName, String email, String role, boolean isNonLocked,
			boolean isActive, MultipartFile profileImage)
			throws UserNotFoundException, UserNameExistException, EmailExistException, MessagingException, IOException, NotAnImageFileException;

	User updateUser(String currentUserName, String newFirstName, String newLastName, String newUserName,
			String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage)
			throws UserNotFoundException, UserNameExistException, EmailExistException, IOException, NotAnImageFileException;

	void deleteUser(String userName) throws IOException;

	void resetPassword(String email) throws EmailNotFoundException, MessagingException;

	User updateProfileImage(String userName, MultipartFile profileImage) 
			throws UserNotFoundException, UserNameExistException, EmailExistException, IOException, NotAnImageFileException;

}
