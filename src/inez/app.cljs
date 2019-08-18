(ns inez.app
  (:require [reagent.core :as reagent])
  (:require [inez.app-state :as app-state])
  (:require [inez.pagination-component :as pagination-component]))

; (defonce app-state (reagent/atom {:searchText nil
;                                   :showPreview false
;                                   :preview-pagination {:current-page 1 :page-size 5 :total-pages 0}
;                                   :list []
;                                   :items item-list/item-list}))
;; **************
;; * PAGINATION *
;; **************

; (defn calculate-max-pages [all-items]
;   "calculates the total-pages for the preview-list"
;   (Math/ceil (/ (count (into [] all-items)) (:page-size (get @app-state/app-state :preview-pagination)))))

; (defn go-to-page [page]
;   "changes the actual preview list page"
;   (swap! app-state/app-state assoc-in [:preview-pagination :current-page] page))

; (defn get-preview-items [all-items]
;   "gets the preview items, respecting the pagination configuration"
;   (let [pagination-config (get @app-state/app-state :preview-pagination)
;         current-page (:current-page pagination-config)
;         page-size (:page-size pagination-config)
;         total-pages (:total-pages pagination-config)]
;     (if (= current-page total-pages)
;       (subvec (into [] all-items) (- (* current-page page-size) page-size))
;       (subvec (into [] all-items) (- (* current-page page-size) page-size) (* current-page page-size)))))

; (defn pagination-component []
;   "pagination component, for navigating between preview-list items"
;   (let [pagination-config (get @app-state/app-state :preview-pagination)
;         current-page (:current-page pagination-config)
;         page-size (:page-size pagination-config)
;         total-pages (:total-pages pagination-config)]
;     (for [page (range 1 (inc total-pages))]
;       [:div {:class "pagination-page-element" :key page}
;        (if (= page current-page)
;          [:div {:class "pagination-page-element-button-active" :on-click #(go-to-page page)} page]
;          [:div {:class "pagination-page-element-button-inactive" :on-click #(go-to-page page)} page])])))

;; ***********************
;; * FOR THE PREVIEW BOX *
;; ***********************

(defn get-preview-items [all-items]
  "gets the preview items, respecting the pagination configuration"
  (let [pagination-config (get @app-state/app-state :preview-pagination)
        current-page (:current-page pagination-config)
        page-size (:page-size pagination-config)
        total-pages (:total-pages pagination-config)]
    (if (= current-page total-pages)
      (subvec (into [] all-items) (- (* current-page page-size) page-size))
      (subvec (into [] all-items) (- (* current-page page-size) page-size) (* current-page page-size)))))

(defn text-input [text]
  "on text entered into the text-box, update the state with the actual search value"
  (pagination-component/go-to-page 1)
  (swap! app-state/app-state assoc :searchText text))

(defn show-preview [state]
  "show/hide preview box"
  (swap! app-state/app-state assoc :showPreview state))

(defn delete-item [item-name]
  "deletes a item from the list"
  (when-let [currentList (get @app-state/app-state :list)]
    (when (some #(= (:name %) item-name) currentList)
      (swap! app-state/app-state assoc :list (remove #(= (:name %) item-name) currentList)))))

(defn add-item [item-name units amount]
  "adds a item to the list. if the item exists, increase the amount by 1 or the specified value in the textbox (passed in as amount)"
  (when-let [currentList (get @app-state/app-state :list)]
    (if-not (some #(= (:name %) item-name) currentList)
      (swap! app-state/app-state assoc :list (conj currentList {:name item-name :units units :amount (or amount 1)}))
      (when-let [found-existing-item (into {} (filter #(= (:name %) item-name) currentList))]
        (let [updated-item (assoc found-existing-item :amount (+ (:amount found-existing-item) (if (= amount nil) 1 amount)))]
          (if (= (:amount updated-item) 0)
            (delete-item item-name)
            (swap! app-state/app-state assoc :list (map #(if (= (:name %) item-name) updated-item %) (:list @app-state/app-state)))))))))

(defn show-preview-results [items]
  "returns the preview list"
  (swap! app-state/app-state assoc-in [:preview-pagination :total-pages] (pagination-component/calculate-max-pages items))
  [:div {:class "alert alert-success"}
   [:div {:class "container"}
    (for [item (get-preview-items items)]
      [:div {:key (:name item) :class "row list-group-item preview-item" :on-click #(add-item (:name item) (:units item) (:amount item))}
       [:div {:class "col-md-4 badge badge-primary badge-pill amount-badge list-badge"} (:amount item) " " (:units item)]
       [:div {:class "col-md" :style {:display "flex"}} (:name item)]])
    [:div {:class "pagination"} (pagination-component/pagination-component)]]])

(defn input-parser [sText]
  "parses the searchValue and returns a vector of found items"
  (let [s clojure.string amount (if (= 0 (int (re-find #"^ *\d+" sText))) 1 (int (re-find #"^ *\d+" sText))) sText (s.trim (s.replace sText #"^ *\d+" ""))]
    (into []
          (flatten
           (for [item-category (get @app-state/app-state :items) :when (some #(s.includes? (s.lower-case %) (s.lower-case sText)) (:name item-category))]
             (map #(merge {:name % :amount (or amount 1) :units (:units item-category)}) (:name item-category)))))))

(defn preview-not-found-component []
  "preview list empty component"
  [:div {:class "alert alert-danger alert-text"} [:img {:src "img/alert.svg" :alt "Alert" :class "alert-symbol"}] "nichts gefunden :("])

(defn preview-component []
  "preview list component"
  (let [sText (get @app-state/app-state :searchText)]
    (if (and (= true (get @app-state/app-state :showPreview) (empty? sText)))
      (show-preview-results (flatten (map #(for [names (:name %)] (merge {:name names :amount 1 :units (:units %)})) (get @app-state/app-state :items))))
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
  (when-let [currentList (get @app-state/app-state :list)]
    (when-not (empty? currentList)
      [:div {:class "container alert alert-info list-label"} "Dein Einkaufszettel:" [:br] [:br]
       (for [item currentList]
         [:div {:key (:name item) :class "row list-group-item"}
          [:div {:class "col-md-2 list-badge badge badge-primary badge-pill amount-badge"} (:amount item) " " (:units item)]
          [:div {:class "col-md-10 list-group-item-name"} (:name item)]
          [:img {:src "img/plus.svg" :alt "+1" :class "list-action-button add-item-button" :on-click #(add-item (:name item) (:units item) 1)}]
          [:img {:src "img/minus.svg" :alt "-1" :class "list-action-button minus-item-button" :on-click #(add-item (:name item) (:units item) -1)}]
          [:img {:src "img/trash.svg" :alt "Löschen" :class "list-action-button delete-item-button" :on-click #(delete-item (:name item))}]])])))

(defn main-component []
  [:div {:class "main"}
   [:h2 {:class "app-title"} "INEZ - Der INtelligente EinkaufsZettel"]
   [:div {:class "input-group mb-3 search"}
    [:input {:type "text" :class "form-control search-input-box" :placeholder "Was suchst du?" :aria-label "aria-label-wtf" :aria-describedby "btn-show-all" :on-change #(text-input (-> % .-target .-value))}]
    [:div {:class "input-group-append"}
     (let [show-preview-button-state (get @app-state/app-state :showPreview)]
       [:button {:class "btn btn-outline-secondary btn-show-all" :type "button" :id "btn-show-all" :on-click #(show-preview (not show-preview-button-state))} (if (= true show-preview-button-state) "Liste ausblenden" "alle Artikel anzeigen")])]
    [:div {:class "info-tooltip-element" :data-toggle "tooltip" :title "Gesuchten Artikel eingeben, zB.:\nSchokolade\noder mit Mengenangabe:\n5 Milch"}
     [:img {:src "img/info.svg" :alt "Quick Help" :class "info-tooltip-icon"}]]]
   [:div {:class "preview"} (preview-component)]
   [:div {:class "list"} (list-component)]])


(defn start []
  (reagent/render-component [main-component]
                            (. js/document (getElementById "app")))
  (.ready (js/$ js/document)
          #(.tooltip (js/$ "[data-toggle=\"tooltip\"]"))))

(defn ^:export init []
  (start))

(defn stop []
  (js/console.log "stop"))
