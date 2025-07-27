-- Postgres larku schema file
-- Should run as 'admin' user (postgres)


DROP database if EXISTS larku WITH (FORCE);


CREATE database larku;
GRANT CONNECT ON DATABASE larku TO larkugrp;

-- connect to DB larku
\c larku;

--This is to extend any future privileges granted to larkugrp to user larku.
ALTER DEFAULT PRIVILEGES FOR ROLE larkugrp IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE,  TRUNCATE ON TABLES TO larku;
ALTER DEFAULT PRIVILEGES FOR ROLE larkugrp IN SCHEMA public GRANT USAGE, SELECT, UPDATE ON SEQUENCES TO larku;



-- DROP TABLE if exists Student_ScheduledClass;

-- DROP TABLE if exists ScheduledClass;

-- DROP TABLE if exists Course;

-- DROP TABLE if exists Student;


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
     id        serial NOT NULL,
     enddate   VARCHAR(20),
     startdate VARCHAR(20),
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
 	
--
 
 
--Grant permissions
REVOKE ALL ON SCHEMA public FROM PUBLIC;

GRANT ALL ON SCHEMA public TO postgres;

-- GRANT ALL ON SCHEMA public TO postgres;
GRANT USAGE ON SCHEMA public TO larkugrp;
GRANT SELECT, INSERT, UPDATE, DELETE, TRUNCATE on ALL TABLES IN SCHEMA public TO larkugrp;
GRANT USAGE, SELECT, UPDATE on ALL SEQUENCES IN SCHEMA public TO larkugrp;
