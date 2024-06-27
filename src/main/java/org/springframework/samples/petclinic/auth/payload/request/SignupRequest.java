package org.springframework.samples.petclinic.auth.payload.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

import org.hibernate.annotations.ColumnDefault;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
	
	@NotBlank
	private String username;
	
	private String authority;

	@NotBlank
	private String password;
	
	@NotBlank
	private String firstName;
	
	@NotBlank
	private String lastName;
	
	private LocalDate registrationDate;

	private LocalDate birthdayDate;

	@ColumnDefault("'Estandar'")
	private String image;

	@NotBlank
	private String email;

}
