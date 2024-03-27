(ns app.ui.root
  (:require
   [app.ui.nia.canto :refer [Canto]]
   [app.ui.nia.core :refer [NIA]]
   [com.fulcrologic.fulcro.dom :as dom :refer [div]] 
   [com.fulcrologic.fulcro.dom.events :as evt]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr] 
   [com.fulcrologic.fulcro-css.css :as css] 
   [taoensso.timbre :as log]))

(declare Session)

(dr/defrouter TopRouter [_this _props]
  {:router-targets [Canto NIA]})

(def ui-top-router (comp/factory TopRouter))

(defsc Session
  "Session representation. Used primarily for server queries. On-screen representation happens in Login component."
  [_this {:keys [:session/valid? :account/name] :as _props}]
  {:query         [:session/valid? :account/name]
   :ident         (fn [] [:component/id :session])
   :pre-merge     (fn [{:keys [data-tree]}]
                    (merge {:session/valid? false :account/name ""}
                           data-tree))
   :initial-state {:session/valid? false :account/name ""}})

(def ui-session (comp/factory Session))

(defsc TopChrome [this {:root/keys [router]}]
  {:query         [{:root/router (comp/get-query TopRouter)}
                   {:root/current-session (comp/get-query Session)}
                   {:root/canto (comp/get-query Canto)}
                   {:nia/root (comp/get-query NIA)}]
   :ident         (fn [] [:component/id :top-chrome])
   :initial-state (fn [_params]
                    {:root/router          {}
                     :root/login           {}
                     :root/current-session {}
                     :nia/root {}
                     :root/canto {:parens/highlighted-count 1
                                  :parens/highlighted-color :blue}})}
  (let [current-tab (some-> (dr/current-route this this) first keyword)]
    (div :.ui.container
         (div :.ui.secondary.pointing.menu 
              (dom/a :.item {:classes [(when (= :poem current-tab) "active")]
                             :onClick (fn [] (dr/change-route this ["poem"]))} "Poem")
              (dom/a :.item {:classes [(when (= :nia current-tab) "active")]
                             :onClick (fn [] (dr/change-route this ["nia"]))} "NIA"))
         (div :.ui.grid
              (div :.ui.row
                   (ui-top-router router))))))

(def ui-top-chrome (comp/factory TopChrome))

(defsc Root [this {:root/keys [top-chrome]}]
  {:query         [{:root/top-chrome (comp/get-query TopChrome)}]
   :initial-state {:root/top-chrome {}}}
  (ui-top-chrome top-chrome))
