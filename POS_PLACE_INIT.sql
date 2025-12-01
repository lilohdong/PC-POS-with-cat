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
create table seat (
    seat_no int primary key, -- 좌석 번호
    m_id VARCHAR(30) unique, -- 현재 사용자 (null이면 빈 좌석)
    is_used boolean default false, -- 사용 중 여부
    login_time datetime default null, -- 로그인 시간
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
    c_id VARCHAR(5) not null, --표에 맞줘 추가
    i_name varchar(30) not null,
    total_quantity int default 0,
    min_quantity int default 0, --표에 맞줘 추가
    is_out boolean default false,
    store_location varchar(20), --표에 맞줘 추가
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
    stock_in_id varchar(5) primary key,
    i_id varchar(5) not null,
    stock_info_id varchar(5) not null,
    in_quantity int not null,
    unit_price int not null default 0, --표에 맞줘 추가
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
            log_id varchar(5) primary key,
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

create view statistics_view as
with current_playing as (
    -- 1. 현재 사용자들이 플레이 중인 게임 목록 (seat 테이블 기준)
    select
        s.m_id,
        g.g_id,
        g.title as game_name
    from seat s
             join play_log pl on s.m_id = pl.m_id -- 현재 로그인한 회원의 최근 플레이 기록
             join game g on pl.g_id = g.g_id
    where s.is_used = true -- 사용 중인 좌석
      and pl.end_time is null -- 아직 종료되지 않은 플레이 기록
      and date(pl.start_time) = current_date() -- 오늘 시작된 기록
    group by s.m_id, g.g_id, g.title
),
     game_users_count as (
         -- 2. 게임별 현재 이용자 수
         select
             game_name,
             count(m_id) as current_users
         from current_playing
         group by game_name
     ),
     today_total_time as (
         -- 3. 오늘 각 게임의 총 사용 시간 (play_log 기준)
         select
             g.title as game_name,
             -- 초 단위로 합산
             sum(timestampdiff(second, pl.start_time, coalesce(pl.end_time, current_timestamp()))) as total_seconds
         from play_log pl
                  join game g on pl.g_id = g.g_id
         where date(pl.start_time) = current_date()
         group by g.title
     )
select
    -- 현재 이용자 수를 기준으로 순위 매김 (동일 인원 시, 총 사용 시간 긴 게임이 상위)
    rank() over (order by guc.current_users desc, ttt.total_seconds desc) as ranking,
    guc.game_name,
    -- 총 사용 시간을 H:M:S 포맷으로 변환 (DAO에서 변환해야 하지만, 뷰에서 문자열로 준비)
    sec_to_time(ttt.total_seconds) as total_time_formatted,
    guc.current_users as current_users
from game_users_count guc
         join today_total_time ttt on guc.game_name = ttt.game_name
order by ranking;