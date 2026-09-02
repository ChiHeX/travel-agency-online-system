USE travel_agency;

-- The following rows are test/demo data only, not real travel agency operating data.
INSERT INTO sys_role (code, name, description) VALUES
    ('USER', '注册用户', '浏览、报名、订单与评价'),
    ('STAFF', '旅行社工作人员', '线路、团期、订单与退款运营'),
    ('GUIDE', '导游', '仅查看和执行本人负责的团期'),
    ('ADMIN', '系统管理员', '系统级用户与权限管理')
ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);

-- password is the BCrypt hash for the demo password "password".
INSERT INTO sys_user (username, password_hash, nickname, real_name, status, deleted)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '系统管理员', '演示管理员', 1, 0)
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname), status = 1, deleted = 0;

INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id FROM sys_user u JOIN sys_role r ON r.code = 'ADMIN'
WHERE u.username = 'admin'
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

INSERT INTO attraction (name, city, address, longitude, latitude, intro, data_source, status)
VALUES
    ('大理古城', '大理', '云南省大理市大理古城', 100.1650000, 25.6940000, '示例景点资料，用于地图与行程演示。', '团队整理的测试数据；坐标仅用于软件演示', 1),
    ('丽江古城', '丽江', '云南省丽江市古城区', 100.2330000, 26.8720000, '示例景点资料，用于地图与行程演示。', '团队整理的测试数据；坐标仅用于软件演示', 1)
ON DUPLICATE KEY UPDATE intro = VALUES(intro);

INSERT INTO travel_route (name, departure_city, destination, duration_days, description, included, excluded, booking_notice, status)
VALUES ('彩云之南经典 6 日跟团游', '上海', '昆明·大理·丽江', 6,
        '示例线路，用于演示线路、行程、团期和订单业务闭环。',
        '交通、住宿、行程内景点首道门票', '个人消费、行程外活动、单房差', '请在出发前确认有效证件与紧急联系人信息。', 'PUBLISHED')
ON DUPLICATE KEY UPDATE description = VALUES(description), status = VALUES(status);

SET @route_id = (SELECT id FROM travel_route WHERE name = '彩云之南经典 6 日跟团游' ORDER BY id LIMIT 1);
SET @dali_id = (SELECT id FROM attraction WHERE name = '大理古城' ORDER BY id LIMIT 1);
SET @lijiang_id = (SELECT id FROM attraction WHERE name = '丽江古城' ORDER BY id LIMIT 1);

INSERT INTO hotel (name, address, contact_phone, intro, data_source, status)
SELECT '彩云之南演示酒店', '云南省大理市古城区', '000-00000000', '课程演示用酒店资料，不提供独立预订。', '团队原创测试资料', 1
WHERE NOT EXISTS (SELECT 1 FROM hotel WHERE name = '彩云之南演示酒店');
SET @hotel_id = (SELECT id FROM hotel WHERE name = '彩云之南演示酒店' ORDER BY id LIMIT 1);

INSERT INTO route_itinerary_day (route_id, day_number, title, description, transportation, meals, hotel_id)
VALUES
    (@route_id, 1, '上海 · 昆明', '抵达昆明，完成集合与入住。', '飞机 / 大巴', '晚餐自理', @hotel_id),
    (@route_id, 2, '昆明 · 大理', '前往大理古城，感受苍山洱海风光。', '旅游大巴', '早、午餐', @hotel_id),
    (@route_id, 3, '大理 · 丽江', '游览古城，前往丽江。', '旅游大巴', '早、午餐', @hotel_id)
ON DUPLICATE KEY UPDATE title = VALUES(title), description = VALUES(description), hotel_id = VALUES(hotel_id);

SET @day2_id = (SELECT id FROM route_itinerary_day WHERE route_id = @route_id AND day_number = 2 LIMIT 1);
SET @day3_id = (SELECT id FROM route_itinerary_day WHERE route_id = @route_id AND day_number = 3 LIMIT 1);
INSERT INTO route_itinerary_item (day_id, sort_no, item_type, name, description, attraction_id, longitude, latitude)
VALUES
    (@day2_id, 1, 'ATTRACTION', '大理古城', '古城步行游览。', @dali_id, 100.1650000, 25.6940000),
    (@day3_id, 1, 'ATTRACTION', '丽江古城', '古城步行游览。', @lijiang_id, 100.2330000, 26.8720000);

INSERT INTO departure (route_id, start_date, end_date, adult_price, child_price, max_people, status)
SELECT @route_id, DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY), DATE_ADD(CURRENT_DATE, INTERVAL 35 DAY), 3980.00, 3280.00, 30, 'OPEN'
WHERE @route_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM departure WHERE route_id = @route_id AND start_date = DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY));

INSERT INTO data_source (data_name, source, source_type, used_date, license, remark)
VALUES ('演示景点与线路基础资料', '团队原创整理的课程测试数据', 'TEAM_TEST_DATA', CURRENT_DATE, '仅限课程项目开发、测试与答辩演示', '不代表真实旅行社经营数据');
