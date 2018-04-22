CREATE TABLE `user` (
    id        INTEGER NOT NULL AUTO_INCREMENT,
    username  VARCHAR(40) NOT NULL UNIQUE,
  	password  VARCHAR(40) NOT NULL,
    primary key(id)
	
);

CREATE TABLE jogging (
    id           INTEGER NOT NULL AUTO_INCREMENT,
    distance     Integer NOT NULL,
    duration     Integer NOT NULL,
    jogging_date date not null default SYSDATE,
    user_id   	 INTEGER NOT NULL,
    primary key(id)
);

ALTER TABLE jogging
    ADD CONSTRAINT jogging_user_fk FOREIGN KEY ( user_id)
        REFERENCES `user` ( id );