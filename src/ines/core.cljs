
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

;; ***********************
;; * FOR THE PREVIEW BOX *
;; ***********************

;; show/hide preview box
(defn show-preview [state]
  (swap! app-state assoc :showPreview state))

;; add a item
(defn add-item [item-name units amount]
  (when-let [currentList (get @app-state :list)]
    (if-not (some #(= (:name %) item-name) currentList)
      (swap! app-state assoc :list (conj currentList {:name item-name :units units :amount (or amount 1)}))
      (when-let [found-existing-item (first (filter #(= (:name %) item-name) currentList))]
        (let [updated-item (assoc found-existing-item :amount (+ (int (:amount found-existing-item)) (if (= amount nil) 1 (int amount))))]
          (swap! app-state assoc :list (map #(if (= (:name %) item-name) updated-item %) (:list @app-state))))))))

;; delete a item
(defn delete-item [item-name]
  (when-let [currentList (get @app-state :list)]
    (when (some #(= (:name %) item-name) currentList)
      (swap! app-state assoc :list (remove #(= (:name %) item-name) currentList)))))

;; get combined name of item
(defn get-combined-name [item-name units amount]
  (if amount
    (str amount " " units " " item-name)
    (str item-name)))

;; shows the preview list
(defn show-preview-results [items]
  [:ul
   (for [item items]
     [:li {:key (:name item) :class "preview-item" :on-click #(add-item (:name item) (:units item) (:amount item))}
      (get-combined-name (:name item) (:units item) (:amount item))])])

;; parse input
(defn input-parser [sText]
  (let [s clojure.string amount (re-find #"^ *\d+" sText) sText (s.trim (s.replace sText #"^ *\d+" ""))]
    (when-let [foundItems (filter #(s.includes? (s.lower-case (:name %)) (s.lower-case sText)) (get @app-state :items))]
      (map #(assoc % :amount amount) foundItems)
    )
  ))

;; preview list component
(defn preview-component []
  (let [sText (get @app-state :searchText)]
    (if (and (= true (get @app-state :showPreview) (empty? sText)))
      (show-preview-results (get @app-state :items))
      (when-not (empty? sText)
        (let [preview-results (input-parser sText)]
          (show-preview-results preview-results))))))


;; *******************************
;; * FOR THE SELECTED ITEMS LIST *
;; *******************************

;; actual list
(defn list-component []
  (when-let [currentList (get @app-state :list)]
    (when-not (empty? currentList)
      [:div "Ihr Einkaufszettel:"
       [:ul
        (for [item currentList]
          [:li {:key (:name item) :class "item" :on-click #(delete-item (:name item))}
           (get-combined-name (:name item) (:units item) (:amount item))])]])))

;; {:style {:background "red"}}
(defn main-component []
  [:div
   [:h2 {:class "app-title"} "Der (halbwegs) intelligente Einkaufszettel"]
   [:div {:class "input-group mb-3 search"}
    [:input {:type "text" :class "form-control" :placeholder "Was suchen sie?" :aria-label "aria-label-wtf" :aria-describedby "btn-show-all" :on-change #(text-input (-> % .-target .-value))}]
    [:div {:class "input-group-append"}
     (let [show-preview-button-state (get @app-state :showPreview)]
       [:button {:class "btn btn-outline-secondary" :type "button" :id "btn-show-all" :on-click #(show-preview (not show-preview-button-state))} (if (= true show-preview-button-state) "Liste ausblenden" "alle Artikel anzeigen")]
     )
    ]
   ]
   ;; [:input {:type "text" :on-change #(text-input (-> % .-target .-value))}]
   [:div {:class "preview"} (preview-component)]
   [:div {:class "list"} (list-component)]])


(defn start []
  (reagent/render-component [main-component]
                            (. js/document (getElementById "app"))))

(defn ^:export init []
  (start))

(defn stop []
  (js/console.log "stop"))
