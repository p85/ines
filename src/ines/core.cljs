
(ns ines.core
  (:require [reagent.core :as reagent :refer [atom]]))

(defonce app-state (reagent/atom {:searchText nil
                                  :liste []
                                  :waren [{:name "Milch" :units "Liter"}
                                          {:name "Milchiges" :units "Kg"}
                                          {:name "Brot" :units "Stück"}]}))

(defn text-input [text]
  (swap! app-state assoc :searchText text))

;; add a item
(defn add-item-from-preview-list [item-name]
  (when-let [currentList (get @app-state :liste)]
    (when-not (some #(= % {:name item-name}) currentList)
      (swap! app-state assoc :liste (conj currentList {:name item-name}))
      (println (get @app-state :liste)))))

;; delete a item
(defn delete-item [item-name]
  (when-let [currentList (get @app-state :liste)]
    (when (some #(= % {:name item-name}) currentList)
      (swap! app-state assoc :liste (remove #(= {:name item-name} %) currentList)))))

(defn preview-component []
  (when-let [sText (get @app-state :searchText)]
    (when-not (empty? sText)
      (when-let [preview-results (filter #(not (= nil (re-find (re-pattern (str "(?i)" sText))  (get % :name)))) (get @app-state :waren))]
        [:ul
         (for [item preview-results]
           [:li {:key (:name item) :class "preview-item" :on-click #(add-item-from-preview-list (:name item))} (:name item)]
           )]))))

(defn list-component []
  (when-let [currentList (get @app-state :liste)]
    (when-not (empty? currentList)
      [:div "Ihr Einkaufszettel:"
       [:ul
        (for [item currentList]
          [:li {:key (:name item) :class "item" :on-click #(delete-item (:name item))} (:name item)])]])))

;; {:style {:background "red"}}
(defn main-component []
  [:div
   [:h1 "ines"]
   [:h3 "bitte etwas eingeben"]
   [:input {:type "text" :on-change #(text-input (-> % .-target .-value))}]
   [:div {:class "preview"} (preview-component)]
   [:div {:class "list"} (list-component)]])




(defn start []
  (reagent/render-component [main-component]
                            (. js/document (getElementById "app"))))

(defn ^:export init []
  (start))

(defn stop []
  (js/console.log "stop"))
