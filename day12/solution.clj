(ns solution
  (:require [instaparse.core :as insta]
            [clojure.string :as str]))


(def line-parser
  (insta/parser
   "<line> = direction amount
    <direction> = #'[A-Z]'
    <amount> = #'\\d+'
"))

(defn- parse-line [line]
  (if-let [parsed (insta/parses line-parser line)]
    parsed))

(defn parse
  [text]
  (->> text
       str/split-lines
       (filter #(not (str/blank? %)))
       (map parse-line)
       (map flatten)
       (map #(vector (first %) (-> % last Integer/parseInt)))))

(def example "
F10
N3
F7
R90
F11
")

(def example2 "
F10
R90
F10
L180
F10
F7
R270
F11
")

(defn add-bearing [[dir am] bearing]
  (let [op ({"L" - "R" +} dir)]
    (mod (op bearing am) 360)))

(defn move-bearing [bearing]
  (case bearing
     0  "N"
     90 "E"
     180 "S"
     270 "W"))

(defn reverse-move-bearing [bearing]
  (case bearing
     "N" 0
     "E" 90
     "S" 180
     "W" 270))

(assert (= "W" (move-bearing (add-bearing ["L" 180] (reverse-move-bearing "E")))))
(assert (= "N" (move-bearing (add-bearing ["L" 180] (reverse-move-bearing "S")))))
(assert (= "N" (move-bearing (add-bearing ["R" 180] (reverse-move-bearing "S")))))

(defn optimize [sequence]
  (loop [xs sequence
         moves []
         bearing 90]
    (if (empty? xs)
      (reverse moves)
      (let [[dir am :as x] (first xs)]
        (cond
          (#{"L" "R"} dir) (recur (rest xs) moves (add-bearing x bearing))
          (= "F" dir) (recur (rest xs) (cons [(move-bearing bearing) am] moves) bearing)
          :else (recur (rest xs) (cons x moves) bearing))))))

(defn execute [sequence]
  (loop [ms sequence
         x 0
         y 0]
    ;; (println (first ms) x y)
    (if (empty? ms)
      [x y]
      (let [[dir am] (first ms)]
        (cond
          (#{"N" "S"} dir) (recur (rest ms) x (({"N" + "S" -} dir) y am))
          (#{"E" "W"} dir) (recur (rest ms) (({"E" + "W" -} dir) x am) y))))))

;; (-> example parse optimize execute)
;; (-> "day12/data/part1.txt" slurp parse optimize execute)
;; (->> "day12/data/part1.txt" slurp parse optimize execute)

(defn rotate-waypoint [rotation [w_x w_y]]
  (case (add-bearing rotation 0)
    0 [w_x w_y]
    90 [w_y (- w_x)]
    180 [(- w_x) (- w_y)]
    270 [(- w_y) w_x]))

(defn move-to-waypoint [times [w_x w_y]]
  [(* times w_x) (* times w_y)])

(defn optimize2 [sequence]
  (loop [xs sequence
         moves []
         [w_x w_y :as waypoint] [10 1]]
    (if (empty? xs)
      (reverse moves)
      (let [[dir am :as x] (first xs)]
        (cond
          (#{"L" "R"} dir) (recur (rest xs) moves (rotate-waypoint x waypoint))
          (#{"N" "S"} dir) (recur (rest xs) moves [w_x (({"N" + "S" -} dir) w_y am)])
          (#{"E" "W"} dir) (recur (rest xs) moves [(({"E" + "W" -} dir) w_x am) w_y])
          :else (recur (rest xs) (cons (move-to-waypoint am waypoint) moves) waypoint))))))

(defn execute2 [sequence]
  (loop [ms sequence
         position [0 0]]
    ;; (println (first ms) x y)
    (if (empty? ms)
      position
      (recur (rest ms) (mapv + position (first ms))))))

;; (-> example parse optimize2 execute2)
(->> "day12/data/part1.txt" slurp parse optimize2 execute2 (reduce +))
