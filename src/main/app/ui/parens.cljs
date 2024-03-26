(ns app.ui.parens
  (:require [app.model.poem :as p]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]))

;; just as an experiment, learn to render
;; these simple components in Fulcro style
(defsc Parens [this 
               {:ui/keys [highlighted-count highlighted-color]}
               {:keys [onClick]}]
  {:query [:ui/highlighted-count :ui/highlighted-color]
   :ident (fn [] [:component/id :parens]) 
   :initial-state (fn [{:keys [highlighted-count
                               highlighted-color] :as _params}]
                    {:ui/highlighted-count highlighted-count
                     :ui/highlighted-color highlighted-color})}
  (let [button-text (fn [count chr] (apply str (repeat count chr)))
        button-attrs {:style {:color highlighted-color}
                      :onClick onClick}]
    (dom/div
     :.ui.horizontal.segments.container
     {}
     (dom/div
      :.ui.massive.button
      button-attrs
      (button-text highlighted-count "("))
     (dom/div
      :.ui.segment.compact.center.aligned
      {:style {:maxWidth "200px"}}
      "poem text here")
     (dom/div
      :.ui.massive.button
      button-attrs
      (button-text highlighted-count ")")))))

(def ui-parens (comp/factory Parens))