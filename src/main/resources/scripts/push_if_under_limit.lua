-- scripts/push_if_under_limit.lua
local currentLength = redis.call('LLEN', KEYS[1])
if tonumber(currentLength) < tonumber(ARGV[1]) then
    redis.call('RPUSH', KEYS[1], ARGV[2])
    return 1
else
    return 0
end