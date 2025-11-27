DROP DATABASE IF EXISTS POS_PLACE;
create database POS_PLACE;
use POS_PLACE;

create table member(
                       id VARCHAR(30) PRIMARY KEY,
                       passwd varchar(30) not null,
                       name VARCHAR(10) NOT NULL,
                       birth DATE not null,
                       sex VARCHAR(10),
                       remain_time int default 0,
                       phone VARCHAR(20) UNIQUE
);
create table product_category(
    c_id int primary key,
    c_name varchar(20) not null
);
create table product(
    p_id varchar(5) primary key,
    p_name varchar(30) not null,
    stock int default 0,
    price int default 0,
    category VARCHAR(20) not null,
    foreign key(category) references product_category(c_id) on update cascade
);
create table stock_in(
    receive_id int auto_increment primary key,
    p_id varchar(5) not null,
    contity int default 1
);
create table sales(
                      sales_id varchar(5) primary key,
                      member_id varchar(30),
                      sales_date date not null,
                      sales_time DATETIME not null,
                      product_id varchar(5),
                      price int default 0,
                      foreign key (member_id) references member(id) on update cascade,
                      foreign key (product_id) references product(p_id) on update cascade on delete set null
);
INSERT INTO member (id, passwd, name, birth, sex, remain_time, phone) VALUES
                                                                          ('user001', 'pass001!', '김철수', '1995-05-15', '남자', 3600, '010-1234-5678'),
                                                                          ('user002', 'pass002!', '이영희', '2000-10-20', '여자', 7200, '010-9876-5432'),
                                                                          ('user003', 'pass003!', '박민준', '1998-03-01', '남자', 0, '010-1111-2222'),
                                                                          ('user004', 'pass004!', '최지우', '2002-07-25', '여자', 1800, '010-3333-4444'),
                                                                          ('user005', 'pass005!', '정현서', '1990-12-05', '남자', 5400, '010-5555-6666');

INSERT INTO product (p_id, p_name, price) VALUES
                                              ('P0001', '콜라 (캔)', 1500),
                                              ('P0002', '삼각김밥 (참치마요)', 1200),
                                              ('P0003', '라면 (컵)', 1000),
                                              ('P0004', '핫바', 2000),
                                              ('P0005', '에너지 드링크', 2500);
INSERT INTO sales (sales_id, member_id, sales_date, sales_time, product_id, price) VALUES
                                                                                       ('S0001', 'user001', '2025-11-26', '2025-11-26 15:30:00', 'P0001', 1500), -- 김철수: 콜라
                                                                                       ('S0002', 'user002', '2025-11-26', '2025-11-26 16:05:00', 'P0003', 1000), -- 이영희: 라면
                                                                                       ('S0003', 'user003', '2025-11-26', '2025-11-26 17:10:00', 'P0002', 1200), -- 박민준: 삼각김밥
                                                                                       ('S0004', 'user001', '2025-11-26', '2025-11-26 18:00:00', 'P0004', 2000), -- 김철수: 핫바
                                                                                       ('S0005', 'user004', '2025-11-25', '2025-11-25 20:45:00', 'P0005', 2500), -- 최지우: 에너지 드링크
                                                                                       ('S0006', 'user002', '2025-11-25', '2025-11-25 21:30:00', 'P0001', 1500), -- 이영희: 콜라
                                                                                       ('S0007', 'user005', '2025-11-24', '2025-11-24 10:15:00', 'P0003', 1000), -- 정현서: 라면
                                                                                       ('S0008', 'user003', '2025-11-24', '2025-11-24 11:40:00', 'P0004', 2000), -- 박민준: 핫바
                                                                                       ('S0009', 'user001', '2025-11-24', '2025-11-24 12:35:00', 'P0005', 2500), -- 김철수: 에너지 드링크
                                                                                       ('S0010', 'user004', '2025-11-23', '2025-11-23 19:50:00', 'P0002', 1200); -- 최지우: 삼각김밥

create view sales_search as
select s.sales_id sales_id, s.member_id member_id, s.sales_date sales_date, s.sales_time sales_time,p.p_name p_name, s.price price
from sales s, product p
where s.product_id = p.p_id;