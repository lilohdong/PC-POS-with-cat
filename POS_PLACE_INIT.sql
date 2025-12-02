DROP DATABASE IF EXISTS POS_PLACE;
create database POS_PLACE;
use POS_PLACE;

-- 기본: 회원 테이블
create table member(
    -- 멤버 no 뺐습니다
    m_id VARCHAR(30) unique not null PRIMARY KEY,
    passwd varchar(30) not null,
    name VARCHAR(10) NOT NULL,
    birth DATE not null,
    sex VARCHAR(10),
    remain_time int default 0,
    phone VARCHAR(20) UNIQUE,
    join_date datetime not null default current_timestamp
);

-- 인수인계 테이블
CREATE TABLE handover (
    ho_id INT AUTO_INCREMENT PRIMARY KEY,      -- 인수인계 고유 번호
    giver_id VARCHAR(30) NOT NULL,             -- 인계자 (주는 사람) ID
    receiver_id VARCHAR(30) NOT NULL,          -- 인수자 (받는 사람) ID

    start_time DATETIME NOT NULL,              -- 정산 시작 시간
    end_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 정산 종료 시간 (현재 인수인계 시점)

    total_sales INT DEFAULT 0,                 -- 해당 기간 동안의 총 매출액
    cash_sales INT DEFAULT 0,                  -- 현금 매출
    card_sales INT DEFAULT 0,                  -- 카드 매출

    cash_reserve INT DEFAULT 0,                -- 현재 금고에 있는 현금 시재 (준비금 + 현금매출 - 지출)

    memo VARCHAR(200)                          -- 특이사항 메모
);

-- 좌석 정보 저장 테이블
create table seat (
    seat_no int primary key,
    is_used boolean default false,
    m_id VARCHAR(30) unique,
    login_time datetime default null,
    end_time datetime default null,
    is_unavailable boolean default false,
    foreign key(m_id) references member(m_id) on update cascade on delete set null
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
    -- 메뉴 아이디 구분 힘들어서 바꿈
    -- 멤버 아이디 -> m_id, 메뉴 아이디 -> menu_id
	menu_id varchar(5) primary key,
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
    m_id varchar(30), -- 필요에 의한 추가, 누가 주문했는지 알아야 함
    o_time datetime default current_timestamp,
    seat_num int not null,
    complete_time datetime default null,
    o_status enum('PREPARING','COMPLETED','CANCELED','REFUNDED') default 'PREPARING',
	requestment text,
    pay_method enum('CARD','CASH') default 'CARD',
    foreign key(m_id) references member(m_id) on update cascade on delete set null
);

create table order_menu(
	order_menu_id varchar(5) primary key,
    o_id varchar(5) not null,
    menu_id varchar(5) not null,
    quantity int not null,
    
    unit_price int not null, -- 이 부분은 trigger를 통해 menu.price 가져오기
    total_price int generated always as (unit_price * quantity) stored,
    
    foreign key (o_id) references orders(o_id),
    foreign key (menu_id) references menu(menu_id)
);

create table refund(
	r_id varchar(5) primary key,
    o_id varchar(5) not null,
    r_time datetime default current_timestamp,
    r_amount int not null,
    
    foreign key(o_id)references orders(o_id)
);

-- 표에 맞추기 위해서 ingredint_category 추가
create table ingredient_category(
    c_id varchar(5) primary key,
    c_name varchar(20) not null
);

create table ingredient (
    i_id varchar(5) primary key,
    c_id VARCHAR(5) not null,
    i_name varchar(30) not null,
    -- 표에 맞춰 추가
    total_quantity int default 0,
    -- 표에 맞춰 추가
    min_quantity int default 0,
    -- 표에 맞춰 추가
    is_out boolean default false,
    store_location varchar(20),
    updated_time datetime default current_timestamp on update current_timestamp,

    foreign key (c_id) references ingredient_category(c_id)
);

create table stock_info(
    stock_info_id varchar(5) primary key,
    i_id varchar(5) not null,
    unit_name varchar(20) not null,
    unit_quantity int not null,

    foreign key (i_id) references ingredient(i_id)
);

create table stock_in(
    in_id varchar(5) primary key,
    i_id varchar(5) not null,
    stock_info_id varchar(5) not null,
    in_quantity int not null,
    -- 표에 맞줘 추가
    unit_price int not null default 0,
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

    foreign key (m_id) references menu(menu_id),
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
            log_id varchar(10) primary key,
            m_id varchar(30) not null,
            g_id varchar(5) not null,
            seat_no int not null,
            start_time datetime not null,
            end_time datetime,
            foreign key(m_id) references member(m_id),
            foreign key(g_id) references game(g_id)
);
-- sales에 사용할 뷰
create view sales_view as
select o.o_id sales_id, o.m_id member_id, o.o_time as o_time, m.m_name as m_name, om.quantity as quantity, om.total_price as total_price
from orders o, menu m, order_menu om
where o.o_id = om.o_id and m.menu_id = om.menu_id and o.o_status = 'COMPLETED';
-- GameStatics에 사용할 뷰
create view popular_game_view as
with today_play_time as (
    -- 1. 오늘 종료된 게임들의 총 플레이 시간 (분 단위)
    select
        g_id,
        timestampdiff(minute, start_time, coalesce(end_time, current_timestamp())) as play_minutes
    from play_log
    where date(start_time) = current_date()
),
     game_total_time as (
         -- 2. 각 게임별 총 플레이 시간
         select
             g.g_id,
             g.title as game_name,
             sum(tpt.play_minutes) as total_minutes
         from game g
                  join today_play_time tpt on g.g_id = tpt.g_id
         group by g.g_id, g.title
     ),
     total_daily_minutes as (
         -- 3. 오늘 전체 게임의 총 플레이 시간
         select sum(total_minutes) as overall_total from game_total_time
     )
select
    rank() over (order by gtt.total_minutes desc) as ranking, -- 순위
    gtt.game_name,
    -- 점유율 계산 (총 플레이 시간 / 전체 시간 * 100)
    round((gtt.total_minutes / (select overall_total from total_daily_minutes)) * 100, 2) as share_percent
from game_total_time gtt
where (select overall_total from total_daily_minutes) > 0;

CREATE OR REPLACE VIEW statistics_view AS
SELECT
    -- 1. 플레이시간 긴 순 -> 이용자 순
    RANK() OVER (
        ORDER BY SUM(TIMESTAMPDIFF(SECOND, pl.start_time, COALESCE(pl.end_time, CURRENT_TIMESTAMP())),COUNT(DISTINCT pl.m_id) DESC) DESC
        ) AS ranking,

    -- 2. 게임 이름
    g.title AS game_name,

    -- 3. 총 플레이 시간 포맷팅 (초 -> 시간:분:초)
    SEC_TO_TIME(
            SUM(TIMESTAMPDIFF(SECOND, pl.start_time, COALESCE(pl.end_time, CURRENT_TIMESTAMP())))
    ) AS total_time_formatted,

    COUNT(DISTINCT pl.m_id) AS current_users

FROM play_log pl
         JOIN game g ON pl.g_id = g.g_id
WHERE DATE(pl.start_time) = CURRENT_DATE() -- 오늘 시작한 기록만 대상
GROUP BY g.title
ORDER BY ranking;
