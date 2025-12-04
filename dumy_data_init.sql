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


-- 9. Ingredient_Category (재료 카테고리) 데이터 (5개)
INSERT INTO ingredient_category (c_id, c_name) VALUES
                                                   ('IC001', '육류'), ('IC002', '채소'), ('IC003', '소스'), ('IC004', '음료베이스'), ('IC005', '기타');


-- 10. Ingredient (재료) 데이터 (100개)
DELIMITER $$
CREATE PROCEDURE InsertIngredients()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 100 DO
            INSERT INTO ingredient (i_id, c_id, i_name, total_quantity, min_quantity, is_out, store_location)
            VALUES (
                       LPAD(i, 5, '0'),
                       CONCAT('IC00', FLOOR(1 + RAND() * 5)),
                       CONCAT('재료_', i),
                       FLOOR(RAND() * 500),
                       10,
                       FALSE,
                       CONCAT('창고_', CHAR(65 + FLOOR(RAND() * 5)))
                   );
            SET i = i + 1;
        END WHILE;
END$$
DELIMITER ;
CALL InsertIngredients();
DROP PROCEDURE InsertIngredients;


-- 11. Stock_Info (재료 단위 정보) 데이터 (100개)
-- 재료 1:1 매핑
DELIMITER $$
CREATE PROCEDURE InsertStockInfo()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 100 DO
            INSERT INTO stock_info (stock_info_id, i_id, unit_name, unit_quantity)
            VALUES (
                       LPAD(i, 5, '0'),
                       LPAD(i, 5, '0'),
                       'BOX',
                       FLOOR(10 + RAND() * 50) -- 한 박스당 10~60개
                   );
            SET i = i + 1;
        END WHILE;
END$$
DELIMITER ;
CALL InsertStockInfo();
DROP PROCEDURE InsertStockInfo;


-- 12. Stock_In (입고) 데이터 (100개)
DELIMITER $$
CREATE PROCEDURE InsertStockIn()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE u_qty INT;
    WHILE i <= 100 DO
            SELECT unit_quantity INTO u_qty FROM stock_info WHERE stock_info_id = LPAD(i, 5, '0');

            INSERT INTO stock_in (in_id, i_id, stock_info_id, in_quantity, unit_price, unit_quantity)
            VALUES (
                       LPAD(i, 5, '0'),
                       LPAD(i, 5, '0'),
                       LPAD(i, 5, '0'),
                       FLOOR(1 + RAND() * 10), -- 1~10 박스 입고
                       FLOOR(5000 + RAND() * 20000), -- 단가
                       u_qty
                   );
            SET i = i + 1;
        END WHILE;
END$$
DELIMITER ;
CALL InsertStockIn();
DROP PROCEDURE InsertStockIn;


-- 13. Menu_Ingredient (메뉴 레시피) 데이터 (100개)
-- 메뉴와 재료 랜덤 매핑
DELIMITER $$
CREATE PROCEDURE InsertMenuIngredient()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 100 DO
            INSERT INTO menu_ingredient (menu_ingredient_id, m_id, i_id, required_quantity)
            VALUES (
                       LPAD(i, 5, '0'),
                       LPAD(FLOOR(1 + RAND() * 100), 5, '0'), -- 랜덤 메뉴
                       LPAD(FLOOR(1 + RAND() * 100), 5, '0'), -- 랜덤 재료
                       FLOOR(1 + RAND() * 5)
                   );
            SET i = i + 1;
        END WHILE;
END$$
DELIMITER ;
CALL InsertMenuIngredient();
DROP PROCEDURE InsertMenuIngredient;


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

INSERT INTO staff (staff_name, birth, gender, salary, phone) VALUES
     ('김민준', '1995-03-15', '남', 2500000, '010-1234-5678'),
     ('이서연', '1998-07-22', '여', 2300000, '010-2345-6789'),
     ('박지훈', '1997-11-08', '남', 2600000, '010-3456-7890'),
     ('최유나', '1999-01-30', '여', 2200000, '010-4567-8901'),
     ('정도현', '1996-05-17', '남', 2700000, '010-5678-9012'),
     ('강수진', '2000-09-25', '여', 2100000, '010-6789-0123'),
     ('윤태영', '1994-12-03', '남', 2800000, '010-7890-1234'),
     ('한지우', '1998-04-14', '여', 2400000, '010-8901-2345'),
     ('임현우', '1997-08-28', '남', 2550000, '010-9012-3456'),
     ('조은비', '1999-06-19', '여', 2250000, '010-0123-4567');
