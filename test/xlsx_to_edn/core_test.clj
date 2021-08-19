(ns xlsx-to-edn.core-test
  (:require [clojure.test :refer :all]
            [xlsx-to-edn.core :refer :all]))

(deftest basic-test
  
  (testing "select"
    (is (= [2 3] (select 2 [1 2 3 4])))
    (is (thrown? Exception (select 3 [1 2 3]))))
  
  (testing "update-vals"
    (is (= {:a 2 :b 3 :c 3} (update-vals {:a 1 :b 2 :c 3} [:a :b] inc))))
  
  (testing "point-round"
    (are [x y] (= x y)
      12.1 (point-round "0.121")
      98.0 (point-round 0.98)
      99.1 (point-round 0.991)))

  (testing "find-max-year"
    (are [x y] (= x y)
      2012 (find-max-year nil)
      2012 (find-max-year {})
      2020 (find-max-year {:2012 1 :2020 "test"}))))

;from prais2.content.cljc

(defrecord Row [h-name h-code h-lat h-lon n-ops n-deaths n-survivors survival-rate outer-low inner-low inner-high outer-high observed])

(def test-datasources
  {:2019
   [
    (Row. "Newcastle, Freeman Hospital" "FRE" 55.002386 -1.593643                         694	11	683	  98.4	95.5	96.3	98.6	99.1 nil)
    (Row. "Leicester, Glenfield Hospital" "GRL" 52.654229 -1.179836                       794	9	  785	  98.9	96.3	97.1	99.0	99.4 nil)
    (Row. "Glasgow, Royal Hospital for Children" "RHS" 55.862745 -4.342357                711	14	697	  98.0	96.9	97.6	99.3	99.7 nil)
    (Row. "Bristol Royal Hospital for Children" "BRC" 51.457899 -2.597014                 880	9	  871	  99.0	96.2	96.9	98.7	99.2 nil)
    (Row. "Southampton, Wessex Cardiothoracic Centre" "SGH" 50.932846 -1.432731           930	18	912	  98.1	96.6	97.2	98.9	99.4 nil)
    (Row. "Dublin, Our Lady's Children's Hospital" "OLS" 53.326005 -6.317399              892	11	881	  98.8	96.3	97.0	98.8	99.2 nil)
    (Row. "Liverpool, Alder Hey Hospital" "ACH" 53.419566 -2.900560                       1058 15	1043	98.6	96.0	96.6	98.5	98.9 nil)
    (Row. "Leeds General Infirmary" "LGI" 53.802109 -1.550870                             929	  6	923	  99.4	96.1	96.8	98.7	99.1 nil)
    (Row. "London, Royal Brompton Hospital" "NHB" 51.489012 -0.170759                     965  11	954	  98.9	96.9	97.4	99.1	99.4 nil)
    (Row. "London, Evelina London Children's Hospital" "GUY" 51.498044 -0.118835          1202 23	1179	98.1	96.3	96.8	98.5	98.9 nil)
    (Row. "Birmingham Children’s Hospital" "BCH" 52.484946 -1.894566                      1308 32	1276	97.6	95.4	96.0	97.9	98.3 nil)
    (Row. "London, Great Ormond Street Hospital for Children" "GOS" 51.522549 -0.120923   1774 15	1759	99.2	97.4	97.8	99.0	99.3 nil)
    ]
   :2018
   [                                                                                ;HSC	210	  8	  202	  96.2	98.3	94.8	96.2	100.0	100.0

    (Row. "London, Harley Street Clinic" "HSC" 51.520348 -0.147726                        210   8   202   96.2  94.8  96.2  100.0 100.0 nil)
    (Row. "Newcastle, Freeman Hospital" "FRE" 55.002386 -1.593643 681 12 669 98.2  95.3 96.2 98.5 99.1 nil)
    (Row. "Leicester, Glenfield Hospital" "GRL" 52.654229 -1.179836 769 5 764 99.4  96.4 97.0 99.0 99.3 nil)
    (Row. "Glasgow, Royal Hospital for Children" "RHS" 55.862745 -4.342357 718 13 705 98.2  96.7 97.4 99.2 99.6 nil)
    (Row. "Bristol Royal Hospital for Children" "BRC" 51.457899 -2.597014 868 9 859 99.0  96.2 96.9 98.7 99.2 nil)
    (Row. "Southampton, Wessex Cardiothoracic Centre" "SGH" 50.932846 -1.432731 964 17 947 98.2  96.5 97.1 98.9 99.3 nil)
    (Row. "Dublin, Our Lady's Children's Hospital" "OLS" 53.326005 -6.317399 906 15 891 98.3  96.4 96.9 98.8 99.2 nil)
    (Row. "Liverpool, Alder Hey Hospital" "ACH" 53.419566 -2.900560 1027 11 1016 98.9  96.4 97.0 98.7 99.1 nil)
    (Row. "Leeds General Infirmary" "LGI" 53.802109 -1.550870 984 12 972 98.8  97.0 97.5 99.1 99.4 nil)
    (Row. "London, Royal Brompton Hospital" "NHB" 51.489012 -0.170759 1002 21 981 97.9  96.5 97.1 98.8 99.2 nil)
    (Row. "London, Evelina London Children's Hospital" "GUY" 51.498044 -0.118835 1226 33 1193 97.3  96.2 96.7 98.4 98.9 nil)
    (Row. "Birmingham Children’s Hospital" "BCH" 52.484946 -1.894566 1361 33 1328 97.6  95.5 96.1 97.9 98.4 nil)
    (Row. "London, Great Ormond Street Hospital for Children" "GOS" 51.522549 -0.120923 1812 12 1800 99.3  97.2 97.6 98.8 99.1 nil)
    ]
   :2017
   [
    (Row. "London, Harley Street Clinic" "HSC" 51.520348 -0.147726 246 10 236 95.9 95.1 96.3 99.6 100.0 nil)
    (Row. "Newcastle, Freeman Hospital" "FRE" 55.002386 -1.593643 679 13 666 98.1 95.0 95.9 98.4 99.0 nil)
    (Row. "Leicester, Glenfield Hospital" "GRL" 52.654229 -1.179836 727 6 721 99.2 96.4 97.1 99.0 99.4 nil)
    (Row. "Glasgow, Royal Hospital for Children" "RHS" 55.862745 -4.342357 698 15 683 97.9 96.1 96.8 99.0 99.4 nil)
    (Row. "Bristol Royal Hospital for Children" "BRC" 51.457899 -2.597014 855 11 844 98.7 96.3 96.8 98.8 99.2 nil)
    (Row. "Southampton, Wessex Cardiothoracic Centre" "SGH" 50.932846 -1.432731 926 18 908 98.1 96.2 96.8 98.7 99.1 nil)
    (Row. "Dublin, Our Lady's Children's Hospital" "OLS" 53.326005 -6.317399 922 22 900 97.6 96.0 96.6 98.6 99.0 nil)
    (Row. "Liverpool, Alder Hey Hospital" "ACH" 53.419566 -2.900560 1075 12 1063 98.9 96.5 97.0 98.7 99.1 nil)
    (Row. "Leeds General Infirmary" "LGI" 53.802109 -1.550870 1029 17 1012 98.3 97.2 97.7 99.2 99.5 nil)
    (Row. "London, Royal Brompton Hospital" "NHB" 51.489012 -0.170759 1068 28 1040 97.4 96.4 97.0 98.7 99.2 nil)
    (Row. "London, Evelina London Children's Hospital" "GUY" 51.498044 -0.118835 1231 36 1195 97.1 95.8 96.3 98.1 98.6 nil)
    (Row. "Birmingham Children’s Hospital" "BCH" 52.484946 -1.894566 1363 39 1324 97.1 95.4 96.0 97.8 98.2 nil)
    (Row. "London, Great Ormond Street Hospital for Children" "GOS" 51.522549 -0.120923 1885 16 1869 99.2 97.1 97.5 98.7 99.0 nil)]

   :2016
   [
    (Row. "London, Harley Street Clinic" "HSC" 51.520348 -0.147726 332 8 324 97.6 95.5 96.7 99.4 100.0 nil)
    (Row. "Newcastle, Freeman Hospital" "FRE" 55.002386 -1.593643 657 18 639 97.3 94.7 95.6 98.2 98.8 nil)
    (Row. "Leicester, Glenfield Hospital" "GRL" 52.654229 -1.179836 671 11 660 98.4 96.3 97.0 99.1 99.6 nil)
    (Row. "Glasgow, Royal Hospital for Children" "RHS" 55.862745 -4.342357 724 20 704 97.2 96.5 97.2 99.0 99.4 nil)
    (Row. "Bristol Royal Hospital for Children" "BRC" 51.457899 -2.597014 841 15 826 98.2 96.6 97.1 98.9 99.4 nil)
    (Row. "Southampton, Wessex Cardiothoracic Centre" "SGH" 50.932846 -1.432731 872 21 851 97.6 96.0 96.7 98.6 99.1 nil)
    (Row. "Dublin, Our Lady's Children's Hospital" "OLS" 53.326005 -6.317399 947 20 927 97.9 96.6 97.1 98.9 99.3 nil)
    (Row. "Liverpool, Alder Hey Hospital" "ACH" 53.419566 -2.900560 1068 9 1059 99.2 96.4 97.0 98.7 99.2 nil)
    (Row. "Leeds General Infirmary" "LGI" 53.802109 -1.550870 1086 17 1069 98.4 97.2 97.7 99.2 99.5 nil)
    (Row. "London, Royal Brompton Hospital" "NHB" 51.489012 -0.170759 1126 28 1098 97.5 96.4 97.0 98.7 99.1 nil)
    (Row. "London, Evelina London Children's Hospital" "GUY" 51.498044 -0.118835 1247 44 1203 96.5 95.9 96.5 98.2 98.6 nil)
    (Row. "Birmingham Children’s Hospital" "BCH" 52.484946 -1.894566 1381 37 1344 97.3 96.0 96.5 98.2 98.6 nil)
    (Row. "London, Great Ormond Street Hospital for Children" "GOS" 51.522549 -0.120923 1894 16 1878 99.2 97.0 97.5 98.7 99.0 nil)]

   :2015
   [
    (Row. "London, Harley Street Clinic" "HSC" 51.520348 -0.147726 418 5 413 98.8 94.5 95.7 98.8 99.3 nil)
    (Row. "Leicester, Glenfield Hospital" "GRL" 52.654229 -1.179836 607 14 593 97.7 95.2 96.0 98.5 99.2 nil)
    (Row. "Newcastle, Freeman Hospital" "FRE" 55.002386 -1.593643 668 15 653 97.8 95.2 96.1 98.5 99.1 nil)
    (Row. "Glasgow, Royal Hospital for Children" "RHS" 55.862745 -4.342357 760 27 733 96.3 95.5 96.3 98.6 99.1 nil)
    (Row. "Bristol Royal Hospital for Children" "BRC" 51.457899 -2.597014 835 14 821 98.3 95.8 96.5 98.6 99.0 nil)
    (Row. "Southampton, Wessex Cardiothoracic Centre" "SGH" 50.932846 -1.432731 829 14 815 98.3 95.1 95.8 98.1 98.7 nil)
    (Row. "Leeds General Infirmary" "LGI" 53.802109 -1.550870 1038 22 1016 97.9 96.3 96.9 98.8 99.1 nil)
    (Row. "Dublin, Our Lady's Children's Hospital" "OLS" 53.326005 -6.317399 983 23 960 97.7 95.6 96.2 98.3 98.8 nil)
    (Row. "London, Royal Brompton Hospital" "NHB" 51.489012 -0.170759 1094 19 1075 98.3 96.2 96.8 98.5 99.0 nil)
    (Row. "Liverpool, Alder Hey Hospital" "ACH" 53.419566 -2.900560 1132 20 1112 98.2 95.5 96.1 98.1 98.6 nil)
    (Row. "London, Evelina London Children's Hospital" "GUY" 51.498044 -0.118835 1220 35 1185 97.1 95.3 96.0 97.9 98.4 nil)
    (Row. "Birmingham Children’s Hospital" "BCH" 52.484946 -1.894566 1457 36 1421 97.5 94.9 95.5 97.4 97.9 nil)
    (Row. "London, Great Ormond Street Hospital for Children" "GOS" 51.522549 -0.120923 1892 19 1873 99.0 96.5 96.9 98.4 98.7 nil)]

   :2014
   [(Row. "Belfast, Royal Victoria Hospital" "RVB" 54.594167 -5.953666 204 2 202 99.0 95.1 96.6 100.0 100.0 nil)
    (Row. "London, Harley Street Clinic" "HSC" 51.520348 -0.147726 482 7 475 98.5 94.8 95.9 98.8 99.4 nil)
    (Row. "Leicester, Glenfield Hospital" "GRL" 52.654229 -1.179836 582 11 571 98.1 95.5 96.4 98.8 99.3 nil)
    (Row. "Newcastle, Freeman Hospital" "FRE" 55.002386 -1.593643 678 15 663 97.8 95.3 96.0 98.5 99.0 nil)
    (Row. "Glasgow, Royal Hospital for Children" "RHS" 55.862745 -4.342357 787 28 759 96.4 95.7 96.4 98.6 99.1 nil)
    (Row. "Bristol Royal Hospital for Children" "BRC" 51.457899 -2.597014 835 19 816 97.7 96.0 96.8 98.7 99.2 nil)
    (Row. "Southampton, Wessex Cardiothoracic Centre" "SGH" 50.932846 -1.432731 890 17 873 98.1 95.4 96.2 98.3 98.8 nil)
    (Row. "Leeds General Infirmary" "LGI" 53.802109 -1.550870 976 23 953 97.6 96.5 97.1 98.9 99.3 nil)
    (Row. "Dublin, Our Lady's Children's Hospital" "OLS" 53.326005 -6.317399 1056 23 1033 97.8 96.2 96.8 98.6 99.1 nil)
    (Row. "London, Royal Brompton Hospital" "NHB" 51.489012 -0.170759 1107 12 1095 98.9 96.5 97.0 98.7 99.1 nil)
    (Row. "Liverpool, Alder Hey Hospital" "ACH" 53.419566 -2.900560 1146 27 1119 97.6 95.8 96.4 98.3 98.7 nil)
    (Row. "London, Evelina London Children's Hospital" "GUY" 51.498044 -0.118835 1204 39 1165 96.8 95.6 96.2 98.1 98.5 nil)
    (Row. "Birmingham Children’s Hospital" "BCH" 52.484946 -1.894566 1481 30 1451 98.0 95.2 95.8 97.6 98.1 nil)
    (Row. "London, Great Ormond Street Hospital for Children" "GOS" 51.522549 -0.120923 1881 30 1851 98.4 96.6 97.0 98.4 98.7 nil)]

   :2013
   [(Row. "Belfast, Royal Victoria Hospital" "RVB" 54.594167 -5.953666 232 4 228 98.3 95.3 96.6 99.6 100.0 nil)
    (Row. "London, Harley Street Clinic" "HSC" 51.520348 -0.147726 483 10 473 97.9 94.6 95.7 98.6 99.2 nil)
    (Row. "Leicester, Glenfield Hospital" "GRL" 52.654229 -1.179836 570 12 558 97.9 95.1 96.1 98.6 99.3 nil)
    (Row. "Newcastle, Freeman Hospital" "FRE" 55.002386 -1.593643 704 16 688 97.7 95.0 95.9 98.3 98.9 nil)
    (Row. "Glasgow, Royal Hospital for Children" "RHS" 55.862745 -4.342357 817 26 791 96.8 95.7 96.5 98.5 99.0 nil)
    (Row. "Bristol Royal Hospital for Children" "BRC" 51.457899 -2.597014 886 21 865 97.6 96.5 97.2 98.9 99.3 nil)
    (Row. "Southampton, Wessex Cardiothoracic Centre" "SGH" 50.932846 -1.432731 914 14 900 98.5 96.0 96.6 98.6 99.0 nil)
    (Row. "Leeds General Infirmary" "LGI" 53.802109 -1.550870 919 32 887 96.5 96.2 96.8 98.7 99.1 nil)
    (Row. "Dublin, Our Lady's Children's Hospital" "OLS" 53.326005 -6.317399 738 22 716 97.0 95.9 96.6 98.8 99.2 nil)
    (Row. "London, Royal Brompton Hospital" "NHB" 51.489012 -0.170759 1117 18 1099 98.4 96.5 97.1 98.7 99.1 nil)
    (Row. "Liverpool, Alder Hey Hospital" "ACH" 53.419566 -2.900560 1195 40 1155 96.7 95.7 96.3 98.2 98.7 nil)
    (Row. "London, Evelina London Children's Hospital" "GUY" 51.498044 -0.118835 1165 42 1123 96.4 95.6 96.2 98.1 98.5 nil)
    (Row. "Birmingham Children’s Hospital" "BCH" 52.484946 -1.894566 1467 44 1423 97.0 95.0 95.6 97.5 98.0 nil)
    (Row. "London, Great Ormond Street Hospital for Children" "GOS" 51.522549 -0.120923 1828 32 1796 98.3 96.7 97.0 98.4 98.7 nil)]})

(defn record->map
  "Transforms records into maps, without the :observed key"
  [m]
  (into {}
        (for [[k v] m]
          [k (mapv #(dissoc (into {} %) :observed) v)])))

(deftest output-test

  (testing "data.edn"
    (is (= (record->map test-datasources)
           (clojure.edn/read-string (slurp "test/xlsx_to_edn/test-data.edn"))))))
