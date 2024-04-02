(ns app.ui.nia.core
  (:require [clojure.string :as str]
            [com.fulcrologic.fulcro.algorithms.react-interop :as react-interop]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [com.fulcrologic.fulcro.dom :as dom]
            [com.fulcrologic.fulcro.react.hooks :as hooks]
            ["semantic-ui-react" :refer [AccordionTitle AccordionContent
                                         Accordion Icon]]))

(def ui-accordion-title (react-interop/react-factory AccordionTitle))
(def ui-accordion-content (react-interop/react-factory AccordionContent))
(def ui-accordion (react-interop/react-factory Accordion))
(def ui-icon (react-interop/react-factory Icon))

(defsc CantoAccordion [_ {:keys [idx canto-number content]}]
  {:use-hooks? true}
  (let [[active? set-active] (hooks/use-state 0)]
    (ui-accordion
     {}
     (ui-accordion-title
      {:active (= active? idx)
       :index idx
       :onClick (fn [_ title-props]
                  (let [idx (.-index title-props)
                        curr (.-active title-props)]
                    (set-active (if curr -1 idx))))}
      (ui-icon {:name "dropdown"})
      canto-number)
     (ui-accordion-content
      {:active (= active? idx)}
      content))))

(def ui-canto-accordion (comp/factory CantoAccordion))

(defsc Footnote [this {:footnote/keys [idx text]
                       :canto/keys [id]
                       :as _props}]
  {:query [:canto/id :footnote/idx :footnote/text :canto/id]
   :ident :footnote/idx
   :initial-state
   (fn [{:keys [idx text] :canto/keys [id]}]
     {:footnote/idx idx
      :footnote/text text
      :canto/id id})}
  ;; ultimately I should add a :parens/level param to this query??
  (let [load-params {:params {:pathom/context {:canto/id id}}}
        on-click (fn [] (df/load! this [:footnote/idx idx] this load-params))]
    (dom/div {} (dom/div {} (dom/section {}
       (dom/button
        :.ui.button {:onClick on-click}
        (str "Load Footnote " idx))
       (map-indexed (fn [i line] (dom/p {:key i} line)) (str/split-lines text)))))))

(def ui-footnote (comp/factory Footnote {:keyfn random-uuid}))

(def footnote-state [{:idx 1 :canto/id 1 :parens/level 4}
                     {:idx 2 :canto/id 1 :parens/level 4}
                     {:idx 1 :canto/id 2 :parens/level 2}
                     {:idx 1 :canto/id 2 :parens/level 3}
                     {:idx 1 :canto/id 4 :parens/level 4}
                     {:idx 2 :canto/id 4 :parens/level 4}
                     {:idx 3 :canto/id 4 :parens/level 4}
                     {:idx 4 :canto/id 4 :parens/level 4}
                     {:idx 5 :canto/id 4 :parens/level 4}])

(defsc Parentheses [this {:parens/keys [level text footnotes]}]
  {:query [:parens/level :parens/text {:parens/footnotes (comp/get-query Footnote)}]
   :ident :parens/level
   :initial-state
   (fn [{:keys [level text]}]
     {:parens/level level
      :parens/text text
      :parens/footnotes
      (mapv (partial comp/get-initial-state Footnote) footnote-state)})}
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
                     ;; do this like above with footnote initial state
                     (comp/get-initial-state ;; DON'T PUT THIS IN A VECTOR!!
                      Parentheses
                      {:level 1
                       :text "Hello parenthesis"})})}
  (dom/div
   (dom/section body)
   (dom/div
    (dom/section :.ui.accordion (ui-parens parentheses)))))

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
   (ui-canto-accordion {:idx id
                        :canto-number (str "Canto " id)
                        :content (ui-thesis thesis)}) #_(ui-thesis thesis)))

(def ui-canto (comp/factory Canto))

(defsc NIA [_this {:keys [canto-i canto-ii canto-iv]}]
  ;; at some point this should perhaps become a union query...
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
