-- 将所有现有的"在售"商品状态改为"待审核"
-- 这样管理员可以重新审核这些商品
UPDATE product 
SET status = 0 
WHERE status = 1;

-- 查看更新后的商品状态
SELECT id, title, status, create_time 
FROM product 
ORDER BY create_time DESC;
