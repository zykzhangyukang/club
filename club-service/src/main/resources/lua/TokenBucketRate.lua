local tokens_key = KEYS[1]                 -- 获取第一个键值，作为tokens_key
local timestamp_key = KEYS[2]              -- 获取第二个键值，作为timestamp_key
local rate = tonumber(ARGV[1])             -- 获取第一个参数，并将其转换为number类型，作为rate
local capacity = tonumber(ARGV[2])         -- 获取第二个参数，并将其转换为number类型，作为capacity
local now = tonumber(ARGV[3])              -- 获取第三个参数，并将其转换为number类型，作为当前时间戳
local requested = tonumber(ARGV[4])        -- 获取第四个参数，并将其转换为number类型，作为请求的token数

local fill_time = capacity / rate          -- 计算填满令牌桶所需的时间
local ttl = math.floor(fill_time * 2)      -- 计算令牌桶的生存时间（TTL），这里是填满时间的两倍

-- 获取当前tokens_key对应的值，即上一次剩余的令牌数
local last_tokens = tonumber(redis.call("get", tokens_key))
if last_tokens == nil then
    last_tokens = capacity                   -- 如果没有记录，默认令牌数为桶的容量
end

-- 获取当前timestamp_key对应的值，即上一次的刷新时间戳
local last_refreshed = tonumber(redis.call("get", timestamp_key))
if last_refreshed == nil then
    last_refreshed = 0                       -- 如果没有记录，默认刷新时间为0
end

local delta = math.max(0, now - last_refreshed)  -- 计算当前时间与上一次刷新时间的差值
local filled_tokens = math.min(capacity, last_tokens + (delta * rate))  -- 计算当前令牌数
local allowed = filled_tokens >= requested     -- 判断当前令牌数是否满足请求

local new_tokens = filled_tokens
local allowed_num = 0                         -- 初始化返回值，表示请求是否被允许
if allowed then
    new_tokens = filled_tokens - requested      -- 如果允许请求，计算新的令牌数
    allowed_num = 1                             -- 标记请求被允许
end

if ttl > 0 then
    redis.call("setex", tokens_key, ttl, new_tokens)  -- 更新tokens_key对应的值和TTL
    redis.call("setex", timestamp_key, ttl, now)     -- 更新timestamp_key对应的值和TTL
end

return { allowed_num, new_tokens }            -- 返回请求是否被允许及新的令牌数
