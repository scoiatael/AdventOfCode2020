# -*- mode: raku -*-
use v6;

my $file  = open 'data/part1.txt';

constant $target = 2020;
my @numbers;
my %pairs;

say "starting...";
for $file.lines -> $line {
    my ($int) = $line.words;

    for @numbers -> $number {
        %pairs{$target - $int - $number} = [$int, $number];
    }
    push @numbers, $int;
    if %pairs{$int} {
        my ($other1, $other2) = %pairs{$int};
        my $mult = $other1 * $other2 * $int;
        say "Found $other1 + $other2 + $int = 2020; $mult"
    }
}
say "done.\n";
