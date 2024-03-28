(ns app.ui.root
  (:require [app.ui.nia.core :refer [NIA ui-nia]]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]))

(defsc Root [this {:keys [nia]}]
  {:query         [{:nia (comp/get-query NIA)}]
   :initial-state (fn [params]
                    {:nia
                     (comp/get-initial-state
                      NIA {:canto-i []
                           :canto-ii []
                           :canto-iv []})})}
  (ui-nia nia))
