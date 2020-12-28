(ns solution
  (:require [instaparse.core :as insta]))

(def example "
939
7,13,x,x,59,x,31,19
")

(def parser
  (insta/parser
   "<schedule> = <whitespace> departure <hard_whitespace> bus_ids <whitespace>
    departure = #'\\d+'
    <bus_ids> = bus*{<','> bus}
    <bus> = bus_id | not_a_bus
    bus_id = #'\\d+'
    not_a_bus = <'x'>
    hard_whitespace = #'\\s+'
    whitespace = #'\\s*'
 "))

(defn parse [text]
  (if-let [parsed (insta/parses parser text)]
    (let [[departure & buses] (first parsed)]
      {:departure (-> departure last Integer/parseInt)
       :buses buses
       :bus_ids (->> buses
                     (filter #(= :bus_id (first %)))
                     (map last)
                     (map #(Integer/parseInt %)))})))

(defn solve [{departure :departure bus_ids :bus_ids}]
  (let [options (->> bus_ids (map (fn [id] [id (- id (mod departure id))])) (apply conj {}))
        best-fit (apply min-key options (keys options))]
    [best-fit (options best-fit)]))

;; (->> example parse solve (reduce *))
;; (->> "day13/data/part1.txt" slurp parse solve (reduce *))

(defn into-eq [{buses :buses}]
  (->> buses
       (map-indexed (fn [idx itm] (if (= :bus_id (first itm)) {:n (-> itm last Integer/parseInt) :y idx})))
       (filter #(not (nil? %)))))

(defn gen_euc_pr
  "
  return a, b such that 1 = a*p + b*q
  https://pl.wikipedia.org/wiki/Algorytm_Euklidesa#Rozszerzony_algorytm_Euklidesa
  "
  [p q]
  (loop [a p
         b q
         x 1
         y 0
         r 0
         s 1]
    (if (>= 0 b) [x y]
        (let [q (quot a b)]
          (recur
           b                            ; =: a
           (mod a b)                    ; =: b
           r                            ; =: x
           s                            ; =: y
           (- x (* q r))                ; =: r
           (- y (* q s))                ; =: s
           )))))

(defn solve2 [eqs]
  (let [M (->> eqs (map :n) (reduce *))
        gMi (->> eqs (map (fn [{y :y n :n}] (let [Mi (/ M n) [f g] (gen_euc_pr n Mi)] [n (vector g Mi)]))) (apply conj {}))]
    (let [sol (->>
               eqs
               (map (fn [{y :y n :n}] (* y (apply * (gMi n)))))
               (reduce +)
               )]
      (- M (mod sol M)))))

;; (solve2 [{:y 3 :n 4} {:y 4 :n 5} {:y 1 :n 7}])
(assert (= (->> "0 17,x,13,19" parse into-eq solve2) 3417))
(assert (= (->> "0 67,7,59,61" parse into-eq solve2) 754018))
(assert (= (->> "0 67,x,7,59,61" parse into-eq solve2) 779210))
(assert (= (->> "0 67,7,x,59,61" parse into-eq solve2) 1261476))
(assert (= (->> "0 1789,37,47,1889" parse into-eq solve2) 1202161486))
(assert (= (->> example parse into-eq solve2) 1068781))

(->> "day13/data/part1.txt" slurp parse into-eq solve2)
