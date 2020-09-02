package com.shreeApp.supportportal.resource;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.shreeApp.supportportal.constants.SecurityConstant.*;

import com.shreeApp.supportportal.domain.HttpResponse;
import com.shreeApp.supportportal.domain.User;
import com.shreeApp.supportportal.domain.UserPrincipal;
import com.shreeApp.supportportal.exception.ExceptionHandling;
import com.shreeApp.supportportal.exception.domain.EmailExistException;
import com.shreeApp.supportportal.exception.domain.EmailNotFoundException;
import com.shreeApp.supportportal.exception.domain.NotAnImageFileException;
import com.shreeApp.supportportal.exception.domain.UserNameExistException;
import com.shreeApp.supportportal.exception.domain.UserNotFoundException;
import com.shreeApp.supportportal.service.UserService;
import com.shreeApp.supportportal.utility.JWTTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.HttpStatus.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.shreeApp.supportportal.constants.FileConstant.*;
import static com.shreeApp.supportportal.constants.UserImplConstant.*;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

//@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping(path = { "/", "/user" })

public class UserResource extends ExceptionHandling {

	/*
	 * @Autowired private UserService userService;
	 * 
	 * @Autowired private AuthenticationManager authenticationManager;
	 * 
	 * @Autowired private JWTTokenProvider jwtTokenProvider;
	 */

	private AuthenticationManager authenticationManager;
	private UserService userService;
	private JWTTokenProvider jwtTokenProvider;

	@Autowired
	public UserResource(AuthenticationManager authenticationManager, UserService userService,
			JWTTokenProvider jwtTokenProvider) {
		this.authenticationManager = authenticationManager;
		this.userService = userService;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@PostMapping("/login")
	public ResponseEntity<User> login(@RequestBody User user) {
		System.out.println("user::"+user);
		authenticate(user.getUserName(), user.getPassword());
		User loginUser = userService.findUserByUserName(user.getUserName());
		UserPrincipal userPrincipal = new UserPrincipal(loginUser);
		HttpHeaders headers = getJwtHeader(userPrincipal);
		return new ResponseEntity<>(loginUser, headers, OK);
	}

	@PostMapping("/register")
	public ResponseEntity<User> register(@RequestBody User user)
			throws UserNotFoundException, UserNameExistException, EmailExistException, MessagingException {
		System.out.println("in register method::");
		User registerUser = userService.register(user.getFirstName(), user.getLastName(), user.getUserName(),
				user.getEmail());
		System.out.println("in register method registerUser::" + registerUser);
		return new ResponseEntity<>(registerUser, OK);
	}

	@PostMapping("/add")
	public ResponseEntity<User> addNewUser(@RequestParam("firstName") String firstName,
			@RequestParam("lastName") String lastName, @RequestParam("userName") String userName,
			@RequestParam("email") String email, @RequestParam("role") String role,
			@RequestParam("isNonLocked") String isNonLocked, @RequestParam("isActive") String isActive,
			@RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
			throws UserNotFoundException, UserNameExistException, EmailExistException, MessagingException, IOException, NotAnImageFileException {
		User newUser = userService.addNewUser(firstName, lastName, userName, email, role,
				Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);
		return new ResponseEntity<>(newUser, OK);
	}

	@PostMapping("/update")
	public ResponseEntity<User> updateUser(@RequestParam("currentUserName") String currentUserName,
			@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName,
			@RequestParam("userName") String userName, @RequestParam("email") String email,
			@RequestParam("role") String role, @RequestParam("isNonLocked") String isNonLocked,
			@RequestParam("isActive") String isActive,
			@RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
			throws UserNotFoundException, UserNameExistException, EmailExistException, MessagingException, IOException, NotAnImageFileException {
		User updatedUser = userService.updateUser(currentUserName, firstName, lastName, userName, email, role,
				Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);
		return new ResponseEntity<>(updatedUser, OK);
	}

	@GetMapping("/find/{username}")
	public ResponseEntity<User> getUser(@PathVariable("username") String username) {
		User getUser = userService.findUserByUserName(username);
		return new ResponseEntity<>(getUser, OK);
	}

	@GetMapping("/list")
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> users = userService.getUsers();
		return new ResponseEntity<>(users, OK);
	}

	@GetMapping("/resetPassword/{email}")
	public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email)
			throws EmailNotFoundException, MessagingException {
		System.out.println("email:"+email);
		userService.resetPassword(email);
		return response(OK, EMAIL_SENT + email);
	}

	@DeleteMapping("/delete/{userName}")
	@PreAuthorize("hasAnyAuthority('user:delete')")
	public ResponseEntity<HttpResponse> delete(@PathVariable("userName") String userName) throws IOException {
		userService.deleteUser(userName);
		return response(OK, USER_DELETED_SUCCESSFULLY);
	}

	@PostMapping("/updateProfileImage")
	public ResponseEntity<User> updateProfileImage(@RequestParam("userName") String userName,
			@RequestParam("profileImage") MultipartFile profileImage)
			throws UserNotFoundException, UserNameExistException, EmailExistException, MessagingException, IOException, NotAnImageFileException {
		User user = userService.updateProfileImage(userName, profileImage);
		return new ResponseEntity<>(user, OK);
	}

	@GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
	public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName)
			throws IOException {
		return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
	}

	@GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
	public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
		URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try (InputStream inputStream = url.openStream()) {
			int bytesRead;
			byte[] chunk = new byte[1024];
			while ((bytesRead = inputStream.read(chunk)) > 0) {
				byteArrayOutputStream.write(chunk, 0, bytesRead);
			}
		}
		return byteArrayOutputStream.toByteArray();
	}

	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
		HttpResponse body = new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
				message.toUpperCase());
		return new ResponseEntity<>(body, httpStatus);
	}

	private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
		return headers;
	}

	private void authenticate(String userName, String password) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));

	}
}
