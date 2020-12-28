#!/usr/bin/env ruby

SLOPES = [
  [3, 1],
  [1, 1],
  [5, 1],
  [7, 1],
  [1, 2]
]

class Row
  def initialize(line)
    @line = line.rstrip.split("")
    @len = @line.length
    @tree_idxs = @line.map.with_index.select { |char, idx| char == '#' } .map(&:last)
  end

  def tree_at?(idx)
    @tree_idxs.include?(idx % @len)
  end

  def check(at)
    line_with_marker = @line.dup
    if tree_at?(at)
      line_with_marker[at % @len] = 'X'
    else
      line_with_marker[at % @len] = 'O'
    end
    line_with_marker.join("")
  end
end

rows = File.read("#{__dir__}/data/part1.txt").lines.map(&Row.method(:new))

tree_counts = SLOPES.map do |(by_x, by_y)| 
    trees = (0...rows.length).step(by_y).select do |row_idx| 
    rows[row_idx].tree_at?(row_idx * by_x / by_y)
    end

    trees.count
end

p tree_counts.reduce(:*)
