CREATE DATABASE IF NOT EXISTS notebook;
USE notebook;
CREATE TABLE notebook.Notes(
    NoteDate varchar(255),
    Title varchar(255),
    Body varchar(255),
    Id varchar(255),
    PRIMARY KEY (Id)
);
