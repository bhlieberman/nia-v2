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

(defsc Footnote [this {:footnote/keys [idx text]}]
  {:query [:canto/id :footnote/idx :footnote/text]
   :ident :footnote/idx
   :initial-state {:footnote/idx :param/idx
                   :footnote/text :param/text}}
  (let [load-params {:params {:pathom/context {:canto/id idx}}}
        on-click (fn [] (df/load! this [:footnote/idx idx] this load-params))]
    (dom/div
     {}
     (dom/div
      {}
      (dom/section
       {}
       (dom/button
        :.ui.button {:onClick on-click}
        (str "Load Footnote " idx))
       (map-indexed
        (fn [i line] (dom/p {:key i} line))
        (str/split-lines text)))))))

(def ui-footnote (comp/factory Footnote {:keyfn :footnote/idx}))

(def footnote-state [{:idx 1 :canto/id 1 :level 4}
                     {:idx 2 :canto/id 1 :level 4}
                     {:idx 1 :canto/id 2 :level 2}
                     {:idx 1 :canto/id 2 :level 3}
                     {:idx 1 :canto/id 4 :level 4}
                     {:idx 2 :canto/id 4 :level 4}
                     {:idx 3 :canto/id 4 :level 4}
                     {:idx 4 :canto/id 4 :level 4}
                     {:idx 5 :canto/id 4 :level 4}])

(defsc Parentheses [_ {:parens/keys [level text footnotes]}]
  {:query [:thesis/id :parens/level :parens/text {:parens/footnotes (comp/get-query Footnote)}]
   ;; I think 7.2.3 of guide (Ident Generation) will come in handy here
   :ident :parens/level
   :initial-state
   (fn [{:keys [level text footnotes]}]
     {:parens/level level
      :parens/text text
      :parens/footnotes footnotes})}
  (dom/div
   (dom/h4 "Parens level: " level)
   (dom/p "Parens text: " text)
   (dom/ol
    (map ui-footnote footnotes))))

(def ui-parens (comp/factory Parentheses {:keyfn :parens/level}))

(defn footnote-flt [lvl ct]
  (into [] (comp (filter (fn [m]
                           (and (= (:level m) lvl)
                                (= (:canto/id m) ct))))
                 (map (partial comp/get-initial-state Footnote)))
        footnote-state))

(defn parens-state [id]
  (into []
        (comp
         (filter #(= (:thesis/id %) id))
         (map (partial comp/get-initial-state Parentheses)))
        (concat
         (for [i (range 1 6)]
           {:thesis/id 1
            :level i
            :text (str "text" i)
            :footnotes
            (footnote-flt i 1)})
         (for [i (range 1 6)]
           {:thesis/id 2
            :level i
            :text (str "text" i)
            :footnotes
            (footnote-flt i 2)})
         (for [i (range 1 6)]
           {:thesis/id 4
            :level i
            :text (str "text" i)
            :footnotes
            (footnote-flt i 4)}))))

(defsc Thesis [_this {:thesis/keys [id body parentheses]}]
  {:query [:thesis/id :thesis/body {:thesis/parentheses (comp/get-query Parentheses)}]
   :ident :thesis/id ; this will be an ID like c1
   :initial-state
   (fn [{:keys [id body]}]
     {:thesis/id id
      :thesis/body body
      :thesis/parentheses
      (parens-state id)})}
  (dom/div
   (dom/section body)
   (dom/div
    (dom/section
     :.ui.accordion
     (map ui-parens parentheses)))))

(def ui-thesis (comp/factory Thesis {:keyfn :canto/thesis}))

(defsc Canto [_this {:canto/keys [id thesis]}]
  {:query [:canto/id {:canto/thesis (comp/get-query Thesis)}]
   :ident :canto/id
   :initial-state
   (fn [{:keys [id]}]
     {:canto/id id
      :canto/thesis
      ;; here's where I am getting the same footnotes every time...
      (comp/get-initial-state Thesis {:id id})})}
  (dom/div
   :.ui.container.segment
   (dom/h3 "Canto " id)
   (ui-canto-accordion {:idx id
                        :canto-number (str "Canto " id)
                        :content (ui-thesis thesis)})))

(def ui-canto (comp/factory Canto))

(defsc NIA [_this {:keys [canto-i canto-ii canto-iv]}]
  ;; at some point this should perhaps become a union query...
  {:query [{:canto-i (comp/get-query Canto)}
           {:canto-ii (comp/get-query Canto)}
           {:canto-iv (comp/get-query Canto)}]
   :ident (fn [] [:component/id :nia/root])
   :initial-state
   (fn [_]
     {:canto-i (comp/get-initial-state
                Canto {:id 1
                       :thesis 1})
      :canto-ii (comp/get-initial-state
                 Canto {:id 2
                        :thesis 2})
      :canto-iv (comp/get-initial-state
                 Canto {:id 4
                        :thesis 4})})
   :route-segment ["nia"]}
  (dom/div
   (dom/h1 "NIA v2")
   (dom/div
    :.ui.container
    (ui-canto canto-i)
    (ui-canto canto-ii)
    (ui-canto canto-iv))))

(def ui-nia (comp/factory NIA))
