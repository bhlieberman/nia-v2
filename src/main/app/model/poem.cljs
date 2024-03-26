(ns app.model.poem
  (:require [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]))

(defmutation change-parens
  "Changes the parens value to represent the appropriate level of
   nesting in the poem. Color is indicated by a simple
   one-to-one correspondence
   
   0 - red
   1 - blue
   2 - yellow
   3 - green
   4 - purple
   5 - orange"
  [_]
  (action
   [{:keys [state]}]
   (let [path [:component/id :parens :ui/highlighted-count]]
     (swap! state update-in path inc))))