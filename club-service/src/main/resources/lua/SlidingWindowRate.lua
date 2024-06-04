local key = KEYS[1]
local limitCount = tonumber(ARGV[1]) -- 限流请求数
local windowInMillis = tonumber(ARGV[2]) -- 限流窗口大小，毫秒
local now = tonumber(ARGV[3]) -- 当前时间戳，毫秒

-- 移除窗口外的旧请求
redis.call('zremrangebyscore', key, 0, now - windowInMillis)

-- 统计当前窗口内的请求数
local currentCount = redis.call('zcard', key)

-- 如果请求数超过限流大小，则返回 0 和当前请求数
if currentCount >= limitCount then
    return { 0, currentCount }
end

-- 记录本次请求
redis.call('zadd', key, now, now)

-- 设置键的过期时间
redis.call('pexpire', key, windowInMillis)

-- 返回当前请求数和剩余可用请求数
return { currentCount + 1, limitCount - currentCount - 1 }
