DROP TABLE if exists Student_ScheduledClass;

DROP TABLE if exists ScheduledClass;

DROP TABLE if exists Course;

DROP TABLE if exists Student;


CREATE TABLE Student
(
    id          INT(11)      NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    phoneNumber VARCHAR(20),
    status      VARCHAR(20),
    CONSTRAINT student_pk PRIMARY KEY (id)
);

CREATE TABLE Course
(
    id      INT    NOT NULL AUTO_INCREMENT,
    code    VARCHAR(20),
    credits DOUBLE NOT NULL,
    title   VARCHAR(255),
    CONSTRAINT course_pk PRIMARY KEY (id)
);

CREATE TABLE ScheduledClass
(
    id        INT NOT NULL AUTO_INCREMENT,
    enddate   VARCHAR(20),
    startdate VARCHAR(20),
    course_id INTEGER,
    CONSTRAINT class_pk PRIMARY KEY (id)
);

CREATE TABLE Student_ScheduledClass
(
    students_id INTEGER NOT NULL,
    classes_id  INTEGER NOT NULL
);

CREATE UNIQUE INDEX SQL150211090953900 ON Course (id ASC);

CREATE INDEX SQL150211090954080 ON Student_ScheduledClass (classes_id ASC);

CREATE INDEX SQL150211090953990 ON ScheduledClass (course_id ASC);

CREATE UNIQUE INDEX SQL150211090953920 ON ScheduledClass (id ASC);

CREATE UNIQUE INDEX SQL150211090953950 ON Student (id ASC);

CREATE INDEX SQL150211090954040 ON Student_ScheduledClass (students_id ASC);

-- ALTER TABLE COURSE ADD CONSTRAINT SQL150211090953900 PRIMARY KEY (ID);

-- ALTER TABLE SCHEDULEDCLASS ADD CONSTRAINT SQL150211090953920 PRIMARY KEY (ID);

-- ALTER TABLE STUDENT ADD CONSTRAINT SQL150211090953950 PRIMARY KEY (ID);

ALTER TABLE Student_ScheduledClass
    ADD CONSTRAINT FK318CA38F83AAB40 FOREIGN KEY (classes_id)
        REFERENCES ScheduledClass (id);

ALTER TABLE Student_ScheduledClass
    ADD CONSTRAINT FK318CA38F5E4D1BDC FOREIGN KEY (students_id)
        REFERENCES Student (id);

ALTER TABLE Student_ScheduledClass
    ADD CONSTRAINT NEW_UNIQUE UNIQUE (students_id, classes_id);

ALTER TABLE ScheduledClass
    ADD CONSTRAINT FKE64C28EBD735B77B FOREIGN KEY (course_id)
        REFERENCES Course (id);
	


	
