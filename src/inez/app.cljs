
(ns inez.app
  (:require [reagent.core :as reagent :refer [atom]])
  (:require [inez.item-list :as item-list]))

(defonce app-state (reagent/atom {:searchText nil
                                  :showPreview false
                                  :preview-pagination {:current-page 1 :page-size 2 :total-pages 0}
                                  :list []
                                  :items item-list/item-list}))
;; **************
;; * PAGINATION *
;; **************

(defn calculate-max-pages [all-items]
  "calculates the total-pages for the preview-list"
  (Math/ceil (/ (count (into [] all-items)) (:page-size (get @app-state :preview-pagination)))))

(defn go-to-page [page]
  "changes the actual preview list page"
  (swap! app-state assoc-in [:preview-pagination :current-page] page))

(defn get-preview-items [all-items]
  "gets the preview items, respecting the pagination configuration"
  (let [pagination-config (get @app-state :preview-pagination)
        current-page (:current-page pagination-config)
        page-size (:page-size pagination-config)
        total-pages (:total-pages pagination-config)]
    (if (= current-page total-pages)
      (subvec (into [] all-items) (- (* current-page page-size) page-size))
      (subvec (into [] all-items) (- (* current-page page-size) page-size) (* current-page page-size)))))

(defn pagination-component []
  "pagination component, for navigating between preview-list items"
  (let [pagination-config (get @app-state :preview-pagination)
        current-page (:current-page pagination-config)
        page-size (:page-size pagination-config)
        total-pages (:total-pages pagination-config)]
    (for [page (range 1 (inc total-pages))]
      [:div {:class "pagination-page-element" :key page}
       (if (= page current-page)
         [:div {:class "pagination-page-element-button-active" :on-click #(go-to-page page)} page]
         [:div {:class "pagination-page-element-button-inactive" :on-click #(go-to-page page)} page])])))

;; ***********************
;; * FOR THE PREVIEW BOX *
;; ***********************

(defn text-input [text]
  "on text entered into the text-box, update the state with the actual search value"
  (go-to-page 1)
  (swap! app-state assoc :searchText text))

(defn show-preview [state]
  "show/hide preview box"
  (swap! app-state assoc :showPreview state))

(defn delete-item [item-name]
  "deletes a item from the list"
  (when-let [currentList (get @app-state :list)]
    (when (some #(= (:name %) item-name) currentList)
      (swap! app-state assoc :list (remove #(= (:name %) item-name) currentList)))))

(defn add-item [item-name units amount]
  "adds a item to the list. if the item exists, increase the amount by 1 or the specified value in the textbox (passed in as amount)"
  (when-let [currentList (get @app-state :list)]
    (if-not (some #(= (:name %) item-name) currentList)
      (swap! app-state assoc :list (conj currentList {:name item-name :units units :amount (or amount 1)}))
      (when-let [found-existing-item (into {} (filter #(= (:name %) item-name) currentList))]
        (let [updated-item (assoc found-existing-item :amount (+ (:amount found-existing-item) (if (= amount nil) 1 amount)))]
          (if (= (:amount updated-item) 0)
            (delete-item item-name)
            (swap! app-state assoc :list (map #(if (= (:name %) item-name) updated-item %) (:list @app-state)))))))))

(defn show-preview-results [items]
  "returns the preview list"
  (swap! app-state assoc-in [:preview-pagination :total-pages] (calculate-max-pages items))
  [:div {:class "alert alert-success"}
   [:ul {:class "list-group"}
    (for [item (get-preview-items items)]
      [:li {:key (:name item) :class "list-group-item list-group-item-action preview-item" :on-click #(add-item (:name item) (:units item) (:amount item))}
       [:span {:class "badge badge-primary badge-pill amount-badge"} (:amount item) " " (:units item)] (:name item)])
    [:div {:class "pagination"} (pagination-component)]]])

(defn input-parser [sText]
  "parses the searchValue and returns a vector of found items"
  (let [s clojure.string amount (if (= 0 (int (re-find #"^ *\d+" sText))) 1 (int (re-find #"^ *\d+" sText))) sText (s.trim (s.replace sText #"^ *\d+" "")) result []]
    (when-let [allItems (get @app-state :items)]
      (into []
            (remove #(nil? %)
                    (flatten
                     (for [i allItems]
                       (for [ii (:name i)]
                         (when (s.includes? (s.lower-case ii) (s.lower-case sText))
                           (for [n (:name i)]
                             (conj result {:name n :amount (or amount 1) :units (:units i)})))))))))))

(defn preview-not-found-component []
  "preview list empty component"
  [:div {:class "alert alert-danger alert-text"} [:img {:src "img/alert.svg" :class "alert-symbol"}] "nichts gefunden :("])

(defn preview-component []
  "preview list component"
  (let [sText (get @app-state :searchText)]
    (if (and (= true (get @app-state :showPreview) (empty? sText)))
      (show-preview-results (flatten (map #(for [names (:name %)] (merge {:name names :amount 1 :units (:units %)})) (get @app-state :items))))
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
           [:img {:src "img/plus.svg" :class "add-item-button" :on-click #(add-item (:name item) (:units item) 1)}]
           [:img {:src "img/minus.svg" :class "minus-item-button" :on-click #(add-item (:name item) (:units item) -1)}]
           [:img {:src "img/trash.svg" :class "delete-item-button" :on-click #(delete-item (:name item))}]])]])))

(defn main-component []
  [:div {:class "main"}
   [:h2 {:class "app-title"} "INEZ - Der INtelligente EinkaufsZettel"]
   [:div {:class "input-group mb-3 search"}
    [:input {:type "text" :class "form-control search-input-box" :placeholder "Was suchen sie?" :aria-label "aria-label-wtf" :aria-describedby "btn-show-all" :on-change #(text-input (-> % .-target .-value))}]
    [:div {:class "input-group-append"}
     (let [show-preview-button-state (get @app-state :showPreview)]
       [:button {:class "btn btn-outline-secondary btn-show-all" :type "button" :id "btn-show-all" :on-click #(show-preview (not show-preview-button-state))} (if (= true show-preview-button-state) "Liste ausblenden" "alle Artikel anzeigen")])]
    [:div {:class "info-tooltip-element" :title "Gesuchten Artikel eingeben, zB.:\nSchokolade\noder mit Mengenangabe:\n5 Milch"}
     [:img {:src "img/info.svg" :class "info-tooltip-icon"}]]]
   [:div {:class "preview"} (preview-component)]
   [:div {:class "list"} (list-component)]])


(defn start []
  (reagent/render-component [main-component]
                            (. js/document (getElementById "app"))))

(defn ^:export init []
  (start))

(defn stop []
  (js/console.log "stop"))
