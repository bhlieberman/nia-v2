(ns app.model.poem
  (:require 
   [clojure.java.io :as io]
   [com.wsscode.pathom.connect :as pc]))

(def footnote-table (atom {1 {:footnote/idx 1
                              :footnote/text (slurp (io/resource "public/assets/footnotes/canto_i/four_one.txt"))}}))

(defn get-parens-from-footnote* 
  "A transducer to return only the lines of a footnote
   that meet the criteria specified in `pred`. For internal
   use only."
  [ftn pred]
  (keep-indexed pred ftn))

(pc/defresolver get-footnote [_ {:footnote/keys [idx]}]
  {::pc/input #{:footnote/idx}
   ::pc/output [:footnote/idx :footnote/text]}
  (get @footnote-table idx))