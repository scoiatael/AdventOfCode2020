(ns solution
  (:require [clojure.string :as str]))

(defn parse
  [text]
  (->> text
       str/split-lines
       (filter #(not (empty? %)))
       (map #(Long/parseLong %))))

(defn solve1
  [numbers]
  (let [by-joltage (-> numbers sort vec)
        most-jolts (last by-joltage)
        diffs (mapv - (conj by-joltage (+ 3 most-jolts)) (cons 0 by-joltage))]
    (frequencies diffs)))

(def example1 "
16
10
15
5
1
11
7
19
6
12
4
")

(def example2 "
28
33
18
42
31
14
46
20
48
47
24
23
49
45
19
38
39
11
1
32
25
35
8
17
7
9
4
2
34
10
3
")

(def input (slurp "day10/data/part1.txt"))

(defn solve2
  [numbers]
  (let [most-jolts (apply max numbers)
        adapter (+ 3 most-jolts)
        by-joltage (-> numbers (conj adapter) sort vec)
        by-index (->> by-joltage (map-indexed #(vector %1 %2)) (reduce conj {}))]
    (loop [idx 0
           options {0 1}]
      (let [cur-val (by-index idx)]
        (if (= adapter cur-val)
          (options most-jolts)
          (let [options-to-current-val (->> [1 2 3] (map #(- cur-val %)) (map options) (filter #(not (nil? %))) (reduce +))]
            (recur (inc idx) (conj options [cur-val options-to-current-val]))))))))

(-> input parse solve2)
