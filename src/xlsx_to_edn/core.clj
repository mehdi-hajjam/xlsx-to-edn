(ns xlsx-to-edn.core
  (:require [dk.ative.docjure.spreadsheet :as dj]
            [clojure.java.io :as io])
  (:gen-class))

(def hosp-data {"FRE" {:h-name "Newcastle, Freeman Hospital"  
                       :h-lat 55.002386 
                       :h-lon -1.593643}
                "GRL" {:h-name "Leicester, Glenfield Hospital" 
                       :h-lat 52.654229 
                       :h-lon -1.179836}
                "RHS" {:h-name "Glasgow, Royal Hospital for Children" 
                       :h-lat 55.862745 
                       :h-lon -4.342357}
                "BRC" {:h-name "Bristol Royal Hospital for Children" 
                       :h-lat 51.457899 
                       :h-lon -2.597014}
                "SGH" {:h-name "Southampton, Wessex Cardiothoracic Centre" 
                       :h-lat 50.932846 
                       :h-lon -1.432731}
                "OLS" {:h-name "Dublin, Our Lady's Children's Hospital" 
                       :h-lat 53.326005 
                       :h-lon -6.317399}
                "ACH" {:h-name "Liverpool, Alder Hey Hospital" 
                       :h-lat 53.419566 
                       :h-lon -2.900560}
                "LGI" {:h-name "Leeds General Infirmary" 
                       :h-lat 53.802109 
                       :h-lon -1.550870} 
                "NHB" {:h-name "London, Royal Brompton Hospital" 
                       :h-lat 51.489012 
                       :h-lon -0.170759}
                "GUY" {:h-name "London, Evelina London Children's Hospital" 
                       :h-lat 51.498044 
                       :h-lon -0.118835}
                "BCH" {:h-name "Birmingham Children’s Hospital" 
                       :h-lat 52.484946 
                       :h-lon -1.894566}
                "GOS" {:h-name "London, Great Ormond Street Hospital for Children"  
                       :h-lat 51.522549 
                       :h-lon -0.120923}
                "HSC" {:h-name "London, Harley Street Clinic"
                       :h-lat 51.520348
                       :h-lon -0.147726}
                "RVB" {:h-name "Belfast, Royal Victoria Hospital"
                       :h-lat 54.594167
                       :h-lon -5.953666}})

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

(defn point-round
  "Returns a double between 0.0 and 100.0"
  [n]
  (cond
    (string? n) (point-round (Float/parseFloat n))
    :else (Double/parseDouble (format "%.1f" (* 100 n)))))

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
  (println "File data.edn successfully created."))

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
