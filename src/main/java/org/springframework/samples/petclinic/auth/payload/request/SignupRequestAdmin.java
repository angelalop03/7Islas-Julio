package org.springframework.samples.petclinic.auth.payload.request;

import jakarta.validation.constraints.NotBlank;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestAdmin {
	
	@NotBlank
	private String username;
	
	private String authority;

	@NotBlank
	private String password;
	


}
