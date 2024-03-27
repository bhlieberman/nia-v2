(ns user
  (:require
   [clojure.tools.namespace.repl :as tools-ns :refer [set-refresh-dirs]]
   [expound.alpha :as expound]
   [clojure.java.io :as io]
   [clojure.spec.alpha :as s]))

(set-refresh-dirs "src/main" "src/dev" "src/test")
(alter-var-root #'s/*explain-out* (constantly expound/printer))

(comment
  (def parens-svg (slurp (io/resource "public/parens.svg")))
  parens-svg)
(comment
  (require 'development)
  (in-ns 'development)
  (development/start)
  (development/restart))
