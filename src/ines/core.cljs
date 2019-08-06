
(ns ines.core
  (:require [reagent.core :as reagent :refer [atom]]))

(defonce app-state (reagent/atom {:searchText nil
                                  :showPreview false
                                  :list []
                                  :items [{:name "Milch" :units "Liter"}
                                          {:name "Milchiges" :units "Kg"}
                                          {:name "Brot" :units "Stück"}]}))


(defn text-input [text]
  "on text entered into the text-box, update the state with the actual search value"
  (swap! app-state assoc :searchText text))

;; ***********************
;; * FOR THE PREVIEW BOX *
;; ***********************

(defn show-preview [state]
  "show/hide preview box"
  (swap! app-state assoc :showPreview state))

(defn delete-item [item-name]
"deletes a item from the list"
(when-let [currentList (get @app-state :list)]
  (when (some #(= (:name %) item-name) currentList)
    (swap! app-state assoc :list (remove #(= (:name %) item-name) currentList)))))

(defn add-item [item-name units amount]
  "adds a item to the list. if the item exists, increase the amount by 1 or the specified value in the textbox"
  (when-let [currentList (get @app-state :list)]
    (if-not (some #(= (:name %) item-name) currentList)
      (swap! app-state assoc :list (conj currentList {:name item-name :units units :amount (or amount 1)}))
      (when-let [found-existing-item (first (filter #(= (:name %) item-name) currentList))]
        (let [updated-item (assoc found-existing-item :amount (+ (int (:amount found-existing-item)) (if (= amount nil) 1 (int amount))))]
          (if (= (:amount updated-item) 0)
            (delete-item item-name)
            (swap! app-state assoc :list (map #(if (= (:name %) item-name) updated-item %) (:list @app-state)))))))))

(defn show-preview-results [items]
  "returns the preview list"
  [:div {:class "alert alert-success"}
   [:ul {:class "list-group"}
    (for [item items]
      [:li {:key (:name item) :class "list-group-item list-group-item-action preview-item" :on-click #(add-item (:name item) (:units item) (:amount item))}
       [:span {:class "badge badge-primary badge-pill amount-badge"} (:amount item) " " (:units item)] (:name item)])]])

(defn input-parser [sText]
  "parses the searchValue and returns a vector of found items"
  (let [s clojure.string amount (re-find #"^ *\d+" sText) sText (s.trim (s.replace sText #"^ *\d+" ""))]
    (when-let [foundItems (filter #(s.includes? (s.lower-case (:name %)) (s.lower-case sText)) (get @app-state :items))]
      (map #(assoc % :amount amount) foundItems))))

(defn preview-not-found-component []
  "preview list empty component"
  [:div {:class "alert alert-danger alert-text"} [:img {:src "/img/alert.svg" :class "alert-symbol"}] "nichts gefunden :("])

(defn preview-component []
  "preview list component"
  (let [sText (get @app-state :searchText)]
    (if (and (= true (get @app-state :showPreview) (empty? sText)))
      (show-preview-results (get @app-state :items))
      (when-not (empty? sText)
        (let [preview-results (input-parser sText)]
          (if-not (empty? preview-results)
            (show-preview-results preview-results)
            (preview-not-found-component)))))))

;; *******************************
;; * FOR THE SELECTED ITEMS LIST *
;; *******************************

(defn list-component []
  "list component"
  (when-let [currentList (get @app-state :list)]
    (when-not (empty? currentList)
      [:div {:class "alert alert-info list-label"} "Ihr Einkaufszettel:" [:br] [:br]
       [:ul {:class "list-group"}
        (for [item currentList]
          [:li {:key (:name item) :class "list-group-item list-group-item-action item"}
           [:span {:class "badge badge-primary badge-pill amount-badge"} (:amount item) " " (:units item)]
           (:name item)
           [:img {:src "/img/plus.svg" :class "add-item-button" :on-click #(add-item (:name item) (:units item) 1)}]
           [:img {:src "/img/minus.svg" :class "minus-item-button" :on-click #(add-item (:name item) (:units item) -1)}]
           [:img {:src "/img/trash.svg" :class "delete-item-button" :on-click #(delete-item (:name item))}]])]])))

(defn main-component []
  [:div
   [:h2 {:class "app-title"} "Der (halbwegs) intelligente Einkaufszettel"]
   [:div {:class "input-group mb-3 search"}
    [:input {:type "text" :class "form-control" :placeholder "Was suchen sie?" :aria-label "aria-label-wtf" :aria-describedby "btn-show-all" :on-change #(text-input (-> % .-target .-value))}]
    [:div {:class "input-group-append"}
     (let [show-preview-button-state (get @app-state :showPreview)]
       [:button {:class "btn btn-outline-secondary" :type "button" :id "btn-show-all" :on-click #(show-preview (not show-preview-button-state))} (if (= true show-preview-button-state) "Liste ausblenden" "alle Artikel anzeigen")])]]
   [:div {:class "preview"} (preview-component)]
   [:div {:class "list"} (list-component)]])


(defn start []
  (reagent/render-component [main-component]
                            (. js/document (getElementById "app"))))

(defn ^:export init []
  (start))

(defn stop []
  (js/console.log "stop"))
