package org.springframework.samples.petclinic.game;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.player.Player;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "games")
public class Game extends BaseEntity{


    @Column(name = "code", unique = true)
    @Size(min = 4, max = 4, message = "The code must be 4 characters long")
    @NotBlank
    String code;

    @Column(name = "create_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createDate;

    @Column(name = "start_date")
    @Nullable
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime startDate;

    @Column(name = "end_date")
    @Nullable
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime endDate;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "creator", referencedColumnName = "id")
    Player creator;


    @JoinTable(name = "games_players", joinColumns = @JoinColumn(name = "game_id")
    , inverseJoinColumns = @JoinColumn(name = "player_id"),
    uniqueConstraints = {@UniqueConstraint(columnNames = {"game_id", "player_id"})})
    @ManyToMany(fetch = FetchType.EAGER)
    List<Player> players;

    @Transient
    public void numberOfPlayers() {
        this.players.size();
    }

    @Transient
    public void removePlayer(Integer playerId) {
        players.replaceAll(player -> {
            if(player.getId().equals(playerId)){
                return null;
            }
            return player;
        });
    }

}
