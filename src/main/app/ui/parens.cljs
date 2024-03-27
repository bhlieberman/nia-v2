(ns app.ui.parens
  (:require 
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.data-fetch :as df]))

;; just as an experiment, learn to render
;; these simple components in Fulcro style
(defsc Parens [_this 
               {:ui/keys [highlighted-count highlighted-color]}
               {:keys [onClick]}]
  {:query [:ui/highlighted-count :ui/highlighted-color]
   :ident (fn [] [:component/id :parens])}
  ;; I'm gonna want to add an ident for each canto's parens sub-component
  ;; so like {:ident [:parens/id :params/id]}
  (let [button-text (fn [count chr] (apply str (repeat count chr)))
        button-attrs {:style {:color highlighted-color} 
                      :onClick onClick}]
    (dom/div
     :.ui.horizontal.segments.container
     {}
     (dom/div
      :.ui.massive.button
      (assoc button-attrs :data-direction "down")
      (button-text highlighted-count "("))
     (dom/div
      :.ui.segment.compact.center.aligned
      {:style {:maxWidth "200px"}}
      "poem text here")
     (dom/div
      :.ui.massive.button
      (assoc button-attrs :data-direction "up")
      (button-text highlighted-count ")"))
     (dom/div
      :.ui.medium.button
      ;; ok this is wrong but I'm learning...
      {:onClick #(df/load! _this [:svg/parens] _this)}
      "Click me"))))

(def ui-parens (comp/factory Parens))