(ns inez.app
  (:require [reagent.core :as reagent])
  (:require [inez.app-state :as app-state])
  (:require [inez.pagination-component :as pagination-component])
  (:require [inez.preview-not-found-component :as preview-not-found-component])
  (:require [inez.preview-component :as preview-component])
  (:require [inez.list-component :as list-component])
  (:require [inez.main-component :as main-component]))

(defn start []
  (reagent/render-component [main-component/main-component]
                            (. js/document (getElementById "app")))
  (.ready (js/$ js/document)
          #(.tooltip (js/$ "[data-toggle=\"tooltip\"]"))))

(defn ^:export init []
  (start))

(defn stop []
  (js/console.log "stop"))
