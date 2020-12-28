# -*- mode: raku -*-
use v6;

my $file  = open 'data/part1.txt';

constant $target = 2020;
my %set;

say "starting...";
for $file.lines -> $line {
    my ($int) = $line.words;

    my $other = $target - $int;
    %set{$target - $int} = $int;
    if %set{$int} {
        my $other = %set{$int};
        my $mult = $other * $int;
        say "Found $other + $int = 2020; $mult"
    }
}
say "done.\n";
