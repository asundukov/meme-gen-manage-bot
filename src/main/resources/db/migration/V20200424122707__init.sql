CREATE TABLE bot (
    bot_id serial not null primary key,
    admin_usr_id bigint not null,
    token varchar(1024) not null,
    total_images INT not null,
    created_on timestamp not null default now()
);

CREATE UNIQUE index bot_token_idx  ON bot(token);
CREATE index bot_admin_usr_id_idx  ON bot(admin_usr_id);

CREATE TABLE mark (
    mark_id serial not null primary key ,
    bot_id int not null references bot(bot_id),
    position smallint not null,
    size_percent DECIMAL(5,2) not null,
    total_images INT not null,
    created_on timestamp not null default now()
);

ALTER TABLE bot add column default_mark_id INT REFERENCES mark(mark_id);

CREATE TABLE usr (
    usr_id BIGINT NOT NULL PRIMARY KEY,
    first_name VARCHAR(1024),
    last_name VARCHAR(1024),
    user_name VARCHAR(1042),
    language_code VARCHAR(256),
    created_on TIMESTAMP NOT NULL
);

CREATE TABLE usr_bot_settings (
    usr_bot_settings_id SERIAL NOT NULL PRIMARY KEY ,
    usr_id BIGINT NOT NULL REFERENCES usr(usr_id),
    bot_id INT NOT NULL REFERENCES bot(bot_id),
    created_on TIMESTAMP NOT NULL,
    is_blocked BOOLEAN NOT NULL,
    selected_mark_id INT REFERENCES mark(mark_id)
);

CREATE UNIQUE INDEX usr_bot_settings_usr_id_bot_id_uniq ON usr_bot_settings(usr_id, bot_id);

CREATE TABLE bot_message (
  bot_message_id serial not null primary key,
  bot_id int not null references bot(bot_id),
  message_type int not null,
  message varchar(2024) not null
);

CREATE index bot_message_bot_id_idx ON bot_message(bot_id);

