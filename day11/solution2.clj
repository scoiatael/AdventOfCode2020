(ns solution2
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

(defn is-seat? [field]
  (case field
    nil true
    \. false
    \L true
    \# true))

(defn add [{x :x y :y} [dx dy]]
  {:x (+ x dx) :y (+ y dy)})

(defn find-neighbour [board coords delta]
  (loop [c coords]
    (if (-> c board is-seat?)
      c
      (recur (add c delta)))))

(defn neighbours [board self]
  (->>
   (mapcat (fn [dy] (map (fn [dx] (find-neighbour board (add self [dx dy]) [dx dy])) (range -1 2))) (range -1 2))
   (filter #(not (= self %)))))

(defn progress [board [coords field]]
  [coords (case field
    \. field
    \# (if
           (<= 5 (->> coords (neighbours board) (map board) (filter occupied?) count))
           \L
           \#)
    \L (if 
           (== 0 (->> coords (neighbours board) (map board) (filter occupied?) count))
           \#
           \L))])


(defn board-str [board]
  (let [width (->> board (map #(-> % first :x))  (apply max))
        height (->> board (map #(-> % first :y))  (apply max))]
    (->> (range (inc height))
         (map  (fn [y] (->> (range (inc width)) (map (fn [x] (board {:x x :y y}))) (str/join ""))))
         (str/join "\n"))))

(defn next-board-cycle [board]
  (->> board (map #(progress board %)) (reduce conj {})))

(defn fix [f v]
  (reduce #(if (= %1 %2) (reduced %1) %2) (iterate f v)))

(def mostly-empty (parse "
.......#.
...#.....
.#.......
.........
..#L....#
....#....
.........
#........
...#.....
"))

(->> (slurp "day11/data/part1.txt")
     parse
     (fix next-board-cycle)
     vals
     (filter #(= \# %))
     count)
