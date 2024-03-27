(ns app.ui.nia.core
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.algorithms.denormalize :as fdn]
            [com.fulcrologic.fulcro.algorithms.normalize :as nc]
            [com.fulcrologic.fulcro.dom :as dom]))

(def nia-data
  {:root/poem
   [{:canto/i
     {:canto/thesis
      {:thesis/body ""
       :canto/parentheses
       [{:parens/level 1 :parens/text ""}
        {:parens/level 2 :parens/text ""}
        {:parens/level 3 :parens/text ""}
        {:parens/level 4
         :parens/text ""
         :parens/footnotes
         [{:footnote/idx 1 :footnote/text ""}
          {:footnote/idx 2 :footnote/text ""}]}
        {:parens/level 5 :parens/text ""}]}}}
    {:canto/ii
     {:canto/thesis
      {:thesis/body ""
       :canto/parentheses
       [{:parens/level 1 :parens/text ""}
        {:parens/level 2
         :parens/text ""
         :parens/footnotes
         [{:footnote/idx 1 :footnote/text ""}]}
        {:parens/level 3
         :parens/text ""
         :parens/footnotes
         [{:footnote/idx 1 :footnote/text ""}]}
        {:parens/level 4 :parens/text ""}
        {:parens/level 5 :parens/text ""}]}}}
    {:canto/iii
     {:canto/thesis
      {:thesis/body ""
       :canto/parentheses []}}}
    {:canto/iv
     {:canto/thesis
      {:thesis/body ""
       :canto/parentheses
       [{:parens/level 1 :parens/text ""}
        {:parens/level 2 :parens/text ""}
        {:parens/level 3 :parens/text ""}
        {:parens/level 4
         :parens/text ""
         :parens/footnotes
         [{:footnote/idx 1 :footnote/text ""}
          {:footnote/idx 2 :footnote/text ""}
          {:footnote/idx 3 :footnote/text ""}
          {:footnote/idx 4 :footnote/text ""}
          {:footnote/idx 5 :footnote/text ""}]}
        {:parens/level 5 :parens/text ""}]}}}]})

(defsc Footnote [_this {:footnote/keys [idx] :as _props}]
  {:query [:footnote/idx :footnote/text]
   :ident :footnote/idx}
  (dom/div
   {}
   (dom/h1 "This is footnote number: " idx)))

(def ui-footnote (comp/factory Footnote {:keyfn :footnote/idx}))

(defsc Parentheses [_this {:parens/keys [level text footnotes] :as _props}]
  {:query [:parens/level :parens/text {:parens/footnotes (comp/get-query Footnote)}]
   :ident :parens/level
   :initial-state (fn [_] {:parens/level :param/level
                           :parens/text :param/text
                           :parens/footnotes []})}
  (dom/div
   (dom/ol
    (map ui-footnote footnotes))))
(comp/get-initial-state Parentheses {:level 1})
(defsc Thesis [_this {:keys [canto/thesis thesis/body canto/parentheses] :as _props}]
  {:query [:canto/thesis :thesis/body {:canto/parentheses (comp/get-query Parentheses {})}]
   :ident (fn [] [:component/id :canto/thesis])
   :initial-state {:canto/parentheses {}}}
  (dom/div
   (dom/h1 "Canto " thesis)
   (dom/section body)
   (when parentheses (dom/section parentheses))))

(def ui-thesis (comp/factory Thesis {:keyfn :canto/thesis}))

(defsc Canto [_this {:canto/keys [id] :as _props}]
  {:query [:canto/id {:canto/thesis (comp/get-query Thesis)}]
   :ident :canto/id}
  (dom/div
   (ui-thesis {:canto/thesis "A Title"
               :thesis/body "A poem"})))

(def ui-canto (comp/factory Canto))

(defsc NIA [_this _props]
  {:query [:nia/root {:root/poem (comp/get-query Canto)}]
   :ident (fn [] [:component/id :nia/root])
   :initial-state {}
   :route-segment ["nia"]}
  (dom/div
   (dom/h1 "NIA v2")
   (dom/div
    (ui-canto {:canto/id 1}))))

(comment
  ;; footnotes
  (comp/get-query Footnote {:footnote/idx 1 :footnote/text ""})
  (nc/tree->db Footnote {:footnote/idx 1 :footnote/text ""} true)

  ;; parens
  (comp/get-query Parentheses)
  (nc/tree->db Parentheses
               {:parens/level 2
                :parens/text "a mackintosh"
                :parens/footnotes {:footnote/idx 1
                                   :footnote/text "a raincoat in England"}})
  (fdn/db->tree (comp/get-query Parentheses)
                (comp/get-initial-state Parentheses {})
                {})

  ;; thesis
  (comp/get-query Thesis)
  (nc/tree->db Thesis [{:parens/level 2
                        :parens/text "a mackintosh"
                        :parens/footnotes {:footnote/idx 1
                                           :footnote/text "a raincoat in England"}}])
  ;; canto
  (comp/get-query Canto)
  (nc/tree->db Canto {:canto/id 1
                      :canto/thesis [{:parens/level 2
                                      :parens/text "a mackintosh"
                                      :parens/footnotes {:footnote/idx 1
                                                         :footnote/text "a raincoat in England"}}]})

  ;; poem root
  (comp/get-query NIA)
  (nc/tree->db NIA nia-data))