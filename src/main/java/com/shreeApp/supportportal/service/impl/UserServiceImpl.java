package com.shreeApp.supportportal.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.shreeApp.supportportal.constants.FileConstant;
import com.shreeApp.supportportal.domain.User;
import com.shreeApp.supportportal.domain.UserPrincipal;
import com.shreeApp.supportportal.enumeration.Role;
import com.shreeApp.supportportal.exception.domain.NotAnImageFileException;
import com.shreeApp.supportportal.exception.domain.EmailExistException;
import com.shreeApp.supportportal.exception.domain.EmailNotFoundException;
import com.shreeApp.supportportal.exception.domain.UserNameExistException;
import com.shreeApp.supportportal.exception.domain.UserNotFoundException;
import com.shreeApp.supportportal.repository.UserRepository;
import com.shreeApp.supportportal.service.UserService;

import static com.shreeApp.supportportal.enumeration.Role.*;
import static com.shreeApp.supportportal.constants.UserImplConstant.*;
import static com.shreeApp.supportportal.constants.FileConstant.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import com.shreeApp.supportportal.service.EmailService;
import com.shreeApp.supportportal.service.LoginAttemptService;

import static org.springframework.http.MediaType.*;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	private UserRepository userRepository;
	private BCryptPasswordEncoder passwordEncoder;
	private LoginAttemptService loginAttemptService;
	private EmailService emailService;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
			LoginAttemptService loginAttemptService, EmailService emailService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.loginAttemptService = loginAttemptService;
		this.emailService = emailService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = this.userRepository.findUserByUserName(username);
		if (user == null) {
			LOGGER.error(NO_USER_FOUND_BY_USERNAME + username);
			throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
		} else {
			validateLoginAttempts(user);
			user.setLastLoginDateDisplay(user.getLastLoginDate());
			user.setLastLoginDate(new Date());
			this.userRepository.save(user);

			UserPrincipal principal = new UserPrincipal(user);
			LOGGER.info("Returning Found User by username: " + username);

			return principal;
		}
	}

	@Override
	public User register(String firstName, String lastName, String userName, String email)
			throws UserNotFoundException, UserNameExistException, EmailExistException, MessagingException {

		//LOGGER.info("before validate method :");
		validateNewUsernameAndEmail(StringUtils.EMPTY, userName, email);
		//LOGGER.info(" after validate method ");

		User user = new User();
		user.setUserId(generateUserId());
		String password = generatePassword();

		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUserName(userName);
		user.setEmail(email);
		user.setJoinDate(new Date());
		user.setPassword(generateEncodePassword(password));
		user.setActive(true);
		user.setNotLocked(true);
		user.setRoles(ROLE_USER.name());
		user.setAuthorities(ROLE_USER.getAuthorities());
		user.setProfileImageUrl(generateTemproryProfileImageUrl(userName));

		userRepository.save(user);
		LOGGER.info("New User Password :" + password);
		emailService.sendNewPasswordEmail(firstName, password, email);
		
		return user;
	}

	@Override
	public User addNewUser(String firstName, String lastName, String userName, String email, String role,
			boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException,
			UserNameExistException, EmailExistException, MessagingException, IOException, NotAnImageFileException {

		validateNewUsernameAndEmail(StringUtils.EMPTY, userName, email);
		User user = new User();
		user.setUserId(generateUserId());
		String password = generatePassword();

		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUserName(userName);
		user.setEmail(email);
		user.setJoinDate(new Date());
		user.setPassword(generateEncodePassword(password));
		user.setActive(isActive);
		user.setNotLocked(isNonLocked);
		user.setRoles(getRoleEnumName(role).name());
		user.setAuthorities(getRoleEnumName(role).getAuthorities());
		System.out.println("temp url::"+ generateTemproryProfileImageUrl(userName));
		user.setProfileImageUrl(generateTemproryProfileImageUrl(userName));
		System.out.println("profileImage::"+profileImage);
		userRepository.save(user);
		saveProfileImage(user, profileImage);
		emailService.sendNewPasswordEmail(firstName, password, email);

		return user;

	}

	@Override
	public User updateUser(String currentUserName, String newFirstName, String newLastName, String newUserName,
			String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage)
			throws UserNotFoundException, UserNameExistException, EmailExistException, IOException,
			NotAnImageFileException {

		User currentUser = validateNewUsernameAndEmail(currentUserName, newUserName, newEmail);

		currentUser.setFirstName(newFirstName);
		currentUser.setLastName(newLastName);
		currentUser.setUserName(newUserName);
		currentUser.setEmail(newEmail);
		currentUser.setActive(isActive);
		currentUser.setNotLocked(isNonLocked);
		currentUser.setRoles(getRoleEnumName(role).name());
		currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());

		userRepository.save(currentUser);
		saveProfileImage(currentUser, profileImage);

		return currentUser;
	}

	@Override
	public void deleteUser(String userName) throws IOException {
		User user = userRepository.findUserByUserName(userName);
		Path userFolder = Paths.get(FileConstant.USER_FOLDER + user.getUserName()).toAbsolutePath().normalize();
		FileUtils.cleanDirectory(new File(userFolder.toString()));
		userRepository.deleteById(user.getId());
	}

	@Override
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	@Override
	public User findUserByUserName(String userName) {
		return userRepository.findUserByUserName(userName);
	}

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findUserByEmail(email);
	}

	@Override
	public void resetPassword(String email) throws EmailNotFoundException, MessagingException {

		User user = userRepository.findUserByEmail(email);
		if (user == null) {
			throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL);
		}
		String password = generatePassword();
		System.out.println("new pass::"+password);
		user.setPassword(generateEncodePassword(password));
		System.out.println("before save new pass::");
		userRepository.save(user);
		System.out.println("after save new pass::");
		emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
		System.out.println("before save new pass email::");
	}

	@Override
	public User updateProfileImage(String userName, MultipartFile profileImage) throws UserNotFoundException,
			UserNameExistException, EmailExistException, IOException, NotAnImageFileException {
		User user = validateNewUsernameAndEmail(userName, null, null);
		this.saveProfileImage(user, profileImage);
		return user;
	}

	private void saveProfileImage(User user, MultipartFile profileImage) throws IOException, NotAnImageFileException {
		
		if (profileImage != null) {
			
			if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE)
					.contains(profileImage.getContentType())) {
				throw new NotAnImageFileException(
						profileImage.getOriginalFilename() + " not an Image File. Please upload correct Image File!");
			}
			
			Path userFolder = Paths.get(USER_FOLDER + user.getUserName()).toAbsolutePath();
			if (!Files.exists(userFolder)) {
				Files.createDirectories(userFolder);
				LOGGER.info(DIRECTORY_CREATED + userFolder);
			}

			Files.deleteIfExists(Paths.get(userFolder + user.getUserName() + DOT + JPG_EXTENSION));
			Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getUserName() + DOT + JPG_EXTENSION),
					REPLACE_EXISTING);
			user.setProfileImageUrl(setProfileImageUrl(user.getUserName()));
			userRepository.save(user);
			LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
		}

	}

	private String setProfileImageUrl(String userName) {
		return ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(USER_IMAGE_PATH + userName + FORWARD_SLASH + userName + DOT + JPG_EXTENSION).toUriString();
	}

	private String generateTemproryProfileImageUrl(String userName) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + userName)
				.toUriString();
	}

	private Role getRoleEnumName(String role) {
		return Role.valueOf(role.toUpperCase());
	}

	private void validateLoginAttempts(User user) {
		if (user.isNotLocked()) {
			if (loginAttemptService.hasExceededMaxAttempts(user.getUserName())) {
				user.setNotLocked(false);
			} else {
				user.setNotLocked(true);
			}
		} else {
			loginAttemptService.evictUserFromLoginAttemptCache(user.getUserName());
		}
	}

	private String generateEncodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	private String generatePassword() {
		return RandomStringUtils.randomAlphanumeric(10);
	}

	private String generateUserId() {
		return RandomStringUtils.randomNumeric(10);
	}

	private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail)
			throws UserNotFoundException, UserNameExistException, EmailExistException {

		User userByNewUserName = findUserByUserName(newUsername);
		User userByEmail = findUserByEmail(newEmail);

		if (StringUtils.isNotBlank(currentUsername)) {
			User currentUser = findUserByUserName(currentUsername);
			if (currentUser == null) {
				throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUser);
			}
			if (userByNewUserName != null && !currentUser.getId().equals(userByNewUserName.getId())) {
				throw new UserNameExistException(USER_NAME_ALREADY_EXISTS);
			}
			if (userByEmail != null && !currentUser.getId().equals(userByNewUserName.getId())) {
				throw new EmailExistException(EMAIL_ALREADY_EXISTS);
			}
			return currentUser;
		} else {
			if (userByNewUserName != null) {
				throw new UserNameExistException(USER_NAME_ALREADY_EXISTS);
			}
			if (userByEmail != null) {
				throw new EmailExistException(EMAIL_ALREADY_EXISTS);
			}
		}
		return null;
	}
}
