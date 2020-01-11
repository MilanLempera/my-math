(ns my-math.grid
  (:require [devcards.core :as dc :include-macros true]
            [hx.react :as hx :refer [defnc]]
            [stylefy.core :as stylefy :refer [use-style]]))

(stylefy/init)

(def colors {
             :background "#FFF9EC"
             :primary    "#5E412F"
             :highlight  "#FCEBB6"
             })

(defn calculate-columns [item-count]
  (cond
    (>= item-count 40) [8 100]
    (>= item-count 28) [7 115]
    (>= item-count 24) [6 133]
    (>= item-count 20) [5 140]
    :else [4 150])
  )

(defn grid-style [background-image item-count]
  (let [[column-count row-height] (calculate-columns item-count)]
    {:display               "grid"
     :max-width             "1200px"
     :padding               "5px"
     :grid-template-columns (str "repeat(" column-count ", 1fr)")
     :grid-gap              "5px"
     :grid-auto-rows        (str "minmax(" row-height "px, auto)")
     :font-size             "20px"
     :background-position   "center"
     :background-repeat     "no-repeat"
     :background-size       "cover"
     :background-image      (str "url('" background-image "')")
     }))

(def status-row
  {
   :color           (:primary colors)
   :display         "flex"
   :justify-content "space-between"
   :margin          "10px 0"
   })

(def grid-item-style
  {:display          "flex"
   :align-items      "center"
   :justify-content  "center"
   :box-shadow       "3px 3px 8px -3px rgba(0,0,0,0.75)"
   :border-radius    "3px"
   :background-color (:background colors)
   :user-select      "none"
   :visibility       "visible"
   :opacity          1
   ::stylefy/mode    {:hover {:background-color (:highlight colors)
                              :transition       "background-color 0.2s ease-in-out"}}
   })

(def selected-grid-item-style
  (merge grid-item-style
         {:background-color  (:highlight colors)
          :transition       "background-color 0.2s ease-in-out"
          ::stylefy/mode    {:hover {:background-color (:highlight colors)
                                     :transition       "background-color 0.2s ease-in-out"
                                     }}
          }))

(def solved-grid-item-style
  (merge grid-item-style
         {:visibility "hidden"
          :opacity    0
          :transition "visibility 0.2s ease-out, opacity 0.2s ease-out"}))

(defn item->string [item]
  (when (nil? item)
    "")

  (if-let [operation (:operation item)]
    (str (:operand1 item) " " (:symbol operation) " " (:operand2 item))
    (:result item)
    )
  )

(defnc grid-item [{:keys [item is-selected is-solved on-click]}]
  (let [styles (cond
                 is-solved solved-grid-item-style
                 is-selected selected-grid-item-style
                 :else grid-item-style)]
    [:div (use-style styles {:on-click on-click})
     (item->string item)
     ]))

(defn is-selected? [selected-expression selected-result item]
  (case (:type item)
    :expression (= selected-expression item)
    :result (= selected-result item)))

(defnc grid [{:keys [background-image items solved-items selected-expression selected-result reset select-item]}]
  [:div
   [:div (use-style status-row)
    [:button {:type "button" :on-click reset} "Nová hra"]
    [:div
     (item->string selected-expression)
     " = "
     (item->string selected-result)
     (when (and selected-expression selected-result)
       (if (= (:result selected-expression) (:result selected-result))
         "RIGHT"
         "BAD"
         ))
     ]
    [:div "dvojice: 10, chyby: 0"]]
   [:div (use-style (grid-style background-image (count items)))
    (for [item items]
      [grid-item {:is-selected (is-selected? selected-expression selected-result item)
                  :is-solved   (some? (solved-items item))
                  :item        item
                  :on-click    #(select-item item)}])]
   ])

(def base-props {:items               (map #(hash-map :type :result :result %) (range 12))
                 :selected-expression nil
                 :selected-result     {:type :result :result 4}
                 :solved-items        #{{:type :result :result 1}
                                        {:type :result :result 5}
                                        {:type :result :result 7}
                                        {:type :result :result 6}}})

(dc/defcard (hx/f [grid base-props]))
(dc/defcard (hx/f [grid (merge base-props {:items (map #(hash-map :type :result :result %) (range 20))})]))
(dc/defcard (hx/f [grid (merge base-props {:items (map #(hash-map :type :result :result %) (range 24))})]))
(dc/defcard (hx/f [grid (merge base-props {:items (map #(hash-map :type :result :result %) (range 28))})]))
(dc/defcard (hx/f [grid (merge base-props {:items (map #(hash-map :type :result :result %) (range 35))})]))
(dc/defcard (hx/f [grid (merge base-props {:items (map #(hash-map :type :result :result %) (range 48))})]))
