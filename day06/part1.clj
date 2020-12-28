(ns part1
  (:require [clojure.set :as st]
            [clojure.string :as str]))

(defn- to-groups [file] (str/split file #"\n\n"))
(defn- to-person-answers [group] (str/split group #"\n"))
(defn- to-single-answers [person-answers] (->> (str/split person-answers #"") (filter (fn [ans] (= 1 (count ans))))))
(defn- all-answers-in-group [group-answers] (->> group-answers (map to-single-answers) flatten set))

(def example-input "abc

a
b
c

ab
ac

a
a
a
a

b
")

(defn solve1 [input]
  (->> input
       to-groups
       (map to-person-answers)
       (map all-answers-in-group)
       (map count)
       (reduce +)))

(solve1 example-input)
(solve1 (slurp "day6/data/part1.txt"))


(defn- common-answers-in-group [group-answers]
  (->> group-answers
       (map to-single-answers)
       (map set)
       (reduce st/intersection)
       ))

(defn solve2 [input]
  (->> input
       to-groups
       (map to-person-answers)
       (map common-answers-in-group)
       (map count)
       (reduce +)
       ))

(solve2 example-input)
(solve2 (slurp "day6/data/part1.txt"))
