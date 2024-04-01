(ns app.server-components.poem
  (:require [clojure.java.io :as io]))

(defmulti resource-path :type)

(defmethod resource-path :footnote [m]
  (let [{:keys [canto parens file]} m
        path (str "c" canto "p" parens "-" file ".txt")]
    (str "public/assets/footnotes/" path)))

(defmethod resource-path :canto [m]
  (str "public/assets/theses/c" (:canto m) ".txt"))

(defmethod resource-path :parens [m]
  (let [{:keys [canto parens]} m]
    (str "public/assets/parens/c" canto "p" parens ".txt")))

(defn get-static-resource [opts]
  (slurp (io/resource (resource-path opts))))

(def poem-data {1 {:canto/thesis (get-static-resource {:type :canto
                                                       :canto 1})
                   :canto/parens
                   (for [i (range 1 6)
                         :let [opts {:type :parens
                                     :canto 1
                                     :parens i}]]
                     (get-static-resource opts))
                   :canto/footnotes
                   [(get-static-resource {:type :footnote
                                          :canto 1
                                          :parens 4
                                          :file "4-1"})
                    (get-static-resource {:type :footnote
                                          :canto 1
                                          :parens 4
                                          :file "4-2"})]}
                2 {:canto/thesis (get-static-resource {:type :canto
                                                       :canto 2})
                   :canto/parens [] :canto/footnotes
                   (for [m [{:parens 2} {:parens 3}]]
                     (-> m
                         (merge {:canto 2
                                 :type :footnote
                                 :file 1})
                         get-static-resource))}
                4 {:canto/thesis ""
                   :canto/parens []
                   :canto/footnotes
                   (for [i (range 1 6)
                         :let [opts {:file i
                                     :canto 4
                                     :parens 4
                                     :type :footnote}]]
                     (get-static-resource opts))}})