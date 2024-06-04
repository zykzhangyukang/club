local key = KEYS[1] -- 限流key
local limitCount = ARGV[1] -- 限流请求数
local limitTime = ARGV[2] -- 限流时间
local currentCount = redis.call('get', key) -- 当前请求数
-- 限流存在并且超过限流大小，则返回剩余可用请求数=0
if (currentCount and tonumber(currentCount) >= tonumber(limitCount)) then
    return { 0 , tonumber(currentCount) }
end
-- 请求数自增
currentCount = redis.call('incr', key)
-- 第一次请求，则设置过期时间
if (tonumber(currentCount) == 1) then
    redis.call('expire', key, limitTime)
end
-- 返回剩余可用请求数
return { tonumber(currentCount),  tonumber(limitCount) }
