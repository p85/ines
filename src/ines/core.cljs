
(ns ines.core
  (:require [reagent.core :as reagent :refer [atom]]))

(defonce app-state (reagent/atom {:searchText nil
                                  :liste []
                                  :waren [{:name "Milch" :units "Liter"}
                                          {:name "Milchiges" :units "Kg"}
                                          {:name "Brot" :units "Stück"}]}))

(defn text-input [text]
  (swap! app-state assoc :searchText text))

(defn on-click-preview-list [item-name]
  (when-let [currentList (get @app-state :liste)]
    (when-not (some #(= % {:name item-name}) currentList)
      (swap! app-state assoc :liste (conj currentList {:name item-name}))
      (println (get @app-state :liste)))))

(defn preview-component []
    (when-let [sText (get @app-state :searchText)]
      (when-let [preview-results (filter #(not (= nil (re-find (re-pattern (str "(?i)" sText))  (get % :name)))) (get @app-state :waren))]
        [:ul
         (for [item preview-results]
           [:li {:key (:name item) :class "preview-item" :on-click #(on-click-preview-list (:name item))} (:name item)]
        )])))

;; {:style {:background "red"}}
(defn main-component []
  [:div
   [:h1 "ines"]
   [:h3 "bitte etwas eingeben"]
   [:input {:type "text" :on-change #(text-input (-> % .-target .-value))}]
   [:div {:class "preview"} (preview-component)]])




(defn start []
  (reagent/render-component [main-component]
                            (. js/document (getElementById "app"))))

(defn ^:export init []
  (start))

(defn stop []
  (js/console.log "stop"))
