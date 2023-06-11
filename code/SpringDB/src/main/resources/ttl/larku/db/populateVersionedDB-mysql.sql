INSERT INTO Course (code,credits,title) VALUES
('BKTW-101',3.0,'Introduction to BasketWeaving'),
('BOT-202',2.0,'Yet more Botany'),
('MATH-101',4.0,'Intro To Math');

INSERT INTO ScheduledClass (startdate,enddate,course_id) VALUES
('2012-10-10','2013-2-20',1),
('2012-10-10','2013-08-10',2),
('2012-10-10','2013-10-10',3);

INSERT INTO Student (name, phoneNumber, status) VALUES
('Manoj-Mysql','222 333-4444','FULL_TIME'),
('Ana-Mysql','222 333-7900','PART_TIME'),
('Roberta-Mysql','383 343-5879','HIBERNATING'),
('Madhu-Mysql','383 598-8279','PART_TIME');

INSERT INTO StudentVersioned (version, name, phoneNumber, status) VALUES
(1, 'Manoj-VMysql','222 333-4444','FULL_TIME'),
(1, 'Ana-VMysql','222 333-7900','PART_TIME'),
(1, 'Roberta-VMysql','383 343-5879','HIBERNATING'),
(1, 'Madhu-VMysql','383 598-8279','PART_TIME');

INSERT INTO Student_ScheduledClass (students_id,classes_id) VALUES
(1,2),
(2,3),
(1,3),
(1,1);