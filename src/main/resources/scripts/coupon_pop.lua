-- scripts/coupon_pop.lua

local result = {}
for i = 1, tonumber(ARGV[1]) do
  local val = redis.call('RPOP', KEYS[1])
  if not val then break end
  table.insert(result, val)
end
return result