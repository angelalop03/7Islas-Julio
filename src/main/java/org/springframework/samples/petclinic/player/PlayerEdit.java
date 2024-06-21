package org.springframework.samples.petclinic.player;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PlayerEdit {

    @NotBlank
    String username;

    @NotBlank
    String password;

    @NotEmpty
    @Column(name = "first_name")
    protected String firstName;

    @NotEmpty
    @Column(name = "last_name")
    protected String lastName;

    String email;

    @Column(name = "birthday_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull
    LocalDate birthdayDate;

    public PlayerEdit() {
    }

}
