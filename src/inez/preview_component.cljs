(ns inez.preview-component
  (:require [inez.app-state :as app-state])
  (:require [inez.preview-not-found-component :as preview-not-found-component])
  (:require [inez.pagination-component :as pagination-component]))

(defn text-input [text]
  "on text entered into the text-box, update the state with the actual search value"
  (pagination-component/go-to-page 1)
  (swap! app-state/app-state assoc :searchText text))

(defn input-parser [sText]
  "parses the searchValue and returns a vector of found items"
  (let [s clojure.string amount (if (= 0 (int (re-find #"^ *\d+" sText))) 1 (int (re-find #"^ *\d+" sText))) sText (s.trim (s.replace sText #"^ *\d+" ""))]
    (into []
          (flatten
           (for [item-category (get @app-state/app-state :items) :when (some #(s.includes? (s.lower-case %) (s.lower-case sText)) (:name item-category))]
             (map #(merge {:name % :amount (or amount 1) :units (:units item-category)}) (:name item-category)))))))

(defn get-preview-items [all-items]
  "gets the preview items, respecting the pagination configuration"
  (let [pagination-config (get @app-state/app-state :preview-pagination)
        current-page (:current-page pagination-config)
        page-size (:page-size pagination-config)
        total-pages (:total-pages pagination-config)]
    (if (= current-page total-pages)
      (subvec (into [] all-items) (- (* current-page page-size) page-size))
      (subvec (into [] all-items) (- (* current-page page-size) page-size) (* current-page page-size)))))

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

(defn preview-component []
  "preview list component"
  (let [sText (get @app-state/app-state :searchText)]
    (if (and (= true (get @app-state/app-state :showPreview) (empty? sText)))
      (show-preview-results (flatten (map #(for [names (:name %)] (merge {:name names :amount 1 :units (:units %)})) (get @app-state/app-state :items))))
      (when-not (empty? sText)
        (let [preview-results (input-parser sText)]
          (if-not (empty? preview-results)
            (show-preview-results preview-results)
            (preview-not-found-component/preview-not-found-component)))))))