(ns inez.preview-not-found-component
  )

(defn preview-not-found-component []
  "preview list empty component"
  [:div {:class "alert alert-danger alert-text"} [:img {:src "img/alert.svg" :alt "Alert" :class "alert-symbol"}] "nichts gefunden :("])