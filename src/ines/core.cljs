
(ns ines.core
  (:require [reagent.core :as reagent :refer [atom]]))

(defonce app-state (reagent/atom {:searchText nil
                                  :showPreview false
                                  :list []
                                  :items [{:name "Milch" :units "Liter"}
                                          {:name "Milchiges" :units "Kg"}
                                          {:name "Brot" :units "Stück"}]}))


;; on text entered into the text-box, update the state with the actual search value
(defn text-input [text]
  (swap! app-state assoc :searchText text))

;; show/hide preview box
(defn show-preview [state]
  (swap! app-state assoc :showPreview state))

;; add a item
(defn add-item-from-preview-list [item-name]
  (when-let [currentList (get @app-state :list)]
    (when-not (some #(= % {:name item-name}) currentList)
      (swap! app-state assoc :list (conj currentList {:name item-name})))))

;; delete a item
(defn delete-item [item-name]
  (when-let [currentList (get @app-state :list)]
    (when (some #(= % {:name item-name}) currentList)
      (swap! app-state assoc :list (remove #(= {:name item-name} %) currentList)))))

(defn show-preview-results [items]
  [:ul
    (for [item items]
      [:li {:key (:name item) :class "preview-item" :on-click #(add-item-from-preview-list (:name item))} (:name item)])])

;; preview list
(defn preview-component []
  (let [sText (get @app-state :searchText)]
    (if (= true (get @app-state :showPreview))
      (show-preview-results (get @app-state :items))
      (when-not (empty? sText)
        (let [preview-results (filter #(not (= nil (re-find (re-pattern (str "(?i)" sText)) (get % :name)))) (get @app-state :items))]
          (show-preview-results preview-results)))
      )))


;; actual list
(defn list-component []
  (when-let [currentList (get @app-state :list)]
    (when-not (empty? currentList)
      [:div "Ihr Einkaufszettel:"
       [:ul
        (for [item currentList]
          [:li {:key (:name item) :class "item" :on-click #(delete-item (:name item))} (:name item)])]])))

;; {:style {:background "red"}}
(defn main-component []
  [:div
   [:h1 "ines"]
   [:h3 "was willst du kaufen?"]
   [:input {:type "text" :on-change #(text-input (-> % .-target .-value))}]
   (let [show-preview-button-state (get @app-state :showPreview)]
     [:span {:class "show-all-button" :on-click #(show-preview (not show-preview-button-state))}
      (if (= true show-preview-button-state) "Liste ausblenden" "alle Artikel anzeigen")]
   )
   [:div {:class "preview"} (preview-component)]
   [:div {:class "list"} (list-component)]])




(defn start []
  (reagent/render-component [main-component]
                            (. js/document (getElementById "app"))))

(defn ^:export init []
  (start))

(defn stop []
  (js/console.log "stop"))
