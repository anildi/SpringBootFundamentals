-- Postgres Larku data

-- SET ROLE postgres;

-- \c larku;
delete from student_scheduledclass;

delete from scheduledclass;
alter sequence scheduledclass_id_seq restart;

delete from course;
alter sequence course_id_seq restart;

delete from student;
alter sequence student_id_seq restart;


INSERT INTO Course (code, credits, title) VALUES
('BKTW-101',3.0,'Introduction to BasketWeaving'),
('BOT-202',2.0,'Yet more Botany'),
('MATH-101',4.0,'Intro To Math');

INSERT INTO ScheduledClass (startdate, enddate, course_id) VALUES
('2022-10-10','2023-02-20',1),
('2022-10-10','2023-08-10',2),
('2022-10-10','2023-10-10',3);


INSERT INTO Student (NAME, PHONENUMBER, DOB, STATUS) VALUES
('Manoj-Cont-Postgres','222 333-4444','1956-08-15', 'FULL_TIME'),
('Ana-Cont-Postgres','222 333-7900','1978-03-10', 'PART_TIME'),
('Roberta-Cont-Postgres','383 343-5879','2000-07-15', 'HIBERNATING'),
('Madhu-Cont-Postgres','383 598-8279','1994-10-07', 'PART_TIME');

INSERT INTO Student_ScheduledClass (students_id, classes_id) VALUES
(1,2),
(2,3),
(1,3);
