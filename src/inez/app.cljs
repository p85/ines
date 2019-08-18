(ns inez.app
  (:require [reagent.core :as reagent])
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
