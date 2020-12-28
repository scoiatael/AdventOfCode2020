(ns solution
  (:require [clojure.string :as str]))

(def ^:dynamic preamble-len 25)

(defn parse
  [text]
  (->> text
       str/split-lines
       (filter #(not (empty? %)))
       (map #(Long/parseLong %))))

(defn- has-sum? [target numbers]
  (let [complements (->> numbers (map-indexed #(vector (- target %2) %1)) (reduce conj {}))]
    (->> numbers
         (keep-indexed #(if-let [compl-index (complements %2)] (if (not (= compl-index %1)) compl-index)))
         count
         (< 0)
         )))

(defn solve1
  [input]
  (let [[preamble numbers] (->> input
                                (split-at preamble-len)
                                (map vec))]
    (loop [buffer preamble
           to-check (->> numbers (map-indexed #(vector %1 %2)))]
      (let [[[cur-idx cur-val] & next] to-check
            buf-rem (-> buffer rest vec)]
        (if (has-sum? cur-val buffer)
          (recur (conj buf-rem cur-val) next)
          cur-val)))))

;; (apply solve1 (with-bindings {#'preamble-len 5}
;;   (parse "
;; 35
;; 20
;; 15
;; 25
;; 47
;; 40
;; 62
;; 55
;; 65
;; 95
;; 102
;; 117
;; 150
;; 182
;; 127
;; 219
;; 299
;; 277
;; 309
;; 576
;; ")))

(def ^:dynamic target (solve1 (parse (slurp "day9/data/part1.txt"))))

(defn prefix-sums
  [numbers]
  (->> numbers
       count
       inc
       (range 0) 
       (map #(reduce + (long 0) (take % numbers)))
       (map-indexed #(vector %1 %2))
       (reduce conj {})))

(prefix-sums [1 2 3 4 5])

(defn smallest-and-largest
  [numbers]
  (let [sorted (sort numbers)]
    (vector (first sorted) (last sorted))))

(defn solve2 [numbers]
  (let [sums (prefix-sums numbers)]
    (loop [beg 0
           len 2]
      (if (< len 2)
        (recur beg (inc len))
        (let [cur-sum (- (sums (+ beg len)) (sums beg))]
          (cond
            (= cur-sum target) (->> beg (nthrest numbers)  (take len ) smallest-and-largest) 
            (< target cur-sum) (recur (inc beg) (dec len))
            :else (recur beg (inc len))))))))

;; (with-bindings {#'target 127} (solve2 
;;   (parse "
;; 35
;; 20
;; 15
;; 25
;; 47
;; 40
;; 62
;; 55
;; 65
;; 95
;; 102
;; 117
;; 150
;; 182
;; 127
;; 219
;; 299
;; 277
;; 309
;; 576
;; ")))

(solve2 (parse (slurp "day9/data/part1.txt")))
