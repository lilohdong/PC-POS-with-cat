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

create view sales_search as
select s.sales_id sales_id, s.member_id member_id, s.sales_date sales_date, s.sales_time sales_time,p.p_name p_name, s.price price
from sales s, product p
where s.product_id = p.p_id;