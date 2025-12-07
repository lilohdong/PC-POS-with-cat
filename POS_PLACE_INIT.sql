DROP DATABASE IF EXISTS POS_PLACE;
create database POS_PLACE;
use POS_PLACE;

-- 회원 테이블
create table member(
    m_id VARCHAR(30) unique not null PRIMARY KEY,
    passwd varchar(30) not null,
    name VARCHAR(10) NOT NULL,
    birth DATE not null,
    sex VARCHAR(10),
    remain_time int default 0,
    phone VARCHAR(20) UNIQUE,
    join_date datetime not null default current_timestamp
);

-- 직원 테이블
CREATE TABLE staff (
   staff_id INT auto_increment PRIMARY KEY,
   staff_name VARCHAR(10) NOT NULL unique , -- 이름
   birth DATE NOT NULL, -- 생년월일
   gender ENUM('남','여') NOT NULL, -- 성별
   salary INT NOT NULL, -- 월급 (원)
   hire_date DATETIME DEFAULT CURRENT_TIMESTAMP, -- 입사일
   is_active BOOLEAN DEFAULT TRUE, -- 재직 여부 (퇴사시 FALSE)
   passwd varchar(4) not null,
   phone VARCHAR(20)
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

-- 금고 유지 테이블
CREATE TABLE IF NOT EXISTS cash_safe (
    id INT PRIMARY KEY,
    amount INT NOT NULL,
    diff_accumulate INT NOT NULL DEFAULT 0
);
-- 초기값 삽입 (한 번만)
INSERT INTO cash_safe(id, amount, diff_accumulate) VALUES(1, 0,0)ON DUPLICATE KEY UPDATE amount = amount;


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

-- 좌석-회원 정보 통합 뷰
-- 좌석 정보와 현재 앉아있는 회원의 정보를 조인
create or replace view seat_member_info_view as
select
    s.seat_no,
    s.login_time,
    m.m_id,
    m.name,
    m.birth,
    m.remain_time
from seat s
    left join member m on s.m_id = m.m_id
where s.is_used = 1;

-- 요금제 테이블
create table price_plan (
    plan_id int auto_increment primary key,
    plan_name varchar(20) not null,
    duration_min int not null,      -- 분 단위 (60, 300, 600)
    price int not null              -- 가격 (1500, 6000, 10000)
);

-- 시간 결제 로그 테이블
create table time_payment_log (
    log_id int auto_increment primary key,
    m_id varchar(30),
    plan_id int not null,
    amount int not null,
    pay_time datetime default current_timestamp,
    foreign key (m_id) references member(m_id) on update cascade on delete set null,
    foreign key (plan_id) references price_plan(plan_id)
);

-- Sales에서 사용할 time 계산 뷰
create or replace view time_sales_view as
    select tpl.log_id as log_id, tpl.m_id as m_id, pp.plan_name,tpl.pay_time as time,tpl.amount as amount, pp.price * tpl.amount as total_price
    from time_payment_log tpl, price_plan pp
    where pp.plan_id = tpl.plan_id;

/*
category(메뉴 카테고리)
menu(등록되어있는 메뉴의 정보)
orders(주문)
order_menu(주문에 있는 메뉴들)
refund(환불 기록)
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
    
    c_id varchar(5),
    foreign key(c_id) references category(c_id) on update cascade on delete set null
);

create table orders(
	o_id varchar(6) primary key,
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
	order_menu_id varchar(7) primary key,
    o_id varchar(6) not null,
    menu_id varchar(5) not null,
    quantity int not null,
    unit_price int not null, -- 이 부분은 trigger를 통해 menu.price 가져오기
    total_price int generated always as (unit_price * quantity) stored,
    
    foreign key (o_id) references orders(o_id),
    foreign key (menu_id) references menu(menu_id)
);

create table refund(
	r_id varchar(5) primary key,
    o_id varchar(6) not null,
    r_time datetime default current_timestamp,
    r_amount int not null,
    
    foreign key(o_id)references orders(o_id)
);

create table sales(
    s_id varchar(5) primary key,
    s_date date not null, -- 정산 날짜 (YYYY-MM-DD)
    total_sales int not null default 0, -- 총 매출
    card_sales int not null default 0, -- 카드 매출
    cash_sales int not null default 0, -- 현금 매출

    unique index idx_s_date (s_date)
);

-- 표에 맞추기 위해서 ingredint_category 추가
create table ingredient_category(
    c_id varchar(5) primary key,
    c_name varchar(20) not null
);
/*
ingredient(재료 정보 - 이 테이블에는 전부 단일 수량으로 표시)
stock_info(재고 단위별 단일수량 정보 저장 ex:치즈 한 박스 = 치즈50개)
stock_in(실제 재료 입고 기록 + 수량 정보)
menu_ingredient(메뉴에 소비되는 재료 정보)
*/
create table ingredient (
    i_id varchar(5) primary key,
    c_id VARCHAR(5) not null,
    i_name varchar(30) not null,
    total_quantity int default 0,
    -- 표에 맞춰 추가
    min_quantity int default 0,
    -- 표에 맞춰 추가
    is_out boolean default false,
    store_location varchar(20),
    updated_time datetime default current_timestamp on update current_timestamp,

    foreign key (c_id) references ingredient_category(c_id),
    unique key unique_name_category(i_name, c_id)
);

create table stock_info(
    stock_info_id varchar(5) primary key,
    i_id varchar(5) not null,
    unit_name varchar(20) not null,
    unit_quantity int not null,

    foreign key (i_id) references ingredient(i_id),
    unique key unique_unit_per_ingredient(i_id, unit_name)
);

create table stock_in(
    in_id varchar(5) primary key,
    i_id varchar(5),
    stock_info_id varchar(5) null,
    in_quantity int not null,
    -- 표에 맞줘 추가
    unit_price int not null default 0,
    total_added int not null default 0,
    in_time datetime default current_timestamp,

    foreign key (i_id) references ingredient(i_id) on update cascade on delete set null,
    foreign key (stock_info_id) references stock_info(stock_info_id) on update cascade on delete set null
);

-- INSERT 전에 total_added 자동 계산하는 트리거
DELIMITER $$
CREATE TRIGGER trg_stock_in_before_insert
    BEFORE INSERT ON stock_in
    FOR EACH ROW
BEGIN
    DECLARE unit_qty INT DEFAULT 1;

    IF NEW.stock_info_id IS NOT NULL THEN
        SELECT unit_quantity INTO unit_qty
        FROM stock_info
        WHERE stock_info_id = NEW.stock_info_id;

        -- 만약 stock_info_id가 잘못됐으면 NULL이 들어가므로 1로 fallback
        IF unit_qty IS NULL THEN
            SET unit_qty = 1;
        END IF;
    END IF;

    SET NEW.total_added = NEW.in_quantity * unit_qty;
END$$
DELIMITER ;

-- UPDATE 시에도 안전하게 (필요하면)
DELIMITER $$
CREATE TRIGGER trg_stock_in_before_update
    BEFORE UPDATE ON stock_in
    FOR EACH ROW
BEGIN
    DECLARE unit_qty INT DEFAULT 1;

    IF NEW.stock_info_id IS NOT NULL THEN
        SELECT unit_quantity INTO unit_qty
        FROM stock_info
        WHERE stock_info_id = NEW.stock_info_id;
        IF unit_qty IS NULL THEN SET unit_qty = 1; END IF;
    END IF;

    SET NEW.total_added = NEW.in_quantity * unit_qty;
END$$
DELIMITER ;


DELIMITER $$
CREATE TRIGGER trg_stock_in_after_insert
    AFTER INSERT ON stock_in
    FOR EACH ROW
BEGIN
    UPDATE ingredient
    SET total_quantity = total_quantity + NEW.total_added,
        is_out = CASE WHEN total_quantity + NEW.total_added > 0 THEN false ELSE true END
    WHERE i_id = NEW.i_id;
END$$
DELIMITER ;


CREATE TABLE stock_out (
    out_id VARCHAR(8) PRIMARY KEY,
    i_id  VARCHAR(5),
    out_quantity INT NOT NULL,
    out_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (i_id) REFERENCES ingredient(i_id) on update cascade on delete set null
);

create table menu_ingredient (
    menu_ingredient_id varchar(5) primary key,
    m_id varchar(5),
    i_id varchar(5),
    required_quantity int not null,

    foreign key (m_id) references menu(menu_id) on update cascade on delete set null,
    foreign key (i_id) references ingredient(i_id) on update cascade on delete set null
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
            m_id varchar(30),
            g_id varchar(5),
            seat_no int not null,
            start_time datetime not null,
            end_time datetime,
            foreign key(m_id) references member(m_id) on update cascade on delete set null,
            foreign key(g_id) references game(g_id) on update cascade on delete set null
);
-- sales에 사용할 뷰
CREATE OR REPLACE VIEW sales_view AS
SELECT
    o.o_id AS sales_id,
    o.m_id AS member_id,
    o.complete_time AS o_time,
    m.m_name AS m_name,
    om.quantity AS quantity,
    om.total_price AS total_price
FROM orders o
         JOIN order_menu om ON o.o_id = om.o_id
         JOIN menu m ON m.menu_id = om.menu_id
WHERE o.o_status = 'COMPLETED';

-- GameStatics에 사용할 뷰
CREATE OR REPLACE VIEW popular_game_view AS
WITH raw_play_data AS (
    -- 1. 모든 플레이 로그의 날짜와 플레이 시간 추출
    SELECT
        g_id,
        DATE(start_time) as play_date, -- 날짜 컬럼 생성
        timestampdiff(minute, start_time, coalesce(end_time, current_timestamp())) as play_minutes
    FROM play_log
),
     daily_game_stats AS (
         -- 2. (날짜, 게임)별 총 플레이 시간 합산
         SELECT
             rpd.play_date,
             g.g_id,
             g.title as game_name,
             SUM(rpd.play_minutes) as total_minutes
         FROM game g
                  JOIN raw_play_data rpd ON g.g_id = rpd.g_id
         GROUP BY rpd.play_date, g.g_id, g.title
     ),
     daily_total_stats AS (
         -- 3. 날짜별 전체 게임 총 플레이 시간 (분모 계산용)
         SELECT
             play_date,
             SUM(total_minutes) as overall_total
         FROM daily_game_stats
         GROUP BY play_date
     )
SELECT
    dgs.play_date, -- Java에서 이 컬럼으로 조회할 예정
    RANK() OVER (PARTITION BY dgs.play_date ORDER BY dgs.total_minutes DESC) as ranking, -- 날짜별로 순위 매기기
    dgs.game_name,
    ROUND((dgs.total_minutes / dts.overall_total) * 100, 2) as share_percent
FROM daily_game_stats dgs
         JOIN daily_total_stats dts ON dgs.play_date = dts.play_date;



CREATE OR REPLACE VIEW statistics_view AS
SELECT
    -- 1. 랭킹: 총 플레이 시간(내림차순) -> 이용자 수(내림차순)
    RANK() OVER (
        ORDER BY
            SUM(TIMESTAMPDIFF(SECOND, pl.start_time, COALESCE(pl.end_time, CURRENT_TIMESTAMP()))) DESC,
            COUNT(DISTINCT pl.m_id) DESC
        ) AS ranking,

    -- 2. 게임 이름
    g.title AS game_name,

    -- 3. 총 플레이 시간 포맷팅 (초 -> 시간:분:초)
    SEC_TO_TIME(
            SUM(TIMESTAMPDIFF(SECOND, pl.start_time, COALESCE(pl.end_time, CURRENT_TIMESTAMP())))
    ) AS total_time_formatted,

    -- 4. 이용자 수
    COUNT(DISTINCT pl.m_id) AS current_users

FROM play_log pl
         JOIN game g ON pl.g_id = g.g_id
WHERE DATE(pl.start_time) = CURRENT_DATE() -- 오늘 날짜 필터링
GROUP BY g.title;
