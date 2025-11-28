-- DB 사용
use POS_PLACE;

---
-- 1. category 테이블 더미 데이터 (5개)
---
insert into category (c_id, c_name) values
                                        ('C001', '음료'),
                                        ('C002', '라면'),
                                        ('C003', '스낵'),
                                        ('C004', '식사'),
                                        ('C005', '주류');

---
-- 2. member 테이블 더미 데이터 (5개)
---
insert into member (m_id, passwd, name, birth, sex, remain_time, phone, join_date) values
                                                                                       ('user001', 'pass1234', '김철수', '1995-05-15', 'M', 120, '010-1111-2222', '2024-01-01 10:00:00'),
                                                                                       ('user002', 'pass5678', '이영희', '2000-11-20', 'F', 60, '010-3333-4444', '2024-01-15 15:30:00'),
                                                                                       ('user003', 'pass9012', '박민규', '1998-03-10', 'M', 300, '010-5555-6666', '2024-02-01 08:00:00'),
                                                                                       ('user004', 'pass3456', '최지수', '2003-08-25', 'F', 0, '010-7777-8888', '2024-02-20 20:10:00'),
                                                                                       ('user005', 'password', '정우성', '1985-01-01', 'M', 450, '010-9999-0000', '2024-03-05 12:45:00');

---
-- 3. menu 테이블 더미 데이터 (10개)
---
insert into menu (menu_id, m_name, m_price, m_description, c_id) values
                                                                     ('M001', '콜라', 1500, '시원한 탄산음료', 'C001'),
                                                                     ('M002', '아이스 아메리카노', 2500, '시원한 아이스커피', 'C001'),
                                                                     ('M003', '신라면', 3500, '국민 라면', 'C002'),
                                                                     ('M004', '짜파게티', 4000, '짜장 라면', 'C002'),
                                                                     ('M005', '포카칩', 1800, '오리지널 감자칩', 'C003'),
                                                                     ('M006', '새우깡', 1500, '짭짤한 스낵', 'C003'),
                                                                     ('M007', '제육덮밥', 7000, '매콤한 제육볶음과 밥', 'C004'),
                                                                     ('M008', '김치볶음밥', 6500, '매콤달콤한 김치볶음밥', 'C004'),
                                                                     ('M009', '사이다', 1500, '청량한 맛', 'C001'),
                                                                     ('M010', '떡볶이', 5000, '매콤달콤한 쌀 떡볶이', 'C004');

---
-- 4. orders 테이블 더미 데이터 (7개)
---
insert into orders (o_id, m_id, o_time, seat_num, o_status, requestment, pay_method) values
                                                                                         ('O0001', 'user001', '2025-11-28 10:30:00', 5, 'COMPLETED', '맵게 해주세요', 'CARD'),
                                                                                         ('O0002', 'user002', '2025-11-28 11:00:00', 12, 'COMPLETED', null, 'CASH'),
                                                                                         ('O0003', 'user003', '2025-11-28 12:00:00', 3, 'PREPARING', '음료는 얼음 많이', 'CARD'),
                                                                                         ('O0004', 'user004', '2025-11-28 13:15:00', 8, 'COMPLETED', null, 'CARD'),
                                                                                         ('O0005', 'user001', '2025-11-28 14:00:00', 5, 'CANCELED', '잘못 주문함', 'CARD'),
                                                                                         ('O0006', null, '2025-11-28 15:00:00', 10, 'COMPLETED', '일회용품 제외', 'CASH'),
                                                                                         ('O0007', 'user005', '2025-11-28 16:30:00', 1, 'REFUNDED', '메뉴 변경', 'CARD');

-- COMPLETED 주문에 대해 complete_time 설정
update orders set complete_time = '2025-11-28 10:35:00' where o_id = 'O0001';
update orders set complete_time = '2025-11-28 11:05:00' where o_id = 'O0002';
update orders set complete_time = '2025-11-28 13:25:00' where o_id = 'O0004';
update orders set complete_time = '2025-11-28 15:10:00' where o_id = 'O0006';

---
-- 5. order_menu 테이블 더미 데이터 (10개)
-- unit_price는 menu.m_price 값을 임의로 사용
---
insert into order_menu (order_menu_id, o_id, menu_id, quantity, unit_price) values
                                                                                ('OM001', 'O0001', 'M007', 1, 7000), -- 제육덮밥
                                                                                ('OM002', 'O0001', 'M001', 2, 1500), -- 콜라
                                                                                ('OM003', 'O0002', 'M003', 1, 3500), -- 신라면
                                                                                ('OM004', 'O0002', 'M009', 1, 1500), -- 사이다
                                                                                ('OM005', 'O0003', 'M002', 3, 2500), -- 아아
                                                                                ('OM006', 'O0004', 'M008', 1, 6500), -- 김볶밥
                                                                                ('OM007', 'O0004', 'M006', 1, 1500), -- 새우깡
                                                                                ('OM008', 'O0005', 'M004', 1, 4000), -- 짜파게티 (CANCELED)
                                                                                ('OM009', 'O0006', 'M010', 2, 5000), -- 떡볶이
                                                                                ('OM010', 'O0007', 'M005', 1, 1800); -- 포카칩 (REFUNDED)

---
-- 6. refund 테이블 더미 데이터 (1개)
---
insert into refund (r_id, o_id, r_time, r_amount) values
    ('R0001', 'O0007', '2025-11-28 16:40:00', 1800); -- O0007 포카칩 환불

---
-- 7. ingredient 테이블 더미 데이터 (5개)
---
insert into ingredient (i_id, i_name, total_quantity, is_out) values
                                                                  ('I0001', '콜라 원액', 50, false),
                                                                  ('I0002', '라면 사리', 20, false),
                                                                  ('I0003', '쌀', 1000, false),
                                                                  ('I0004', '돼지고기', 50, false),
                                                                  ('I0005', '커피 원두', 80, false);

---
-- 8. stock_info 테이블 더미 데이터 (5개)
---
insert into stock_info (stock_info_id, i_id, unit_name, unit_quantity) values
                                                                           ('SI001', 'I0001', '박스(10L)', 50),     -- 콜라 1박스는 50단위 (잔)
                                                                           ('SI002', 'I0002', '봉지(5개입)', 5),     -- 라면 1봉지 5개입
                                                                           ('SI003', 'I0003', '포대(20kg)', 100),   -- 쌀 1포대는 1000단위 (10g)
                                                                           ('SI004', 'I0004', '팩(1kg)', 10),      -- 돼지고기 1팩 10단위 (100g)
                                                                           ('SI005', 'I0005', '봉투(500g)', 10);    -- 원두 1봉투 10단위 (1잔 분량)

---
-- 9. stock_in 테이블 더미 데이터 (6개)
-- total_added는 trigger 없이 계산하여 넣음
---
insert into stock_in (stock_in_id, i_id, stock_info_id, in_quantity, unit_quantity, in_time) values
                                                                                                 ('IN001', 'I0001', 'SI001', 1, 50, '2025-10-01 09:00:00'), -- 콜라 1박스 (50개)
                                                                                                 ('IN002', 'I0002', 'SI002', 4, 5, '2025-10-15 10:30:00'), -- 라면 4봉지 (20개)
                                                                                                 ('IN003', 'I0003', 'SI003', 1, 1000, '2025-11-01 11:00:00'), -- 쌀 1포대 (1000개)
                                                                                                 ('IN004', 'I0004', 'SI004', 5, 10, '2025-11-10 12:00:00'), -- 돼지고기 5팩 (50개)
                                                                                                 ('IN005', 'I0005', 'SI005', 8, 10, '2025-11-20 13:00:00'), -- 원두 8봉투 (80개)
                                                                                                 ('IN006', 'I0001', 'SI001', 2, 50, '2025-11-25 15:00:00'); -- 콜라 2박스 (100개)

-- ingredient.total_quantity 업데이트 (실제 DB에서는 트리거/프로시저로 관리)
update ingredient set total_quantity = 50 + 100 where i_id = 'I0001'; -- 150
update ingredient set total_quantity = 20 where i_id = 'I0002'; -- 20
update ingredient set total_quantity = 1000 where i_id = 'I0003'; -- 1000
update ingredient set total_quantity = 50 where i_id = 'I0004'; -- 50
update ingredient set total_quantity = 80 where i_id = 'I0005'; -- 80

---
-- 10. menu_ingredient 테이블 더미 데이터 (5개)
---
insert into menu_ingredient (menu_ingredient_id, m_id, i_id, required_quantity) values
                                                                                    ('MI001', 'M001', 'I0001', 1), -- 콜라 1잔에 콜라 원액 1단위
                                                                                    ('MI002', 'M002', 'I0005', 1), -- 아아 1잔에 원두 1단위
                                                                                    ('MI003', 'M003', 'I0002', 1), -- 신라면 1개에 라면 사리 1단위
                                                                                    ('MI004', 'M007', 'I0004', 5), -- 제육덮밥 1개에 돼지고기 5단위 (500g 가정)
                                                                                    ('MI005', 'M007', 'I0003', 10); -- 제육덮밥 1개에 쌀 10단위 (100g 가정)

---
-- 11. game 테이블 더미 데이터 (3개)
---
insert into game (g_id, title, publisher) values
                                              ('G0001', '리그 오브 레전드', '라이엇 게임즈'),
                                              ('G0002', '배틀그라운드', '크래프톤'),
                                              ('G0003', '피파 온라인 4', '넥슨');

---
-- 12. play_log 테이블 더미 데이터 (5개)
-- m_id는 member 테이블의 m_id와 일치해야 하지만, 테이블 스키마에 m_id는 varchar(30)이지만, play_log의 m_id는 varchar(5)로 되어 있어 데이터 유효성을 위해 m_id가 짧은 member를 추가하겠습니다.
---
-- 새로운 임시 멤버 추가 (m_id 길이를 맞추기 위해)
insert into member (m_id, passwd, name, birth, sex, remain_time, phone, join_date) values
                                                                                       ('test1', '1234', '임시1', '2000-01-01', 'M', 10, '010-1234-0001', '2024-11-28 09:00:00'),
                                                                                       ('test2', '1234', '임시2', '2000-01-02', 'F', 20, '010-1234-0002', '2024-11-28 09:00:00');

insert into play_log (log_id, m_id, g_id, seat_no, start_time, end_time) values
                                                                             ('L0001', 'test1', 'G0001', 5, '2025-11-28 10:00:00', '2025-11-28 11:30:00'),
                                                                             ('L0002', 'test2', 'G0002', 12, '2025-11-28 10:30:00', '2025-11-28 12:00:00'),
                                                                             ('L0003', 'test1', 'G0003', 5, '2025-11-28 14:00:00', '2025-11-28 15:00:00'),
                                                                             ('L0004', 'test2', 'G0001', 12, '2025-11-28 15:30:00', null), -- 현재 플레이 중
                                                                             ('L0005', 'test1', 'G0002', 5, '2025-11-28 18:00:00', '2025-11-28 18:45:00');