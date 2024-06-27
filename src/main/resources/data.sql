-- One admin user, named admin1 with passwor 4dm1n and authority admin
INSERT INTO authorities(id,authority) VALUES (1,'ADMIN');
INSERT INTO authorities(id,authority) VALUES (2, 'PLAYER');

/*ADMINS*/
/*Contraseña : 4dm1n */
INSERT INTO appusers(id,username,password,authority) VALUES (1,'admin1','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS',1);
INSERT INTO appusers(id,username,password,authority) VALUES (2,'admin2','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS',1);
INSERT INTO appusers(id,username,password,authority) VALUES (3,'admin3','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS',1);

/*PLAYERS*/
/*Contraseña : 0wn3r*/

INSERT INTO appusers(id,username,password,authority) VALUES (4,'player1','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (5,'player2','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (6,'player3','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (7,'player4','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);

/* 4 Players*/
INSERT INTO players(id, first_name , last_name , email,image,birthday_date , registration_date ,user_id ) VALUES 
(1, 'Feyre', 'Archeron','feyre@Acotar.com', 'foto2', '1995-05-24', '2020-01-01', 4);
INSERT INTO players(id, first_name , last_name , email,image,birthday_date , registration_date ,user_id ) VALUES 
(2, 'Rhysand', 'Night', 'Rhys@Acotar.com', 'foto3' , '1989-06-14', '2020-01-02', 5);
INSERT INTO players(id, first_name , last_name , email,image,birthday_date , registration_date ,user_id ) VALUES 
(3, 'Cassian', 'Shadow', 'Cassian@Acosf.com', 'foto3' , '1988-07-07', '2020-01-02', 6);
INSERT INTO players(id, first_name , last_name , email,image,birthday_date , registration_date ,user_id ) VALUES 
(4, 'Nesta', 'Archeron', 'Nesta@Acosf.com', 'foto4' , '1992-01-22', '2020-01-02', 7);


/*2 partidas terminadas*/
INSERT INTO games(id,code,create_date, start_date, end_date, creator) VALUES 
(1, 'AAAA','2018-08-04 17:29:49','2018-08-04 17:30:25','2018-08-04 17:48:49', 1);
INSERT INTO games(id,code,create_date, start_date, end_date, creator) VALUES 
(2, 'BBBB','2020-07-02 17:29:49','2020-07-02 17:30:25','2020-07-02 17:48:49', 2);



/*Jugadores en partidas*/

INSERT INTO games_players(game_id, player_id) VALUES (1,1);
INSERT INTO games_players(game_id, player_id) VALUES (1,2);
INSERT INTO games_players(game_id, player_id) VALUES (1,3);
INSERT INTO games_players(game_id, player_id) VALUES (1,4);
INSERT INTO games_players(game_id, player_id) VALUES (2,1);
INSERT INTO games_players(game_id, player_id) VALUES (2,4);



