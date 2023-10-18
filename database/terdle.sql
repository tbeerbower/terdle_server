-- Database: terdle

BEGIN TRANSACTION;

DROP TABLE IF EXISTS user_game, game, app_user;

CREATE TABLE app_user (
	user_id serial,
	username varchar(50) NOT NULL,
	password_hash varchar(200) NOT NULL,
	role varchar(50) NOT NULL,
	display_name varchar(50),
	img_url varchar(500),
	short_bio varchar(500),
	CONSTRAINT PK_app_user PRIMARY KEY (user_id),
	CONSTRAINT UQ_username UNIQUE (username)
);

CREATE TABLE game (
	game_id serial PRIMARY KEY,
	word char(5) NOT NULL,
	game_date date NOT NULL,
	type int NOT NULL, -- 0 DAILY, 1 RANDOM
	CONSTRAINT UQ_game UNIQUE (word, game_date)
);

CREATE TABLE user_game (
	user_id int NOT NULL,
	game_id int NOT NULL,
	guesses int,
	guess1 char(5),
	guess2 char(5),
	guess3 char(5),
	guess4 char(5),
	guess5 char(5),
	guess6 char(5),
	success boolean,
	CONSTRAINT PK_user_game PRIMARY KEY (user_id, game_id),
	CONSTRAINT FK_user FOREIGN KEY(user_id) REFERENCES app_user(user_id),
	CONSTRAINT FK_game FOREIGN KEY(game_id) REFERENCES game(game_id)
);

------------------------------ Test Data ---------------------------------

-- Users - all have password: 'password'
INSERT INTO app_user (username, password_hash, role, display_name, img_url, short_bio)
	VALUES ('admin','$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC','ROLE_ADMIN', null, null, null);
INSERT INTO app_user (username, password_hash, role, display_name, img_url, short_bio)
	VALUES ('job_coach','$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC','ROLE_USER', 'Imani',
		'https://source.boringavatars.com/bauhaus/80/job_coach',
		'Career coach, specializing in working with students fresh out of college and career changers, with a special interest in technology roles.');
INSERT INTO app_user (username, password_hash, role, display_name, img_url, short_bio)
	VALUES ('newbie_coder','$2a$10$We8.y4IV/uQOPT1crppxR.aASgeKFr24ISrkHcqWWSYlxRu4oeqE6','ROLE_USER', null,
		'https://source.boringavatars.com/beam/80/newbie_coder',
		'New parent turned coder, with a passion for basketball, stats, and a good laugh.');
INSERT INTO app_user (username, password_hash, role, display_name, img_url, short_bio)
	VALUES ('troublemaker','$2a$10$K/XxMq03OaJM4AhLU7YE3eQh1KAd8/gzWIOWLgBqVrb5AoSy.pmSK','ROLE_USER', null, null, null);

COMMIT TRANSACTION;
