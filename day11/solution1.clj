(ns solution1
  (:require [clojure.string :as str]))

(def example "
L.LL.LL.LL
LLLLLLL.LL
L.L.L..L..
LLLL.LL.LL
L.LL.LL.LL
L.LLLLL.LL
..L.L.....
LLLLLLLLLL
L.LLLLLL.L
L.LLLLL.LL
")

(defn parse
  [text]
  (->> text
       str/split-lines
       (filter #(not (str/blank? %)))
       (map-indexed #(map-indexed (fn [idx item] [{:x idx :y %1} item]) %2))
       (mapcat identity)
       (reduce conj {})))

(defn occupied? [field]
  (case field
    nil false
    \. false
    \L false
    \# true))

(defn neighbours [{x :x y :y :as self}]
  (->>
   (mapcat (fn [dy] (map (fn [dx] {:x (+ x dx) :y (+ y dy)}) (range -1 2))) (range -1 2))
   (filter #(not (= self %)))))

(defn progress [board [coords field]]
  [coords (case field
    \. field
    \# (if
           (<= 4 (->> coords neighbours (map board) (filter occupied?) count))
           \L
           \#)
    \L (if 
           (== 0 (->> coords neighbours (map board) (filter occupied?) count))
           \#
           \L))])


(defn next-board-cycle [board]
  (->> board (map #(progress board %)) (reduce conj {})))

(defn board-str [board]
  (let [width (->> board (map #(-> % first :x))  (apply max))
        height (->> board (map #(-> % first :y))  (apply max))]
    (->> (range (inc height))
         (map  (fn [y] (->> (range (inc width)) (map (fn [x] (board {:x x :y y}))) (str/join ""))))
         (str/join "\n"))))

(defn fix [f v]
  (reduce #(if (= %1 %2) (reduced %1) %2) (iterate f v)))

(->>
 (slurp "day11/data/part1.txt")
 parse
 (fix  next-board-cycle)
 ;; board-str
 ;; println
 vals
 (filter #(= \# %))
 count
 )
