(ns part1
  (:require [clojure.string :as str]
            [instaparse.core :as insta]))

(def example "
light red bags contain 1 bright white bag, 2 muted yellow bags.
dark orange bags contain 3 bright white bags, 4 muted yellow bags.
bright white bags contain 1 shiny gold bag.
muted yellow bags contain 2 shiny gold bags, 9 faded blue bags.
shiny gold bags contain 1 dark olive bag, 2 vibrant plum bags.
dark olive bags contain 3 faded blue bags, 4 dotted black bags.
vibrant plum bags contain 5 faded blue bags, 6 dotted black bags.
faded blue bags contain no other bags.
dotted black bags contain no other bags.
")

(def line-parser
  (insta/parser
   "line = adj-pair <whitespace> <'bags contain'> <whitespace> bag(<','> <whitespace> bag)*<'.'>
    adj-pair = adj <whitespace> adj
    whitespace = #'\\s*'
    <adj> = #'[a-zA-Z]+'
    <count> = #'\\d+'
    bag = count <whitespace> adj-pair <whitespace> <#'bags?'>"))


(defn- parse-line [line]
  (if-let [parsed (insta/parses line-parser line)]
    parsed))

(defn- parse [text]
  (->> text
       str/split-lines
       (filter #(not (str/blank? %)))
       (map parse-line)
       (filter #(not (empty? %)))))

;; (parse example)

(defn- reverse-graph [[[_line adjs-from & bags]]]
  (map (fn [[_bag _count adjs-to]] [adjs-to adjs-from]) bags))

(defn- reachable-from [start graph]
  (loop [visited #{}
         queue [start]]
    (if (empty? queue)
      visited
      (let [[cur & rest] queue
            neighbours (graph cur)]
        (recur (conj visited cur) (apply conj rest (filter #(not (visited %)) neighbours)))))))

;; (reachable-from 1 {1 [2] 2 [3] 3 [2]})

(defn solve1 [text]
  (->> text
       parse
       (mapcat reverse-graph)
       (reduce (fn [m [from to]] (update m from conj to)) {})
       (reachable-from [:adj-pair "shiny" "gold"])
       count
       dec))

;; (solve1 example)
;; (solve1 (slurp "day7/data/part1.txt"))

(defn- to-graph [[[_line adjs-from & bags]]]
  [adjs-from (map (fn [[_bag count adjs-to]] [adjs-to (Integer/parseInt count)]) bags)])

(defn- accum [start graph]
  (loop [leaves []
         queue [[start 1]]]
    (println leaves queue)
    (if (empty? queue)
      (->> leaves (reduce +))
      (let [[[cur acc] & rest] queue]
        (if-let [neighbours (graph cur)]
          (recur (conj leaves acc) (apply conj rest (map #(vector (first %) (* acc (last %))) neighbours)))
          (recur (conj leaves acc) rest))))))

;; (accum 1 {1 [[2 2]] 2 [[3 3]]})

(defn solve2 [text]
  (->> text
       parse
       (map to-graph)
       (reduce #(assoc %1 (first %2) (last %2)) {})
       (accum [:adj-pair "shiny" "gold"])
       dec))

(def example2 "
shiny gold bags contain 2 dark red bags.
dark red bags contain 2 dark orange bags.
dark orange bags contain 2 dark yellow bags.
dark yellow bags contain 2 dark green bags.
dark green bags contain 2 dark blue bags.
dark blue bags contain 2 dark violet bags.
dark violet bags contain no other bags.
")

;; (solve2 example)
;; (solve2 example2)

(solve2 (slurp "day7/data/part1.txt"))
