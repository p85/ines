(ns ines.core
  (:require [reagent.core :as reagent :refer [atom]]))

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (reagent/atom {:searchText nil
                                  :showPreview false
                                  :waren [{:name "Milch" :prop 123}
                                          {:name "Milchiges" :prop 1}
                                          {:name "Brot" :prop 2}]}))

(defn text-input [text]
  (swap! app-state assoc :searchText text))

(defn preview-component []
  (when-not (empty? (get @app-state :searchText))
    (when-let [sText (get @app-state :searchText)]
      (when-let [preview-results (filter #(not (= nil (re-find (re-pattern (str "(?i)" sText))  (get % :name)))) (get @app-state :waren))]
        preview-results))))

(defn main-component []
  [:div
   [:h1 "ines"]
   [:h3 "bitte etwas eingeben"]
   [:input {:type "text" :on-change #(text-input (-> % .-target .-value))}]
   [:div (preview-component)]])




(defn start []
  (reagent/render-component [main-component]
                            (. js/document (getElementById "app"))))

(defn ^:export init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (start))

(defn stop []
  ;; stop is called before any code is reloaded
  ;; this is controlled by :before-load in the config
  (js/console.log "stop"))
