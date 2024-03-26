(ns app.ui.canto
  (:require [app.model.poem :as p]
            [app.ui.parens :refer [Parens ui-parens]]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [goog.object :as gobj]))

(defsc Canto [this {:poem/keys [canto]
                    :parens/keys [highlighted-count]}]
  {:query [:poem/canto {:parens/highlighted-count (comp/get-query Parens)}]
   :ident (fn [] [:component/id :canto])
   ;; constant ident like above means there MUST be initial state...
   :initial-state {}
   :route-segment ["poem"]}
  ;; TODO: get the transaction to update my data tree...
  ;; DONE!
  ;; perhaps each canto could even maintain its own routing
  ;; state so that a user could bounce around more freely
  (ui-parens
   (comp/computed {:ui/highlighted-color :blue
                   :ui/highlighted-count highlighted-count}
                  {:onClick
                   (fn [_e]
                     ;; maybe this should stay here because
                     ;; the mutation does update in this component's
                     ;; path, which I imagine is the right thing
                     (let [data-attrs (.. _e -target -dataset)
                           side (gobj/get data-attrs "direction")
                           computed-f (case side "up" inc "down" dec identity)]
                       (comp/transact! this [(p/change-parens {:f computed-f})])))})))

