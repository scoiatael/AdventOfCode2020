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
      Set.new(@fields.keys).superset?(REQUIRED)
    end
end

passports = File.read("#{__dir__}/data/part1.txt").split(/^$/).map(&Passport.method(:new))
p passports.select(&:valid?).count
