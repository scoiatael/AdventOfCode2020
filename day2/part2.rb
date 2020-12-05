#!/usr/bin/env ruby

class String
  include Enumerable

  def each(&block)
    self.split("").each(&block)
  end
end

def parse(line)
  range, char, pass = line.split
  min, max = range.split('-').map(&:to_i)

  [[min, max], char.to_a.first, pass.to_a]
end

valid = File.read("#{__dir__}/data/part1.txt").lines.map(&method(:parse)).select do |(min, max), char, pass| 
  (pass[min-1] ==  char) ^ (pass[max-1] == char)
end

p valid.count

