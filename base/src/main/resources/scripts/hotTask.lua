-- KEYS[1]: 计数器 Key (hot:play:mv:1)
-- KEYS[2]: 活跃 Set Key (active:songs)
-- ARGV[1]: ID

-- 1. 原子获取并重置计数器
local res = redis.call('getset', KEYS[1], '0')
redis.call('srem', KEYS[2], ARGV[1])
return tonumber(res)