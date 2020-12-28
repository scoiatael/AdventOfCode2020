#!/usr/bin/env ruby

def decode(str)
  Integer(str.gsub(/[FL]/, "0").gsub(/[BR]/, "1"), 2)
end

{
    "BFFFBBFRRR" =>  567,
    "FFFBBBFRRR" =>  119,
    "BBFFBBFRLL" =>  820,
}.each { |str, sid| fail "#{str} != #{sid} (got #{decode(str)})" unless sid == decode(str) }

sids = File.read("#{__dir__}/data/part1.txt").lines.map(&method(:decode))
p sids.max

sorted = sids.sort
sorted.zip(sorted[1..-1]).each do |sid, next_sid| 
  if sid + 1 != next_sid
    p sid+1 
    break
  end
end
