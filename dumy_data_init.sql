USE POS_PLACE;

-- 1. Member 데이터 (100명)
-- 한국어 이름 3글자 랜덤 생성, m_id는 member1 ~ member100
DELIMITER $$
CREATE PROCEDURE InsertMembers()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE first_names VARCHAR(100) DEFAULT '김이박최정강조윤장림한오서신권황안송류전홍고문양손배조백허유남심노하곽성차주우구신임나전민유진지엄채원천방공현함변염양변여추노소현범왕살옥석';
    DECLARE mid_names VARCHAR(100) DEFAULT '민서예지도하주윤채현지수현우건우준서준혁승현승우시우지호지후준영동현진호태현민재성민규빈재원준원우진서준예준도현건하은지아서윤서연하은지우하윤민서지유채원서현수빈다은예은지안소율시아';
    DECLARE last_names VARCHAR(100) DEFAULT '준호후윤서현지우민준현우도현건우서준예준시우주원지호준혁승현승우진우지원민재성민규빈재원준원우진서준예준도현건하은지아서윤서연하은지우하윤민서지유채원서현수빈다은예은지안소율시아';

    WHILE i <= 100 DO
            INSERT INTO member (m_id, passwd, name, birth, sex, remain_time, phone, join_date)
            VALUES (
                       CONCAT('member', i),
                       'password123',
                       CONCAT(SUBSTRING(first_names, FLOOR(1 + RAND() * 60), 1),
                              SUBSTRING(mid_names, FLOOR(1 + RAND() * 60), 1),
                              SUBSTRING(last_names, FLOOR(1 + RAND() * 60), 1)),
                       DATE_ADD('1990-01-01', INTERVAL FLOOR(RAND() * 10000) DAY),
                       IF(RAND() > 0.5, 'M', 'F'),
                       FLOOR(RAND() * 600), -- 남은 시간(초 or 분)
                       CONCAT('010-', LPAD(FLOOR(RAND() * 9999), 4, '0'), '-', LPAD(FLOOR(RAND() * 9999), 4, '0')),
                       DATE_ADD('2025-01-01', INTERVAL FLOOR(RAND() * 300) DAY)
                   );
            SET i = i + 1;
        END WHILE;
END$$
DELIMITER ;
CALL InsertMembers();
DROP PROCEDURE InsertMembers;


-- 2. Handover (인수인계) 데이터 (100개)
DELIMITER $$
CREATE PROCEDURE InsertHandover()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 100 DO
            INSERT INTO handover (giver_id, receiver_id, start_time, end_time, total_sales, cash_sales, card_sales, cash_reserve, memo)
            VALUES (
                       CONCAT('manager', FLOOR(1 + RAND() * 10)), -- 관리자라고 가정
                       CONCAT('manager', FLOOR(1 + RAND() * 10)),
                       DATE_ADD('2025-11-20', INTERVAL i * 4 HOUR),
                       DATE_ADD('2025-11-20', INTERVAL (i * 4) + 4 HOUR),
                       FLOOR(RAND() * 500000),
                       FLOOR(RAND() * 100000),
                       FLOOR(RAND() * 400000),
                       300000, -- 기본 시재
                       IF(RAND() > 0.8, '특이사항 없음', '메모 내용')
                   );
            SET i = i + 1;
        END WHILE;
END$$
DELIMITER ;
CALL InsertHandover();
DROP PROCEDURE InsertHandover;


-- 3. Seat (좌석) 데이터 (100석)
-- 일부는 사용 중인 상태로 설정
DELIMITER $$
CREATE PROCEDURE InsertSeats()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 100 DO
            IF RAND() > 0.8 THEN -- 20% 확률로 사용중
                INSERT INTO seat (seat_no, is_used, m_id, login_time)
                VALUES (i, TRUE, CONCAT('member', i), NOW());
            ELSE
                INSERT INTO seat (seat_no, is_used)
                VALUES (i, FALSE);
            END IF;
            SET i = i + 1;
        END WHILE;
END$$
DELIMITER ;
CALL InsertSeats();
DROP PROCEDURE InsertSeats;

-- 4. Category (카테고리) 데이터 (요청하신 3개 고정)
INSERT INTO category (c_id, c_name) VALUES
                                        ('C001', '음식'),
                                        ('C002', '음료수'),
                                        ('C003', '과자');


-- 5. Menu (메뉴) 데이터 (100개)
-- 카테고리 3개에 골고루 분배
DELIMITER $$
CREATE PROCEDURE InsertMenu()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE cat_id VARCHAR(5);
    WHILE i <= 100 DO
            SET cat_id = CASE FLOOR(1 + RAND() * 3)
                             WHEN 1 THEN 'C001'
                             WHEN 2 THEN 'C002'
                             ELSE 'C003'
                END;

            INSERT INTO menu (menu_id, m_name, m_price, m_description, c_id)
            VALUES (
                       LPAD(i, 5, '0'),
                       CONCAT('메뉴_', i),
                       FLOOR(1000 + RAND() * 10000),
                       '맛있는 메뉴 설명입니다.',
                       cat_id
                   );
            SET i = i + 1;
        END WHILE;
END$$
DELIMITER ;
CALL InsertMenu();
DROP PROCEDURE InsertMenu;


-- 6. Orders (주문) 데이터 (100개)
-- 2025-11-20 ~ 2025-12-02 사이 랜덤 시간
DELIMITER $$
CREATE PROCEDURE InsertOrders()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE rand_dt DATETIME;
    WHILE i <= 100 DO
            -- 날짜 범위 설정 (2025-11-20 ~ 2025-12-02)
            SET rand_dt = DATE_ADD('2025-11-20 00:00:00', INTERVAL FLOOR(RAND() * (13 * 24 * 60 * 60)) SECOND);

            INSERT INTO orders (o_id, m_id, o_time, seat_num, complete_time, o_status, requestment, pay_method)
            VALUES (
                       LPAD(i, 5, '0'),
                       CONCAT('member', FLOOR(1 + RAND() * 100)),
                       rand_dt,
                       FLOOR(1 + RAND() * 100),
                       DATE_ADD(rand_dt, INTERVAL 10 MINUTE),
                       'COMPLETED',
                       '맛있게 해주세요',
                       IF(RAND() > 0.5, 'CARD', 'CASH')
                   );
            SET i = i + 1;
        END WHILE;
END$$
DELIMITER ;
CALL InsertOrders();
DROP PROCEDURE InsertOrders;


-- 7. Order_Menu (주문 상세) 데이터 (100개)
-- 각 주문마다 메뉴 1개씩 매핑 (단순화)
DROP PROCEDURE IF EXISTS InsertOrderMenu;

DELIMITER $$
CREATE PROCEDURE InsertOrderMenu()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE v_price INT DEFAULT 0;      -- 변수명 변경 (m_price -> v_price) 및 초기화
    DECLARE v_target_m_id VARCHAR(5);   -- 변수명 변경 (m_id -> v_target_m_id)

    WHILE i <= 100 DO
            -- 1. 00001 ~ 00100 사이의 랜덤 메뉴 ID 생성
            SET v_target_m_id = LPAD(FLOOR(1 + RAND() * 100), 5, '0');

            -- 2. 변수 초기화 (이전 루프의 값 잔존 방지)
            SET v_price = NULL;

            -- 3. 해당 메뉴의 가격 조회
            SELECT m_price INTO v_price
            FROM menu
            WHERE menu_id = v_target_m_id
            LIMIT 1;

            -- 4. 방어 코드: 만약 가격을 못 가져왔다면(NULL이라면) 기본값 5000원 설정
            IF v_price IS NULL THEN
                SET v_price = 5000;
            END IF;

            -- 5. 데이터 삽입
            INSERT INTO order_menu (order_menu_id, o_id, menu_id, quantity, unit_price)
            VALUES (
                       LPAD(i, 5, '0'),
                       LPAD(i, 5, '0'), -- orders 테이블의 o_id와 1:1 매칭 가정
                       v_target_m_id,
                       FLOOR(1 + RAND() * 3),
                       v_price
                   );
            SET i = i + 1;
        END WHILE;
END$$
DELIMITER ;

-- 프로시저 실행
CALL InsertOrderMenu();



-- 8. Refund (환불) 데이터 (20개 정도만 생성)
INSERT INTO refund (r_id, o_id, r_amount)
SELECT
    LPAD(row_number() over(), 5, '0'),
    o.o_id,
    om.total_price
FROM orders o
         JOIN order_menu om ON o.o_id = om.o_id
WHERE o.o_id <= '00020'; -- 앞쪽 20개 주문에 대해 환불 처리 (예시)
UPDATE orders SET o_status = 'REFUNDED' WHERE o_id <= '00020';


-- DML 데이터 삽입
INSERT INTO ingredient_category (c_id, c_name) VALUES
                                                   ('C001', '면류'),
                                                   ('C002', '밥/곡물'),
                                                   ('C003', '육류/해산물'),
                                                   ('C004', '채소/과일'),
                                                   ('C005', '유제품/소스'),
                                                   ('C006', '음료/액상'),
                                                   ('C007', '스낵/과자'),
                                                   ('C008', '조미료/양념'),
                                                   ('C009', '기타 잡화');

INSERT INTO ingredient (i_id, c_id, i_name, total_quantity, min_quantity, is_out, store_location) VALUES
                                                                                                      ('I001', 'C001', '신라면', 150, 50, FALSE, '창고A-선반1'),
                                                                                                      ('I002', 'C002', '냉동볶음밥(새우)', 80, 30, FALSE, '냉동고B-칸2'),
                                                                                                      ('I003', 'C003', '돼지고기(대패)', 45, 10, FALSE, '냉동고A-칸1'),
                                                                                                      ('I004', 'C005', '모짜렐라치즈', 20, 5, FALSE, '냉장고C-칸3'),
                                                                                                      ('I005', 'C006', '콜라(캔 355ml)', 300, 100, FALSE, '창고A-선반4'),
                                                                                                      ('I006', 'C004', '양파', 10, 5, FALSE, '야채실'),
                                                                                                      ('I007', 'C007', '포카칩(양파맛)', 60, 20, FALSE, '매대-스낵'),
                                                                                                      ('I008', 'C002', '흰 쌀밥(즉석밥)', 200, 50, FALSE, '창고A-선반2'),
                                                                                                      ('I009', 'C006', '아이스티(분말)', 120, 40, FALSE, '창고A-선반5'),
                                                                                                      ('I010', 'C008', '간장', 5, 1, FALSE, '주방-선반1'),
                                                                                                      ('I011', 'C009', '일회용 젓가락', 1000, 200, FALSE, '창고B-잡화'),
                                                                                                      ('I012', 'C001', '우동면', 50, 15, FALSE, '냉장고A-칸1'),
                                                                                                      ('I013', 'C003', '비엔나 소시지', 90, 20, FALSE, '냉장고B-칸2');

INSERT INTO stock_info (stock_info_id, i_id, unit_name, unit_quantity) VALUES
                                                                           ('S001', 'I001', '박스(40개)', 40),
                                                                           ('S002', 'I002', '봉지(10팩)', 10),
                                                                           ('S003', 'I003', '박스(1kg/5팩)', 5),
                                                                           ('S004', 'I004', '봉지(1kg)', 1), -- 치즈 1kg 봉지 = 단일 수량 1 (단위 환산 없음)
                                                                           ('S005', 'I005', '캔 박스(30개)', 30),
                                                                           ('S006', 'I006', '망(10개)', 10),
                                                                           ('S007', 'I007', '박스(20개)', 20),
                                                                           ('S008', 'I008', '박스(24개)', 24),
                                                                           ('S009', 'I009', '봉지(1kg/60인분)', 60),
                                                                           ('S010', 'I010', '통(1.8L)', 1), -- 간장은 1통이 단일 수량 1 (단위 환산 없음)
                                                                           ('S011', 'I011', '박스(500쌍)', 500),
                                                                           ('S012', 'I012', '팩(10개)', 10),
                                                                           ('S013', 'I013', '팩(10개)', 10);


-- in_time은 current_timestamp로 자동 설정됩니다.
-- total_added는 트리거에 의해 in_quantity * unit_quantity로 계산됩니다.

INSERT INTO stock_in (in_id, i_id, stock_info_id, in_quantity, unit_price) VALUES
                                                                               ('IN001', 'I001', 'S001', 3, 20000), -- 신라면 3박스 (3 * 40 = 120개 추가)
                                                                               ('IN002', 'I005', 'S005', 5, 15000), -- 콜라 5박스 (5 * 30 = 150개 추가)
                                                                               ('IN003', 'I002', 'S002', 4, 18000), -- 볶음밥 4봉지 (4 * 10 = 40개 추가)
                                                                               ('IN004', 'I004', 'S004', 20, 7000), -- 치즈 20봉지 (20 * 1 = 20개 추가)
                                                                               ('IN005', 'I007', 'S007', 3, 12000), -- 포카칩 3박스 (3 * 20 = 60개 추가)
                                                                               ('IN006', 'I006', 'S006', 1, 8000), -- 양파 1망 (1 * 10 = 10개 추가)
                                                                               ('IN007', 'I003', 'S003', 9, 25000), -- 대패삼겹 9박스 (9 * 5 = 45개 추가)
                                                                                ('IN008', 'I008', 'S008', 5, 28000),  -- 즉석밥 5박스 (5 * 24 = 120개 추가)
                                                                                ('IN009', 'I009', 'S009', 2, 22000),  -- 아이스티 분말 2봉지 (2 * 60 = 120인분 추가)
                                                                                ('IN010', 'I011', 'S011', 2, 18000),  -- 젓가락 2박스 (2 * 500 = 1000쌍 추가)
                                                                                ('IN011', 'I013', 'S013', 8, 15000),  -- 소시지 8팩 (8 * 10 = 80개 추가)
                                                                                ('IN012', 'I010', 'S010', 1, 9000);   -- 간장 1통 (1 * 1 = 1개 추가)

-- out_time은 current_timestamp로 자동 설정됩니다.

INSERT INTO stock_out (out_id, i_id, out_quantity) VALUES
                                                       ('OUT001', 'I001', 5),  -- 신라면 5개 소비
                                                       ('OUT002', 'I005', 10), -- 콜라 10개 소비
                                                       ('OUT003', 'I002', 3),  -- 볶음밥 3개 소비
                                                       ('OUT004', 'I004', 1),  -- 치즈 1개 소비
                                                       ('OUT005', 'I003', 2),  -- 대패삼겹 2개 소비
                                                        ('OUT006', 'I008', 15),  -- 흰 쌀밥 15개 소비
                                                        ('OUT007', 'I009', 20),  -- 아이스티 20인분 소비
                                                        ('OUT008', 'I011', 50),  -- 젓가락 50쌍 소비
                                                        ('OUT009', 'I013', 5),   -- 비엔나 소시지 5개 소비
                                                        ('OUT010', 'I012', 3);   -- 우동면 3개 소비
-- 13. Menu_Ingredient (메뉴 레시피) 데이터 (100개)


-- 14. Game (게임) 데이터 (20개)
-- 100개까지는 필요 없어 보이나 요청에 의해 최대한 생성하되, 유명 게임 위주로
INSERT INTO game (g_id, title, publisher) VALUES
                                              ('G001', 'League of Legends', 'Riot Games'),
                                              ('G002', 'PUBG', 'Krafton'),
                                              ('G003', 'Overwatch 2', 'Blizzard'),
                                              ('G004', 'Valorant', 'Riot Games'),
                                              ('G005', 'FIFA Online 4', 'Nexon'),
                                              ('G006', 'Sudden Attack', 'Nexon'),
                                              ('G007', 'Lost Ark', 'Smilegate'),
                                              ('G008', 'StarCraft', 'Blizzard'),
                                              ('G009', 'MapleStory', 'Nexon'),
                                              ('G010', 'Dungeon & Fighter', 'Nexon'),
                                              ('G011', 'Minecraft', 'Mojang'),
                                              ('G012', 'GTA V', 'Rockstar'),
                                              ('G013', 'Apex Legends', 'EA'),
                                              ('G014', 'World of Warcraft', 'Blizzard'),
                                              ('G015', 'Diablo 4', 'Blizzard'),
                                              ('G016', 'Among Us', 'Innersloth'),
                                              ('G017', 'Roblox', 'Roblox Corp'),
                                              ('G018', 'Eternal Return', 'Nimble Neuron'),
                                              ('G019', 'Cyphers', 'Neople'),
                                              ('G020', 'KartRider: Drift', 'Nexon');
-- 나머지 80개는 더미로 채움
DELIMITER $$
CREATE PROCEDURE InsertMoreGames()
BEGIN
    DECLARE i INT DEFAULT 21;
    WHILE i <= 100 DO
            INSERT INTO game (g_id, title, publisher)
            VALUES (
                       CONCAT('G', LPAD(i, 3, '0')),
                       CONCAT('Steam Game ', i),
                       'Indie Dev'
                   );
            SET i = i + 1;
        END WHILE;
END$$
DELIMITER ;
CALL InsertMoreGames();
DROP PROCEDURE InsertMoreGames;


-- 15. Play_Log (게임 플레이 기록) 데이터 (100개)
-- 오늘 날짜 포함 최근 데이터 생성
DELIMITER $$
CREATE PROCEDURE InsertPlayLog()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE start_dt DATETIME;
    WHILE i <= 100 DO
            SET start_dt = DATE_ADD(NOW(), INTERVAL -FLOOR(RAND() * 24) HOUR);

            INSERT INTO play_log (log_id, m_id, g_id, seat_no, start_time, end_time)
            VALUES (
                       CONCAT('LOG', LPAD(i, 5, '0')),
                       CONCAT('member', FLOOR(1 + RAND() * 100)),
                       CONCAT('G', LPAD(FLOOR(1 + RAND() * 20), 3, '0')), -- 상위 20개 인기게임 위주
                       FLOOR(1 + RAND() * 100),
                       start_dt,
                       DATE_ADD(start_dt, INTERVAL FLOOR(10 + RAND() * 120) MINUTE) -- 10~130분 플레이
                   );
            SET i = i + 1;
        END WHILE;
END$$
DELIMITER ;
CALL InsertPlayLog();
DROP PROCEDURE InsertPlayLog;


-- 16. 요금제 데이터 입력
INSERT INTO price_plan (plan_name, duration_min, price) VALUES
    ('1시간', 60, 1500),
    ('3시간', 180, 4000),
    ('5시간', 300, 6000),
    ('10시간', 600, 10000);
