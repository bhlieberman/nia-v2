(ns app.ui.canto
  (:require [app.model.poem :as p]
            [app.ui.parens :refer [Parens ui-parens]]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]))

(defsc Canto [_this {:poem/keys [canto]
                     :ui/keys [highlighted-count]}]
  {:query [:poem/canto {:ui/highlighted-count (comp/get-query Parens)}]
   :ident (fn [] [:component/id :canto])
   ;; constant ident like above means there MUST be initial state...
   :initial-state (fn [_params] [(comp/get-initial-state Parens)])
   :route-segment ["poem"]}
  ;; TODO: get the transaction to update my data tree...
  (ui-parens (comp/computed {:ui/highlighted-color :blue
                             :ui/highlighted-count highlighted-count} 
                            {:onClick (fn [_e] 
                                        (comp/transact! _this [(p/change-parens {})])
                                        (js/console.log "clicking parens button"))})))