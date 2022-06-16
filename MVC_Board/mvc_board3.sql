CREATE TABLE member (
	idx INT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(20) NOT NULL,
	id VARCHAR(8) UNIQUE NOT NULL,
	passwd VARCHAR(16) NOT NULL,
	email VARCHAR(50) UNIQUE NOT NULL,
	gender VARCHAR(1) NOT NULL,
	date DATE NOT NULL
);

SHOW TABLES;
DESC member;

SHOW TABLE STATUS WHERE name='member';

INSERT INTO member VALUES (null,'관리자','admin','1234','admin@admin.com','남',now());
SELECT * FROM member;

