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

  [(min..max), char.to_a.first, pass]
end

valid = File.read("#{__dir__}/data/part1.txt").lines.map(&method(:parse)).select do |range, char, pass| 
  range.include?(pass.to_a.select { |c| c == char }.count)
end

p valid.count
