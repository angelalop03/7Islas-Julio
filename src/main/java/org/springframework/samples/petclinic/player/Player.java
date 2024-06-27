package org.springframework.samples.petclinic.player;

import java.time.LocalDate;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.samples.petclinic.model.Person;
import org.springframework.samples.petclinic.user.User;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "players")
public class Player extends Person{

    @NotNull
    @Column(name = "registration_date")
    @JsonFormat(pattern = "yyyy-MM-dd",shape=JsonFormat.Shape.STRING)
    LocalDate registrationDate;

    @NotNull
    @Column(name = "birthday_date")
    @JsonFormat( pattern="yyyy-MM-dd",shape=JsonFormat.Shape.STRING)
    LocalDate birthdayDate;

    @NotBlank
    @Column(name = "email")
    String email;


    @NotBlank
    @Column(name = "image")
    @ColumnDefault("'Estandar'")
    String image;

    @ColumnDefault("false")
    @Column(name = "is_connected")
    Boolean isConnected;

    @Column(name = "turn")
    Boolean Turn;

    @OneToOne(cascade = { CascadeType.DETACH, CascadeType.REFRESH, CascadeType.PERSIST })
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;

}
