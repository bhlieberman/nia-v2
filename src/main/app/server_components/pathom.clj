(ns app.server-components.pathom
  (:require
   [mount.core :refer [defstate]]
   [taoensso.timbre :as log]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [com.wsscode.pathom3.connect.indexes :as pci]
   [com.wsscode.pathom3.connect.operation :as pco]
   [com.wsscode.pathom3.interface.eql :as p.eql]
   [com.wsscode.pathom3.connect.built-in.resolvers :as pbir]
   [com.wsscode.pathom3.connect.built-in.plugins :as pbip]
   [com.wsscode.pathom3.interface.async.eql :as p.a.eql]
   [com.wsscode.pathom3.interface.smart-map :as psm]
   [com.wsscode.pathom3.plugin :as p.plugin]
   [app.model.mock-database :as db]))

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

(def registry
  [(pbir/static-table-resolver
    `cantos :canto/id
    {1 {:canto/name "Damietta"}
     2 {:canto/name "The Battle-field of the Pyramids"}
     4 {:canto/name "The Gardens of Rosetta Seen from A Dahabiah"}})
   (pbir/constantly-resolver
    :canto/contents
    {1 {:canto/thesis (get-static-resource {:type :canto
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
   (pbir/attribute-table-resolver
    :canto/contents :canto/id
    [:canto/thesis])])

(let [sm (psm/smart-map (pci/register registry) {:canto/id 1})]
  (:canto/thesis sm))