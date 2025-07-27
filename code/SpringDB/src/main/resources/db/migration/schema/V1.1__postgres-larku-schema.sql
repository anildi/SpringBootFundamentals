-- Postgres larku schema file

-- Note that the database 'larku' and the user 'larku' have to already exist

-- select current_user;
-- set ROLE larku;

DROP TABLE if exists Student_ScheduledClass CASCADE;

DROP TABLE if exists ScheduledClass CASCADE;
-- drop sequence if exists scheduledclass_id_seq;

DROP TABLE if exists Course CASCADE;
-- drop sequence if exists course_id_seq;


DROP TABLE if exists Student CASCADE;
DROP TABLE if exists StudentVersioned CASCADE;
-- drop sequence if exists student_id_seq;

CREATE TABLE StudentVersioned
(
    id          serial primary key not null,
    name        VARCHAR(255) NOT NULL,
    phoneNumber VARCHAR(20),
    dob         DATE,
    status      VARCHAR(20),
    version     integer
);

-- Create Tables
CREATE TABLE Student
(
    id          serial primary key not null,
    name        VARCHAR(255) NOT NULL,
    phoneNumber VARCHAR(20),
    dob         DATE,
    status      VARCHAR(20)
);

CREATE TABLE Course
(
     id      serial primary key not NULL,
     code    VARCHAR(20),
     credits REAL NOT NULL,
     title   VARCHAR(255)
);


CREATE TABLE ScheduledClass
(
     id        serial primary key NOT NULL,
     enddate   DATE,
     startdate DATE,
     course_id INTEGER
);

CREATE TABLE Student_ScheduledClass
(
     students_id INTEGER NOT NULL,
     classes_id  INTEGER NOT NULL
);

--

-- Create Indices
CREATE UNIQUE INDEX idx_course_id ON Course (id ASC);

CREATE INDEX idx_student_scheduledclass_classes_id ON Student_ScheduledClass (classes_id ASC);

CREATE INDEX idx_student_scheduledclass_students_id ON Student_ScheduledClass (students_id ASC);

CREATE INDEX idx_scheduledclass_course_id ON ScheduledClass (course_id ASC);

CREATE UNIQUE INDEX idx_scheduledclass_id ON ScheduledClass (id ASC);

CREATE UNIQUE INDEX idx_student_id ON Student (id ASC);
-- 


-- Add Constraints
ALTER TABLE Student_ScheduledClass
    ADD CONSTRAINT fk_student_scheduledclass_classes_id FOREIGN KEY (classes_id)
         REFERENCES ScheduledClass (id);

ALTER TABLE Student_ScheduledClass
     ADD CONSTRAINT fk_student_scheduled_class_students_id FOREIGN KEY (students_id)
         REFERENCES Student (id);
 
ALTER TABLE Student_ScheduledClass
     ADD CONSTRAINT NEW_UNIQUE UNIQUE (students_id, classes_id);
 
ALTER TABLE ScheduledClass
     ADD CONSTRAINT fk_scheduledclass_course_id FOREIGN KEY (course_id)
         REFERENCES Course (id);