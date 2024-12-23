--liquibase formatted sql

CREATE DATABASE pokedex;

--changeset pokedex:1

CREATE TABLE IF NOT EXISTS trainers (
    id varchar(36) default(gen_random_uuid()) primary key,
    username text UNIQUE,
    password text
);
--rollback drop table trainers

CREATE TABLE IF NOT EXISTS pokedex (
    id varchar(36) default(gen_random_uuid()) primary key,
    pokemon_id int,
    trainers_id varchar(36),
    CONSTRAINT fk_trainers FOREIGN KEY(trainers_id) REFERENCES trainers(id)
);
--rollback drop table pokedex
