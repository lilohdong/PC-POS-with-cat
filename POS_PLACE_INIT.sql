DROP DATABASE IF EXISTS POS_PLACE;
create database POS_PLACE;
use POS_PLACE;

-- 기본: 회원 테이블
create table member(
    m_no int primary key auto_increment,
    m_id VARCHAR(30) unique not null,
    passwd varchar(30) not null,
    name VARCHAR(10) NOT NULL,
    birth DATE not null,
    sex VARCHAR(10),
    remain_time int default 0,
    phone VARCHAR(20) UNIQUE,
    join_date datetime not null default current_timestamp
);


/*
조민규: 주문화면, 재고관리 화면 관련 DB 테이블들

!!완전 갈아엎었습니다!!
category(메뉴 카테고리)
menu(등록되어있는 메뉴의 정보)
orders(주문)
order_menu(주문에 있는 메뉴들)
refund(환불 기록)
ingredient(재료 정보 - 이 테이블에는 전부 단일 수량으로 표시)
stock_info(재고 단위별 단일수량 정보 저장 ex:치즈 한 박스 = 치즈50개)
stock_in(실제 재료 입고 기록 + 수량 정보)
menu_ingredient(메뉴에 소비되는 재료 정보)
*/
create table category(
	c_id varchar(5) primary key,
	c_name varchar(10) not null
);

create table menu(
	m_id varchar(5) primary key,
	m_name varchar(20) not null,
    m_price int not null,
    m_description text,
    is_soldout boolean default false,
    created_time datetime default current_timestamp,
    updated_time datetime default current_timestamp,
    
    c_id varchar(5) not null,
    foreign key(c_id) references category(c_id)
);

create table orders(
	o_id varchar(5) primary key,
    o_time datetime default current_timestamp,
    seat_num int not null,
    complete_time datetime default null,
    o_status enum('PREPARING','COMPLETED','CANCELED','REFUNDED') default 'PREPARING',
	requestment text,
    pay_method enum('CARD','CASH') default 'CARD'
);

create table order_menu(
	order_menu_id varchar(5) primary key,
    o_id varchar(5) not null,
    m_id varchar(5) not null,
    quantity int not null,
    
    unit_price int not null, -- 이 부분은 trigger를 통해 menu.price 가져오기
    total_price int generated always as (unit_price * quantity) stored,
    
    foreign key (o_id) references orders(o_id),
    foreign key (m_id) references menu(m_id)
);

create table refund(
	r_id varchar(5) primary key,
    o_id varchar(5) not null,
    r_time datetime default current_timestamp,
    r_amount int not null,
    
    foreign key(o_id)references orders(o_id)
);


create table ingredient (
    i_id varchar(5) primary key,
    i_name varchar(30) not null,
    total_quantity int default 0, 
    is_out boolean default false,
    updated_time datetime default current_timestamp on update current_timestamp
);

create table stock_info(
    stock_info_id varchar(5) primary key,
    i_id varchar(5) not null,
    unit_name varchar(20) not null,
    unit_quantity int not null,

    foreign key (i_id) references ingredient(i_id)
);

create table stock_in(
    stock_in_id varchar(5) primary key,
    i_id varchar(5) not null,
    stock_info_id varchar(5) not null,
    in_quantity int not null,
    unit_quantity int not null, -- 이 부분은 trigger를 통해 stock_info.unit_quantity 가져오기
    total_added int generated always as (in_quantity * unit_quantity) stored,
    in_time datetime default current_timestamp,

    foreign key (i_id) references ingredient(i_id),
    foreign key (stock_info_id) references stock_info(stock_info_id)
);

create table menu_ingredient (
    menu_ingredient_id varchar(5) primary key,
    m_id varchar(5) not null,
    i_id varchar(5) not null,
    required_quantity int not null,

    foreign key (m_id) references menu(m_id),
    foreign key (i_id) references ingredient(i_id)
);

/*
게임 통계 DB: time, game, play_log등(미완)
*/
create table game(
    g_id varchar(5) primary key,
    title varchar(50) not null unique,
    publisher varchar(50)
); -- 가능하다면 pc방 게임 정보를 불러올 수 있으면 good

create table play_log(
    log_id varchar(5) primary key,
    m_id varchar(5) not null,
    g_id varchar(5) not null,
    seat_no int not null,
    start_time datetime not null,
    end_time datetime
);


create view sales_search as
select s.sales_id sales_id, s.member_id member_id, s.sales_date sales_date, s.sales_time sales_time,p.p_name p_name, s.price price
from sales s, product p
where s.product_id = p.p_id;