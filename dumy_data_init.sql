USE POS_PLACE;

-- 1. 게임 데이터 (10개)
INSERT INTO game (g_id, title, publisher) VALUES
                                              ('G001', 'League of Legends', 'Riot Games'),
                                              ('G002', 'FC Online', 'Nexon'),
                                              ('G003', 'Valorant', 'Riot Games'),
                                              ('G004', 'PUBG: 배틀그라운드', '크래프톤'),
                                              ('G005', 'Overwatch 2', 'Blizzard'),
                                              ('G006', '서든어택', 'Nexon'),
                                              ('G007', '로스트아크', 'Smilegate'),
                                              ('G008', '메이플스토리', 'Nexon'),
                                              ('G009', 'StarCraft', 'Blizzard'),
                                              ('G010', '던전앤파이터', 'Nexon');

-- 2. 카테고리 (4개)
INSERT INTO category VALUES
                         ('C001', '라면류'), ('C002', '밥류'), ('C003', '음료'), ('C004', '과자');

-- 3. 메뉴 (10개)
INSERT INTO menu (menu_id, m_name, m_price, c_id, m_description) VALUES
                                                                     ('M001', '신라면', 4000, 'C001', '매콤한 기본 라면'),
                                                                     ('M002', '짜파게티', 4500, 'C001', '계란 후라이 추가 가능'),
                                                                     ('M003', '참치마요덮밥', 6500, 'C002', '참치 듬뿍'),
                                                                     ('M004', '제육덮밥', 7000, 'C002', '든든한 한끼'),
                                                                     ('M005', '아이스아메리카노', 2500, 'C003', '카페인 충전'),
                                                                     ('M006', '콜라(캔)', 1500, 'C003', '시원한 탄산'),
                                                                     ('M007', '사이다(캔)', 1500, 'C003', '청량한 맛'),
                                                                     ('M008', '웰치스', 1500, 'C003', '포도맛 탄산'),
                                                                     ('M009', '포카칩', 2000, 'C004', '감자 과자'),
                                                                     ('M010', '홈런볼', 2000, 'C004', '초코 과자');

-- 4. 재료 및 재고 (기본 세팅)
INSERT INTO ingredient (i_id, i_name, total_quantity) VALUES
                                                          ('I001', '라면사리', 100), ('I002', '단무지', 50), ('I003', '콜라캔', 100);

INSERT INTO stock_info (stock_info_id, i_id, unit_name, unit_quantity) VALUES
                                                                           ('S001', 'I001', '박스(30개)', 30), ('S002', 'I003', '박스(24개)', 24);

INSERT INTO stock_in (stock_in_id, i_id, stock_info_id, in_quantity, unit_quantity) VALUES
    ('SI001', 'I001', 'S001', 5, 30); -- 라면 150개 입고

-- 5. 회원 (20명 생성)
INSERT INTO member (m_id, passwd, name, birth, sex, phone, remain_time) VALUES
                                                                            ('user01', '1234', '김철수', '2000-01-01', '남', '010-1111-0001', 12000),
                                                                            ('user02', '1234', '이영희', '1998-05-05', '여', '010-1111-0002', 5000),
                                                                            ('user03', '1234', '박지성', '2002-03-10', '남', '010-1111-0003', 300),
                                                                            ('user04', '1234', '손흥민', '1995-07-08', '남', '010-1111-0004', 60000),
                                                                            ('user05', '1234', '김연아', '1990-09-05', '여', '010-1111-0005', 0),
                                                                            ('user06', '1234', '페이커', '1996-05-07', '남', '010-1111-0006', 99999),
                                                                            ('user07', '1234', '쵸비', '2001-03-03', '남', '010-1111-0007', 4000),
                                                                            ('user08', '1234', '쇼메이커', '2000-07-22', '남', '010-1111-0008', 3500),
                                                                            ('user09', '1234', '데프트', '1996-10-23', '남', '010-1111-0009', 2000),
                                                                            ('user10', '1234', '케리아', '2002-10-14', '남', '010-1111-0010', 8000),
                                                                            ('test01', '1234', '테스터1', '2000-01-01', '남', '010-2222-0001', 3000),
                                                                            ('test02', '1234', '테스터2', '2000-01-01', '여', '010-2222-0002', 3000),
                                                                            ('test03', '1234', '테스터3', '2000-01-01', '남', '010-2222-0003', 3000),
                                                                            ('test04', '1234', '테스터4', '2000-01-01', '여', '010-2222-0004', 3000),
                                                                            ('test05', '1234', '테스터5', '2000-01-01', '남', '010-2222-0005', 3000),
                                                                            ('guest1', '1234', '비회원1', '1900-01-01', '무', NULL, 0),
                                                                            ('guest2', '1234', '비회원2', '1900-01-01', '무', NULL, 0),
                                                                            ('admin', '1234', '관리자', '1990-01-01', '남', '010-0000-0000', 999999),
                                                                            ('zombie', '1234', '잠수부', '2000-01-01', '남', '010-9999-9999', 10),
                                                                            ('newbie', '1234', '뉴비', '2010-01-01', '여', '010-8888-8888', 60);

-- 6. 좌석 (50개 생성)
-- 일단 빈 좌석 생성
DELIMITER $$
DROP PROCEDURE IF EXISTS CreateSeats$$
CREATE PROCEDURE CreateSeats()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 50 DO
            INSERT INTO seat (seat_no, m_id, is_used, login_time) VALUES (i, NULL, false, NULL);
            SET i = i + 1;
        END WHILE;
END$$
DELIMITER ;
CALL CreateSeats();

-- -----------------------------------------------------------
-- [중요] 실시간 통계 테스트를 위한 "현재 접속 중인 상황" 연출
-- -----------------------------------------------------------

-- 좌석 1~5번에 회원을 앉힙니다.
UPDATE seat SET m_id = 'user01', is_used = true, login_time = DATE_SUB(NOW(), INTERVAL 120 MINUTE) WHERE seat_no = 1;
UPDATE seat SET m_id = 'user02', is_used = true, login_time = DATE_SUB(NOW(), INTERVAL 45 MINUTE) WHERE seat_no = 2;
UPDATE seat SET m_id = 'user06', is_used = true, login_time = DATE_SUB(NOW(), INTERVAL 200 MINUTE) WHERE seat_no = 3;
UPDATE seat SET m_id = 'user10', is_used = true, login_time = DATE_SUB(NOW(), INTERVAL 10 MINUTE) WHERE seat_no = 4;
UPDATE seat SET m_id = 'test01', is_used = true, login_time = DATE_SUB(NOW(), INTERVAL 5 MINUTE) WHERE seat_no = 5;

-- 접속 중인 회원들의 "끝나지 않은(end_time IS NULL)" Play Log를 생성합니다. (그래야 statistics_view에 잡힘)
INSERT INTO play_log (log_id, m_id, g_id, seat_no, start_time, end_time) VALUES
                                                                             ('LNOW1', 'user01', 'G001', 1, DATE_SUB(NOW(), INTERVAL 110 MINUTE), NULL), -- 롤 110분째 플레이 중
                                                                             ('LNOW2', 'user02', 'G004', 2, DATE_SUB(NOW(), INTERVAL 40 MINUTE), NULL),  -- 배그 40분째 플레이 중
                                                                             ('LNOW3', 'user06', 'G001', 3, DATE_SUB(NOW(), INTERVAL 190 MINUTE), NULL), -- 롤 190분째 플레이 중 (롤 현재 2명)
                                                                             ('LNOW4', 'user10', 'G003', 4, DATE_SUB(NOW(), INTERVAL 5 MINUTE), NULL),   -- 발로란트 5분째 플레이 중
                                                                             ('LNOW5', 'test01', 'G002', 5, DATE_SUB(NOW(), INTERVAL 3 MINUTE), NULL);   -- 피파 3분째 플레이 중

-- -----------------------------------------------------------
-- [중요] 인기 차트(Sales, Popular View)를 위한 "과거 기록" 연출
-- -----------------------------------------------------------

-- 오늘 이미 종료된 게임 기록들 (popular_game_view용)
INSERT INTO play_log (log_id, m_id, g_id, seat_no, start_time, end_time) VALUES
                                                                             ('L001', 'user03', 'G001', 10, '2025-01-01 10:00:00', '2025-01-01 12:00:00'), -- 롤 2시간
                                                                             ('L002', 'user04', 'G001', 11, '2025-01-01 10:00:00', '2025-01-01 13:00:00'), -- 롤 3시간
                                                                             ('L003', 'user05', 'G002', 12, '2025-01-01 09:00:00', '2025-01-01 10:00:00'), -- 피파 1시간
                                                                             ('L004', 'user07', 'G003', 13, '2025-01-01 11:00:00', '2025-01-01 12:30:00'), -- 발로란트 1.5시간
                                                                             ('L005', 'user08', 'G001', 14, '2025-01-01 08:00:00', '2025-01-01 12:00:00'), -- 롤 4시간 (롤 압도적 1위 예상)
                                                                             ('L006', 'user09', 'G004', 15, '2025-01-01 14:00:00', '2025-01-01 16:00:00'), -- 배그 2시간
                                                                             ('L007', 'test02', 'G005', 20, '2025-01-01 15:00:00', '2025-01-01 16:00:00'), -- 오버워치 1시간
                                                                             ('L008', 'test03', 'G006', 21, '2025-01-01 12:00:00', '2025-01-01 13:00:00'), -- 서든 1시간
                                                                             ('L009', 'test04', 'G008', 22, '2025-01-01 10:00:00', '2025-01-01 15:00:00'), -- 메이플 5시간
                                                                             ('L010', 'test05', 'G001', 23, '2025-01-01 18:00:00', '2025-01-01 19:00:00'), -- 롤 1시간 추가
-- 어제 기록 (오늘 통계에는 안 잡혀야 정상)
                                                                             ('L011', 'user01', 'G001', 1, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 23 HOUR));

-- 8. 주문 기록 (Sales View 및 매출 통계용)
-- 완료된 주문들
INSERT INTO orders (o_id, m_id, o_time, seat_num, complete_time, o_status, pay_method) VALUES
                                                                                           ('O001', 'user01', NOW(), 1, NOW(), 'COMPLETED', 'CARD'),
                                                                                           ('O002', 'user02', NOW(), 2, NOW(), 'COMPLETED', 'CASH'),
                                                                                           ('O003', 'user06', NOW(), 3, NOW(), 'COMPLETED', 'CARD');

INSERT INTO order_menu (order_menu_id, o_id, menu_id, quantity, unit_price) VALUES
                                                                                ('OM01', 'O001', 'M001', 1, 4000), -- 신라면
                                                                                ('OM02', 'O001', 'M006', 1, 1500), -- 콜라
                                                                                ('OM03', 'O002', 'M003', 1, 6500), -- 참치마요
                                                                                ('OM04', 'O003', 'M005', 2, 2500); -- 아아 2잔

-- 현재 준비중인 주문 (주방 화면 테스트용)
INSERT INTO orders (o_id, m_id, o_time, seat_num, o_status, requestment) VALUES
    ('O004', 'user10', NOW(), 4, 'PREPARING', '단무지 많이 주세요');

INSERT INTO order_menu (order_menu_id, o_id, menu_id, quantity, unit_price) VALUES
    ('OM05', 'O004', 'M002', 1, 4500); -- 짜파게티