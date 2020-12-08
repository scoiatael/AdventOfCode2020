(ns part1
  (:require [instaparse.core :as insta]
            [clojure.string :as str]))


(def example "
nop +0
acc +1
jmp +4
acc +3
jmp -3
acc -99
acc +1
jmp -4
acc +6
")

(def line-parser
  (insta/parser
   "<line> = nop | acc | jmp
    nop = <'nop'> <whitespace> arg
    jmp = <'jmp'> <whitespace> arg
    acc = <'acc'> <whitespace> arg
    whitespace = #'\\s*'
    <arg> = #'[-+]\\s*\\d+' "))

(defn- parse-line [line]
  (if-let [parsed (insta/parses line-parser line)]
    parsed))

(defn- parse [text]
  (->> text
       str/split-lines
       (filter #(not (str/blank? %)))
       (map parse-line)
       (filter #(not (empty? %)))
       (mapcat first)
       ))

;; (parse example)

(defn- avoid-indirect-jmp [[idx [_jmp amount]]]
  [idx [:jmp (+ idx amount)]])

(defn- parse-arg [[idx [acc amount]]]
  [idx [acc (Integer/parseInt amount)]])

(defn- optimize [code]
  (->> code
       (map-indexed #(vector %1 %2))
       (map parse-arg)
       (map #(if (= :jmp (-> % last first)) (avoid-indirect-jmp %) %))))

(defn- run [code]
  (loop [idx 0
         acc 0
         visited #{}]
    (if (visited idx)
      acc
      (let [[instr arg] (code idx)]
        (case instr
          :nop (recur (inc idx) acc (conj visited idx))
          :acc (recur (inc idx) (+ acc arg) (conj visited idx))
          :jmp (recur arg acc (conj visited idx)))))))

(defn solve1 [text]
  (->> text
      parse
      optimize
      (reduce #(conj %1 %2) {})
      run))

;; (solve1 example)

;; (solve1 (slurp "day8/data/part1.txt"))

(defn- flow [[idx [instr arg]]]
  (case instr
    :nop [[(inc idx) (+ idx arg)] idx]
    :acc [[(inc idx)] idx]
    :jmp [[arg (inc idx)] idx]))

(defn- create-flows [code]
  (->> code
       (map flow)))

(defn- reverse-graph [[ids idx]]
  (if (= 1 (count ids))
    [[(first ids) [idx false]]]
    [[(first ids) [idx false]]
     [(last ids) [idx true]]]))

(defn- find-path [graph]
  (loop [tracks [{:at (dec (count graph)) :used-fork false :visited #{} :path []}]]
    (let [[cur & other-options] tracks
          {at :at used-fork :used-fork visited :visited path :path} cur
          reachable-from (graph at)
          valid-tracks (filter (fn [[idx by-fork]] (and (not (visited idx)) (or (not by-fork) (not used-fork)))) reachable-from)]
      (if (= at 0)
        (-> path rest (conj 0))                     ;; drop "after last" instruction
        (let [options (map (fn [[idx by-fork]] {:at idx :used-fork (if by-fork at used-fork) :visited (conj visited at) :path (conj path at)}) valid-tracks)]
          (recur (apply conj other-options options)))))))

(defn- exec-path [code path]
  (->> path
      (map #(nth code %))
      (map last)
      (filter #(= (first %) :acc))
      (map last)
      (reduce +)))

(defn solve2 [text]
  (let [code (-> text parse optimize)]
    (->> code
         create-flows
         (mapcat reverse-graph)
         (reduce (fn [m [from to]] (update m from conj to)) {})
         find-path
         (exec-path code)
         )))

;; (solve2 example)
;; {0 ([0 true]),
;;  7 ([6 false]),
;;  1 ([4 false] [0 false]),
;;  4 ([3 false]),
;;  6 ([5 false] [2 false]),
;;  3 ([2 true] [7 false]),
;;  2 ([1 false]),
;;  9 ([8 false]),
;;  5 ([4 true]),
;;  8 ([7 true])}

(solve2 (slurp "day8/data/part1.txt"))
