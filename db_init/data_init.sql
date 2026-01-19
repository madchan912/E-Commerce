-- 1. 공연 데이터 (아이유 콘서트)
INSERT INTO performance (name, date, location, ticket_opening_time)
VALUES ('아이유 콘서트', '2026-05-01 19:00:00', '잠실 주경기장', '2026-04-20 10:00:00');

-- 2. 좌석 데이터 100개 (A1 ~ A100)
INSERT INTO performance_seat (seat_code, status, performance_id)
SELECT 'A' || i, 'AVAILABLE', 1
FROM generate_series(1, 100) AS i;

-- 3. (옵션) 테스트용 사용자 1명 생성
-- 비밀번호는 Bcrypt로 암호화되지 않아서 실제 로그인은 불가능하지만, 데이터 확인용입니다.
INSERT INTO app_user (email, name, password, is_verified, phone_number, address)
VALUES ('test@test.com', '테스터', '1234', true, '010-1234-5678', '서울시 강남구');