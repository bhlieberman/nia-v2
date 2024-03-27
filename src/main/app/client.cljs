(ns app.client
  (:require
   [app.application :refer [SPA]]
   [app.ui.root :as root]
   [app.ui.nia.core :as nia]
   [com.fulcrologic.fulcro.application :as app]
   ;; TODO: load the poem text!
   [com.fulcrologic.fulcro.data-fetch :as df] 
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro-css.css-injection :as cssi] 
   [taoensso.timbre :as log]
   [com.fulcrologic.fulcro.algorithms.merge :as merge]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.fulcro.inspect.inspect-client :as inspect]))

(defn ^:export refresh []
  (log/info "Hot code Remount")
  (cssi/upsert-css "componentcss" {:component root/Root})
  (app/mount! SPA root/Root "app"))

(defn ^:export init []
  (log/info "Application starting.")
  (cssi/upsert-css "componentcss" {:component root/Root})
  ;(inspect/app-started! SPA)
  (app/set-root! SPA root/Root {:initialize-state? true})
  (dr/initialize! SPA)
  (log/info "Starting session machine.") 
  (app/mount! SPA root/Root "app" {:initialize-state? false}))

(comment
  ;; TODO: learn what each of these does
  (inspect/app-started! SPA)
  ;; ok this finds the right thing but there's still a :not-found
  ;; error in the map...
  (df/load! SPA [:footnote/idx 1] nia/Footnote)
  (app/mounted? SPA)
  
  (app/set-root! SPA root/Root {:initialize-state? true})
  
  (reset! (::app/state-atom SPA) {})

  (comp/get-query root/Settings (app/current-state SPA))

  (tap> SPA)
  
  (com.fulcrologic.fulcro.algorithms.indexing/reindex)
  
  (dr/initialize! SPA)
  
  (app/mount! SPA root/Root "app") 

  (-> SPA ::app/runtime-atom deref ::app/indexes)
  (comp/class->any SPA root/Root)
  (let [s (app/current-state SPA)]
    ;; returns the passed in initial state?
    s)
  )
