#!/usr/bin/env ruby

require 'set'

FIELDS = %W[
    byr
    iyr
    eyr
    hgt
    hcl
    ecl
    pid
    cid
]

REQUIRED = Set.new(FIELDS[0...-1])

RULES = []
# byr (Birth Year) - four digits; at least 1920 and at most 2002.
RULES << def byr_check(passport)
  (1920..2002).include?(passport.byr.to_i)
end
# iyr (Issue Year) - four digits; at least 2010 and at most 2020.
RULES << def iyr_check(passport)
  (2010..2020).include?(passport.iyr.to_i)
end
# eyr (Expiration Year) - four digits; at least 2020 and at most 2030.
RULES << def eyr_check(passport)
  (2020..2030).include?(passport.eyr.to_i)
end
# hgt (Height) - a number followed by either cm or in:
#   If cm, the number must be at least 150 and at most 193.
#   If in, the number must be at least 59 and at most 76.
RULES << def hgt_check(passport)
  range = passport.hgt.end_with?("cm") ? (150..193) : (59..76)
  range.include?(passport.hgt[0...-2].to_i)
end
# hcl (Hair Color) - a # followed by exactly six characters 0-9 or a-f.
RULES << def hcl_check(passport)
  passport.hcl.match?(/^#[a-f0-9]{6}$/)
end
# ecl (Eye Color) - exactly one of: amb blu brn gry grn hzl oth.
RULES << def ecl_check(passport)
  %w[amb blu brn gry grn hzl oth].include?(passport.ecl)
end
# pid (Passport ID) - a nine-digit number, including leading zeroes.
RULES << def pid_check(passport)
  passport.pid.match?(/^\d{9}$/)
end
# cid (Country ID) - ignored, missing or not.

def check_rules(passport)
    RULES.map { |r| [r, method(r).call(passport)] } .to_h
end

class Passport
    # --- Example passport
    # ecl:gry pid:860033327 eyr:2020 hcl:#fffffd
    # byr:1937 iyr:2017 cid:147 hgt:183cm
    # --- parsed
    # {"ecl"=>"gry", "pid"=>"860033327", "eyr"=>"2020", "hcl"=>"#fffffd", "byr"=>"1937", "iyr"=>"2017", "cid"=>"147", "hgt"=>"183cm"}
    def initialize(str)
        @fields = str.split.map { |f| f.split(":") } .to_h
    end

    def valid?
      Set.new(@fields.keys).superset?(REQUIRED) and check_rules(self).values.all?
    end

    REQUIRED.each do |field| 
      define_method(field) do
        @fields[field]
      end
    end
end

passports = File.read("#{__dir__}/data/part1.txt").split(/^$/).map(&Passport.method(:new))
p passports.select(&:valid?).count

