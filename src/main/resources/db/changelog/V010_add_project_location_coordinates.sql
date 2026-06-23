--liquibase formatted sql

--changeset kathiravan:010

ALTER TABLE projects
    ADD COLUMN latitude  NUMERIC(10, 7),
    ADD COLUMN longitude NUMERIC(10, 7);
