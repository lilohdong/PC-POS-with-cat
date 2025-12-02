USE POS_PLACE;

-- 1. 데이터 초기화
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE order_menu;
TRUNCATE TABLE category;
TRUNCATE TABLE refund;
TRUNCATE TABLE orders;
TRUNCATE TABLE stock_in;
TRUNCATE TABLE stock_info;
TRUNCATE TABLE menu_ingredient;
TRUNCATE TABLE ingredient;
TRUNCATE table ingredient_category;
TRUNCATE TABLE menu;
TRUNCATE TABLE seat;
truncate table game;
TRUNCATE TABLE play_log;
TRUNCATE TABLE member;
TRUNCATE TABLE handover;
SET FOREIGN_KEY_CHECKS = 1;

-- 2. 프로시저 재생성
DROP PROCEDURE IF EXISTS generate_dummy_data;

DELIMITER $$

CREATE PROCEDURE generate_dummy_data()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE j INT DEFAULT 1;
    DECLARE v_m_id VARCHAR(30);
    DECLARE v_o_id VARCHAR(5);
    DECLARE v_menu_id VARCHAR(5);
    DECLARE v_g_id VARCHAR(5);

    -- 1. Member
    SET i = 1;
    WHILE i <= 500 DO
            INSERT INTO member (m_id, passwd, name, birth, sex, remain_time, phone, join_date)
            VALUES (CONCAT('user', LPAD(i, 3, '0')), 'pass1234', CONCAT('Member', i), DATE_ADD('1990-01-01', INTERVAL FLOOR(RAND() * 10000) DAY), IF(RAND() > 0.5, 'Male', 'Female'), FLOOR(RAND() * 600), CONCAT('010-', LPAD(FLOOR(RAND() * 9999), 4, '0'), '-', LPAD(FLOOR(RAND() * 9999), 4, '0')), DATE_ADD('2023-01-01', INTERVAL FLOOR(RAND() * 365) DAY));
            SET i = i + 1;
        END WHILE;

    -- 2. Handover
    SET i = 1;
    WHILE i <= 500 DO
            INSERT INTO handover (giver_id, receiver_id, start_time, end_time, total_sales, cash_sales, card_sales, cash_reserve, memo)
            VALUES ('admin', 'parttimer', DATE_ADD('2023-01-01 09:00:00', INTERVAL i DAY), DATE_ADD('2023-01-01 18:00:00', INTERVAL i DAY), FLOOR(RAND() * 500000), FLOOR(RAND() * 100000), FLOOR(RAND() * 400000), 300000, IF(RAND() > 0.8, '특이사항 없음', '정산 확인 요망'));
            SET i = i + 1;
        END WHILE;

    -- 3. Seat
    SET i = 1;
    WHILE i <= 100 DO
            INSERT INTO seat (seat_no, is_used, m_id, login_time) VALUES (i, FALSE, NULL, NULL);
            SET i = i + 1;
        END WHILE;
    -- 좌석 사용 (중복 방지 순차 할당)
    SET i = 1;
    WHILE i <= 20 DO
            UPDATE seat SET is_used = TRUE, m_id = CONCAT('user', LPAD(i, 3, '0')), login_time = NOW() WHERE seat_no = i;
            SET i = i + 1;
        END WHILE;

    -- 4. Category
    INSERT INTO category VALUES ('C01', 'Food'), ('C02', 'Beverage'), ('C03', 'Snack');

    -- 5. Menu
    SET i = 1;
    WHILE i <= 50 DO
            INSERT INTO menu (menu_id, m_name, m_price, m_description, c_id) VALUES (CONCAT('M', LPAD(i, 3, '0')), CONCAT('Menu_Name_', i), FLOOR(RAND() * 10 + 1) * 1000, 'Delicious menu description', CONCAT('C0', FLOOR(RAND() * 3) + 1));
            SET i = i + 1;
        END WHILE;

    -- 6. Orders (우회 입력 적용됨)
    SET i = 1;
    WHILE i <= 500 DO
            SET v_m_id = CONCAT('user', LPAD(FLOOR(RAND() * 500) + 1, 3, '0'));
            INSERT INTO orders (o_id, m_id,o_time ,seat_num, o_status, requestment, pay_method)
            VALUES (
                       CONCAT('O', LPAD(i, 4, '0')),
                       v_m_id,
                       FROM_UNIXTIME(
                               UNIX_TIMESTAMP('2025-11-20 00:00:00') +
                               FLOOR(RAND() * (UNIX_TIMESTAMP('2025-12-02 23:59:59') - UNIX_TIMESTAMP('2025-11-20 00:00:00')))
                       ),
                       FLOOR(RAND() * 100) + 1,
                       'COMPLETED',
                       IF(RAND() > 0.9, '소스 많이 주세요', ''),
                       -- [수정됨] CONCAT으로 문자열을 합쳐서 입력
                       IF(RAND() > 0.5, CONCAT('CARD'), 'CASH')
                   );
            SET i = i + 1;
        END WHILE;

    -- 7. Order_Menu
    SET i = 1;
    WHILE i <= 500 DO
            SET v_o_id = CONCAT('O', LPAD(i, 4, '0'));
            SET v_menu_id = CONCAT('M', LPAD(FLOOR(RAND() * 50) + 1, 3, '0'));
            INSERT INTO order_menu (order_menu_id, o_id, menu_id, quantity, unit_price) VALUES (CONCAT('O', LPAD(i, 4, '0')), v_o_id, v_menu_id, FLOOR(RAND() * 3) + 1, (SELECT m_price FROM menu WHERE menu_id = v_menu_id));
            SET i = i + 1;
        END WHILE;

    -- 8, 9, 10, 11, 12. Ingredient & Stock
    INSERT INTO ingredient_category VALUES ('IC01', 'Meat'), ('IC02', 'Vegetable'), ('IC03', 'Sauce');

    SET i = 1;
    WHILE i <= 50 DO
            INSERT INTO ingredient (i_id, c_id, i_name, total_quantity, min_quantity, store_location) VALUES (CONCAT('I', LPAD(i, 3, '0')), CONCAT('IC0', FLOOR(RAND() * 3) + 1), CONCAT('Ingredient_', i), FLOOR(RAND() * 100), 10, CONCAT('Shelf-', CHAR(65 + FLOOR(RAND() * 5))));
            INSERT INTO stock_info (stock_info_id, i_id, unit_name, unit_quantity) VALUES (CONCAT('SI', LPAD(i, 3, '0')), CONCAT('I', LPAD(i, 3, '0')), 'Box', 10 + FLOOR(RAND() * 20));
            INSERT INTO menu_ingredient (menu_ingredient_id, m_id, i_id, required_quantity) VALUES (CONCAT('MI', LPAD(i, 3, '0')), CONCAT('M', LPAD(i, 3, '0')), CONCAT('I', LPAD(i, 3, '0')), 1);
            SET i = i + 1;
        END WHILE;

    SET i = 1;
    WHILE i <= 500 DO
            SET j = FLOOR(RAND() * 50) + 1;
            INSERT INTO stock_in (in_id, i_id, stock_info_id, in_quantity, unit_price, unit_quantity) VALUES (CONCAT('IN', LPAD(i, 3, '0')), CONCAT('I', LPAD(j, 3, '0')), CONCAT('SI', LPAD(j, 3, '0')), FLOOR(RAND() * 5) + 1, FLOOR(RAND() * 50) * 1000, (SELECT unit_quantity FROM stock_info WHERE stock_info_id = CONCAT('SI', LPAD(j, 3, '0'))));
            SET i = i + 1;
        END WHILE;

    -- 13, 14. Game & Play Log
    INSERT INTO game VALUES ('G001', 'League of Legends', 'Riot Games');
    INSERT INTO game VALUES ('G002', 'FIFA Online 4', 'Nexon');
    INSERT INTO game VALUES ('G003', 'Overwatch 2', 'Blizzard');
    INSERT INTO game VALUES ('G004', 'Valorant', 'Riot Games');
    INSERT INTO game VALUES ('G005', 'PUBG', 'Krafton');
    SET i = 6;
    WHILE i <= 20 DO
            INSERT INTO game VALUES (CONCAT('G', LPAD(i, 3, '0')), CONCAT('Steam Game ', i), 'Steam');
            SET i = i + 1;
        END WHILE;

    SET i = 1;
    WHILE i <= 500 DO
            SET v_m_id = CONCAT('user', LPAD(FLOOR(RAND() * 500) + 1, 3, '0'));
            SET v_g_id = IF(i <= 5, CONCAT('G00', i), CONCAT('G00', FLOOR(RAND() * 5) + 1));
            INSERT INTO play_log (log_id, m_id, g_id, seat_no, start_time, end_time) VALUES (CONCAT('L', LPAD(i, 4, '0')), v_m_id, v_g_id, FLOOR(RAND() * 100) + 1, DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 100) HOUR), DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 90) HOUR));
            SET i = i + 1;
        END WHILE;

    -- 15. Refund
    SET i = 1;
    WHILE i <= 50 DO
            SET v_o_id = CONCAT('O', LPAD(i, 4, '0'));
            UPDATE orders SET o_status = 'REFUNDED' WHERE o_id = v_o_id;
            INSERT INTO refund (r_id, o_id, r_amount) VALUES (CONCAT('R', LPAD(i, 3, '0')), v_o_id, 5000);
            SET i = i + 1;
        END WHILE;

END$$

DELIMITER ;

-- 3. 실행
CALL generate_dummy_data();

-- 4. 확인
SELECT '데이터 생성 성공!' as msg, (SELECT COUNT(*) FROM orders) as order_count;
