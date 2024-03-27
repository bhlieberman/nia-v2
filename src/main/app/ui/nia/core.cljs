(ns app.ui.nia.core
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.algorithms.denormalize :as fdn]
            [com.fulcrologic.fulcro.algorithms.normalize :as nc]))

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

(defsc Footnote [_this _props]
  {:query [:footnote/idx :footnote/text]
   :ident :footnote/idx})

(defsc Parentheses [_this _props]
  {:query [:parens/level :parens/text {:parens/footnotes (comp/get-query Footnote)}]
   :ident :parens/level
   :initial-state (fn [_params] {:parens/level :param/level
                                :parens/text :param/text
                                :parens/footnotes []})})

(defsc Thesis [_this _props]
  {:query [:canto/thesis :thesis/body {:canto/parentheses (comp/get-query Parentheses {})}]
   :ident [:component/id :canto/thesis]})

(defsc Canto [_this _props]
  {:query [:canto/id {:canto/thesis (comp/get-query Thesis)}]
   :ident :canto/id})

(defsc NIA [_this _props]
  {:query [:nia/root {:root/poem (comp/get-query Canto)}]
   :ident [:component/id :nia/root]})

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
  (nc/tree->db NIA nia-data)
  )