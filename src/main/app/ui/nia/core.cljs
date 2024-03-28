(ns app.ui.nia.core
  (:require [clojure.string :as str]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]))

(defsc Footnote [_this {:footnote/keys [idx text] :as _props}]
  {:query [:footnote/idx :footnote/text]
   :ident :footnote/idx
   :initial-state (fn [{:keys [idx text]}] {:footnote/idx idx :footnote/text text})}
  (dom/div
   {}
   (dom/div
    (dom/h1 "This is footnote number: " idx)
    (dom/section
     (map dom/p (str/split-lines text))))))

(def ui-footnote (comp/factory Footnote {:keyfn :footnote/idx}))

(defsc Parentheses [_this {:parens/keys [level text footnotes]}]
  {:query [:parens/level :parens/text {:parens/footnotes (comp/get-query Footnote)}]
   :ident :parens/level
   :initial-state (fn [{:keys [level text]}]
                    {:parens/level level
                     :parens/text text
                     :parens/footnotes
                     [(comp/get-initial-state
                       Footnote {:idx 4 :text nil})]})}
  (dom/div
   (dom/h4 "Parens level: " level)
   (dom/p "Parens text: " text)
   (dom/ol
    (map ui-footnote footnotes))))

(def ui-parens (comp/factory Parentheses))

(defsc Thesis [_this {:thesis/keys [id body parentheses]}]
  {:query [:thesis/id :thesis/body {:thesis/parentheses (comp/get-query Parentheses)}]
   :ident :thesis/id ; this will be an ID like c1
   :initial-state (fn [{:keys [id body]}]
                    {:thesis/id id
                     :thesis/body body
                     :thesis/parentheses
                     ;; later this will have conditional logic
                     ;; to determine how many parentheses each
                     ;; thesis has, but for now: simple is best 
                     (comp/get-initial-state ;; DON'T PUT THIS IN A VECTOR!!
                      Parentheses
                      {:level 1
                       :text "Hello parenthesis"})})}
  (dom/div
   (dom/section body)
   (dom/div
    (dom/section (ui-parens parentheses)))))

(def ui-thesis (comp/factory Thesis {:keyfn :canto/thesis}))

(defsc Canto [_this {:canto/keys [id thesis]}]
  {:query [:canto/id {:canto/thesis (comp/get-query Thesis)}]
   :ident :canto/id
   :initial-state (fn [{:keys [id]}]
                    {:canto/id id
                     :canto/thesis
                     (comp/get-initial-state
                      Thesis
                      {:id id
                       :body "thesis body"})})}
  (dom/div 
   :.ui.container.segment
   (dom/h3 "Canto " id)
   (ui-thesis thesis)))

(def ui-canto (comp/factory Canto))

(defsc NIA [_this {:keys [canto-i canto-ii canto-iv]}]
  {:query [{:canto-i (comp/get-query Canto)}
           {:canto-ii (comp/get-query Canto)}
           {:canto-iv (comp/get-query Canto)}]
   :ident (fn [] [:component/id :nia/root])
   :initial-state (fn [_] {:canto-i (comp/get-initial-state Canto {:id 1})
                           :canto-ii (comp/get-initial-state Canto {:id 2})
                           :canto-iv (comp/get-initial-state Canto {:id 4})})
   :route-segment ["nia"]}
  (dom/div
   (dom/h1 "NIA v2")
   (dom/div
    :.ui.container
    (ui-canto canto-i)
    (ui-canto canto-ii)
    (ui-canto canto-iv))))

(def ui-nia (comp/factory NIA))
