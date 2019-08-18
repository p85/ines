(ns inez.main-component
  (:require [inez.list-component :as list-component])
  (:require [inez.preview-component :as preview-component])
  (:require [inez.app-state :as app-state]))

(defn main-component []
  [:div {:class "main"}
   [:h2 {:class "app-title"} "INEZ - Der INtelligente EinkaufsZettel"]
   [:div {:class "input-group mb-3 search"}
    [:input {:type "text" :class "form-control search-input-box" :placeholder "Was suchst du?" :aria-label "aria-label-wtf" :aria-describedby "btn-show-all" :on-change #(preview-component/text-input (-> % .-target .-value))}]
    [:div {:class "input-group-append"}
     (let [show-preview-button-state (get @app-state/app-state :showPreview)]
       [:button {:class "btn btn-outline-secondary btn-show-all" :type "button" :id "btn-show-all" :on-click #(preview-component/show-preview (not show-preview-button-state))} (if (= true show-preview-button-state) "Liste ausblenden" "alle Artikel anzeigen")])]
    [:div {:class "info-tooltip-element" :data-toggle "tooltip" :title "Gesuchten Artikel eingeben, zB.:\nSchokolade\noder mit Mengenangabe:\n5 Milch"}
     [:img {:src "img/info.svg" :alt "Quick Help" :class "info-tooltip-icon"}]]]
   [:div {:class "preview"} (preview-component/preview-component)]
   [:div {:class "list"} (list-component/list-component)]])