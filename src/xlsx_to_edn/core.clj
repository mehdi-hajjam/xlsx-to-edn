(ns xlsx-to-edn.core
  (:require [dk.ative.docjure.spreadsheet :as dj]
            [clojure.java.io :as io])
  (:import java.util.Locale)
  (:gen-class))

;Loads part of hospitals metadata from the file hospitals-location.edn
;namely name, latitude and longitude
(def hosp-data
  (->> (slurp "resources/hospitals-location.edn")
       (clojure.edn/read-string)))

(defn load-xlsx
  "Loads all the columns of interest (notably ignores F)"
  [path]
  (as-> (dj/load-workbook path) wb
        (dj/select-sheet (dj/sheet-name (first (dj/sheet-seq wb))) wb)
        (dj/select-columns {:A :h-code 
                            :B :n-ops 
                            :C :n-deaths 
                            :D :n-survivors
                            :E :survival-rate
                            :G :outer-low
                            :H :inner-low
                            :I :inner-high
                            :J :outer-high} wb)))

(defn select
  "selects the actual data lines"
  [hosp-nb loaded]
  (cond 
    (> hosp-nb (+ (count loaded) 1)) 
      (throw (Exception. "There are not that many hospitals in the data."))
    :else 
      (subvec loaded 1 (+ hosp-nb 1))))

(defn update-vals 
  "Applies f to all the keys from vals in map"
  [map vals f]
  (reduce #(update-in % [%2] f) map vals))

(defn my-format 
  "Returns a format based on the chosen locale"
  [fmt n & [locale]]
  (let [locale (if locale (Locale. locale)
                   (Locale/getDefault))]
    (String/format locale fmt (into-array Object [n]))))

(defn point-round
  "Returns a double between 0.0 and 100.0"
  [n]
  (cond
    (string? n) (point-round (Float/parseFloat n))
    :else (Double/parseDouble (my-format "%.1f" (* 100 n) "en-Gb"))))

(defn format-percentages
  "Returns the percentages as numbers between 0 and 100, with 1 decimal points"
  [m]
  (let [ks [:survival-rate :outer-low :inner-low :inner-high :outer-high]]
    (mapv #(update-vals % ks point-round) m)))

(defn format-integers
  "Returns integers where the loading has created floats (e.g. 694.0 survivors)"
  [m]
  (let [ks [:n-ops :n-deaths :n-survivors]]
    (mapv #(update-vals % ks int) m)))

(defn add-info
  "adds the info of map m to each corresponding map in data"
  [m data]
  (mapv #(merge %1 (m (:h-code %1))) data))

(defn load-previous-data
  "Loads data from the file previous-data.edn"
  []
  (->> (slurp "resources/previous-data.edn")
       (clojure.edn/read-string)))

(defn find-max-year
  "Returns the max year from a map m with keys as years (e.g. :2019)"
  [m]
  (cond 
    (or (= m {}) (= m nil)) 2012 ;so that the first key is :2013
    :else (apply max (mapv #(Integer. (name %)) (keys m)))))

(defn add-history
  "adds the data from previous-data.edn file"
  [previous-data new-data]
  (let [k (+ (find-max-year previous-data) 1)]
    (assoc previous-data (keyword (str k)) new-data)))

(defn ->file
  "prints datastructure to a full path"
  [path data]
  (spit (io/file path) data)
  (println "File successfully created."))

(defn -main
  [filepath hosp-nb]
  (->> filepath
       (load-xlsx)
       (select (Integer. hosp-nb))
       (format-percentages)
       (format-integers)
       (add-info hosp-data)
       (add-history (load-previous-data))
       (->file "resources/data.edn")))

;;
; Octopus
;;

(defn not-subclass?
  [row]
  (let [f (first row)]
    (cond
      (<= (count f) 8) true
      :else (not (= "Subclass" (subs (first row) 0 8))))
   ))

(defn clean-row
  "Returns a clean row"
  [row]
  (filterv #(not (nil? %)) row))

(defn addrow
  "Can be used with res as {} if needs be"
  [row res]
  (let [frow (clean-row row)]
    (assoc res #_(keyword (str (first frow))) (first frow) {:term (second frow)})))

(defn gdad?
  "Returns true if it is a grand-dad, false otherwise"
  [row]
  (not (some nil? row)))

(defn dad?
  "Returns true if it is a dad, false otherwise"
  [row]
  (= 1 (count (filterv nil? row))))

(defn son?
  "Returns true if it is a son, false otherwise"
  [row]
  (and (= 2 (count (filterv nil? row)))
       (not (nil? (first row)))))

(defn precision?
  "Returns true if it is a precision, false otherwise"
  [row]
  (and (<= 2 (count (filterv nil? row)))
       (nil? (first row))))

(defn nb-nil
  [v]
  (count (filterv nil? v)))

(defn list-gdad
  "Loops through seq and stores first level elements as :AC1-999 {:term 'Collections. Series....'}}"
  [seq]
  (loop [s seq
         res {}]
    (cond
      (empty? s) res
      (gdad? (first s)) (recur (rest s)
                               (addrow (first s) res))
      :else (recur (rest s)
                   res))))`

(defn maketree
  "Loops through a seq, and assoc back into the structure"
  [seq1] 
  (loop [s seq1
         lgdad 0
         ldad 0
         lson 0
         lgson 0
         lg2son 0
         lg3son 0
         lg4son 0
         lg5son 0
         res {}] 
    (cond
      (empty? s) res
      (gdad? (first s)) (recur (rest s)
                               (ffirst s)
                               ldad
                               lson
                               lgson
                               lg2son
                               lg3son
                               lg4son
                               lg5son
                               (addrow (first s) res))
      (dad? (first s)) (recur (rest s)
                              lgdad
                              (ffirst s)
                              lson
                              lgson
                              lg2son
                              lg3son
                              lg4son
                              lg5son
                              (update-in res [lgdad :children] merge (addrow (first s) {})))
      (son? (first s)) (recur (rest s)
                              lgdad
                              ldad
                              (ffirst s)
                              lgson
                              lg2son
                              lg3son
                              lg4son
                              lg5son
                              (update-in res [lgdad :children ldad :children] merge (addrow (first s) {}))) 
      (= 3 (nb-nil (first s))) (recur (rest s)
                                      lgdad
                                      ldad
                                      lson
                                      (ffirst s)
                                      lg2son
                                      lg3son
                                      lg4son
                                      lg5son
                                      (update-in res [lgdad :children ldad :children lson :children] merge (addrow (first s) {})))
      (= 4 (nb-nil (first s))) (recur (rest s)
                                      lgdad
                                      ldad
                                      lson
                                      lgson
                                      (ffirst s)
                                      lg3son
                                      lg4son
                                      lg5son
                                      (update-in res [lgdad :children ldad :children lson :children lgson :children] merge (addrow (first s) {})))
      (= 5 (nb-nil (first s))) (recur (rest s)
                                      lgdad
                                      ldad
                                      lson
                                      lgson
                                      lg2son
                                      (ffirst s)
                                      lg4son
                                      lg5son
                                      (update-in res [lgdad :children ldad :children lson :children lgson :children lg2son :children] merge (addrow (first s) {})))
      (= 6 (nb-nil (first s))) (recur (rest s)
                                      lgdad
                                      ldad
                                      lson
                                      lgson
                                      lg2son
                                      lg3son
                                      (ffirst s)
                                      lg5son
                                      (update-in res [lgdad :children ldad :children lson :children lgson :children lg2son :children lg3son :children] merge (addrow (first s) {})))
      (= 7 (nb-nil (first s))) (recur (rest s)
                                      lgdad
                                      ldad
                                      lson
                                      lgson
                                      lg2son
                                      lg3son
                                      lg4son
                                      (ffirst s)
                                      (update-in res [lgdad :children ldad :children lson :children lgson :children lg2son :children lg3son :children lg4son :children] merge (addrow (first s) {})))
      :else (recur (rest s)
                   lgdad
                   ldad
                   lson
                   lgson
                   lg2son
                   lg3son
                   lg4son
                   lg5son
                   res))))

(defn add-precision
  "Takes a rowseq and add precision to the previous string"
  [seq1]
  (loop [s seq1
         res []]
    (cond
      (empty? s) res
      (precision? (first s)) (recur (rest s)
                                    (conj (vec (butlast res)) (conj (vec (butlast (last res))) (str (last (last res)) " " (last (first s)))) #_(conj (subvec (last res) 0 2) (str (nth (last res) 2) " " (nth (first s) 2)))))
      :else (recur (rest s)
                   (conj res (first s))))))

(defn octopus
  "Tab is a string of a tab name"
  [tab]
  (->> (dj/load-workbook "/Users/mehdi/Projects/winton-centre/xlsx-to-edn/resources/LOC classifications for Octopus.xlsx")
       (dj/select-sheet tab)
       dj/row-seq
       (remove nil?)
       (mapv dj/cell-seq)
       (mapv #(mapv dj/read-cell %))
       (filterv not-subclass?)
       (add-precision)))

(defn ednify
  "Turns a tab into an edn structure.
   Then use https://repo.tiye.me/mvc-works/edn-formatter/ for instance to get the json"
  [tab]
  (let [seq1 (octopus tab)]
    [(assoc {} tab (maketree seq1))]))

(comment
  (def tabs ["A" "B" "D" "E" "F" "G" "H" "J" "K" "L" "M" "N" "P"
             "Q" "R" "S" "T" "U" "V" "Z"])

  (def dtabs (mapv #(ednify %) tabs))

  (def res (reduce merge (vec (apply concat dtabs))))

  (->file "resources/topics.edn" res)
  )
