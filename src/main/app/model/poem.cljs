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
   (let [count [:component/id :canto :parens/highlighted-count]
         ;; TODO: make this so swaps don't occur when current value is not 0 <= curr < 5
         new-db (swap! state update-in count f)
         color [:component/id :canto :parens/highlighted-color]
         new-count (get-in new-db [:component/id :canto :parens/highlighted-count])
         new-color (nth [:red :blue :yellow :green :purple :orange] new-count)]
     (swap! state assoc-in color new-color))))