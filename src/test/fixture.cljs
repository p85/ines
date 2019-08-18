(ns test.fixture)

(defn before-each [app-state]
  ;; populate the state with example items
  (swap! app-state assoc
         :searchText nil
         :showPreview false
         :preview-pagination {:current-page 1 :page-size 2 :total-pages 0}
         :list []
         :items [{:name ["Milch"] :units "Liter"}
                 {:name ["Jogurt" "Danone"] :units "Kg"}
                 {:name ["Brot" "Bread" "Vollkorn"] :units "Stück"}
                 {:name ["Reis" "Rice" "Uncle" "Bens"] :units "Beutel"}
                 {:name ["Schokolade" "Milka" "Alpia"] :units "Stück"}]))
