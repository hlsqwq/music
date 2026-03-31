-- KEYS[1]: userLikeBitmap (位图)
-- KEYS[2]: likeCount (计数器)
-- KEYS[3]: likeSet (热点ID集合)
-- ARGV[1]: userId (偏移量)
-- ARGV[2]: mvId (业务ID)

local isLiked = redis.call('getbit', KEYS[1], ARGV[1])

if isLiked == 1 then
    -- 1. 如果已点赞 -> 取消点赞
    redis.call('setbit', KEYS[1], ARGV[1], 0)
    -- 2. 计数器减 1
    local count = redis.call('decr', KEYS[2])
    -- 3. 将 ID 放入变动集合 (同步 MySQL 用)
    redis.call('sadd', KEYS[3], ARGV[2])
    return count
else
    -- 1. 如果未点赞 -> 执行点赞
    redis.call('setbit', KEYS[1], ARGV[1], 1)
    -- 2. 计数器加 1
    local count = redis.call('incr', KEYS[2])
    -- 3. 将 ID 放入变动集合
    redis.call('sadd', KEYS[3], ARGV[2])
    return count
end