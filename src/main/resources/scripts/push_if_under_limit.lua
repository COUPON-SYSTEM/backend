-- KEYS[1] = queueKey (쿠폰 요청 큐)
-- KEYS[2] = totalCountKey (누적 발급 카운터)
-- KEYS[3] = userKeyPrefix (유저 중복 방지 키 prefix)
-- ARGV[1] = maxTotalCount (예: 100)
-- ARGV[2] = userId
-- ARGV[3] = userGuardTtlSeconds (0이면 TTL 없음)

-- 유저 중복 키 생성
local userKey = KEYS[3] .. ARGV[2]

-- 1. 이미 참여한 유저인지 확인 (SETNX: 존재하면 0)
local created = redis.call('SETNX', userKey, 1)
if created == 0 then
    return -1 -- 이미 참여한 유저
end

-- 2. 누적 발급 개수 확인
local currentTotal = tonumber(redis.call('GET', KEYS[2]) or "0")
if currentTotal < tonumber(ARGV[1]) then
    -- 누적 한도 미만일 때 push & count 증가
    redis.call('RPUSH', KEYS[1], ARGV[2])
    redis.call('INCR', KEYS[2])
    return 1 -- 성공
else
    -- 누적 한도 초과 시, 유저 키 롤백
    redis.call('DEL', userKey)
    return 0 -- 발급 한도 초과
end