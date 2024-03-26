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
  [{:keys [f]}]
  (action
   [{:keys [state]}]
   ;; ok so this is updating in parens path, not the canto...
   (let [path [:component/id :canto :parens/highlighted-count]]
     (swap! state update-in path f))))