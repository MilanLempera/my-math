(ns my-math.grid
  (:require [devcards.core :as dc :include-macros true]
            [hx.react :as hx :refer [defnc]]
            [stylefy.core :as stylefy :refer [use-style]]))


(def colors {:page-background "#fefdf7"
             :background      "#fef9e9"
             :primary         "#191817"
             :highlight       "#FCEBB6"
             :highlight-light "#fdf5da"
             })

(stylefy/init)

(def body-style
  {:background-color (:page-background colors)})

(stylefy/tag "body" body-style)

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
     :box-shadow            "inset 3px 3px 8px -3px rgba(0,0,0,0.75)"
     :border-radius         "3px"
     }))

(def status-row
  {
   :font-size       "1.5em"
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
   :transition       "background-color 0.3s"
   ::stylefy/mode    {:hover {:background-color (:highlight-light colors)}}
   })

(def selected-grid-item-style
  (merge grid-item-style
         {:background-color (:highlight colors)
          ::stylefy/mode    {:hover {:background-color (:highlight colors)
                                     }}
          }))

(def solved-grid-item-style
  (merge grid-item-style
         {:visibility "hidden"
          :opacity    0
          :transition "visibility 0.2s ease-out, opacity 0.2s ease-out"}))


(def score-style
  {:display :flex
   :width   "150px"})

(def score-badge-style
  {:display :flex
   :justify-content :middle
   :align-items "center"})

(def score-space-style
  {:flex-grow 1})

(def score-image-style
  {:height        "20px"
   :padding-left  "5px"
   :padding-right "5px"})

(def score-number
  {:margin-right "5px"
   :display      :inline-box})

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

(defnc score [{:keys [stats]}]
  (let [{:keys [right-count bad-count]} stats]
    [:div (use-style score-style)
     [:div (use-style score-badge-style)
      [:img (use-style score-image-style {:src "images/right.png" :title "Správně"})]
      [:span (use-style score-number) right-count]]

     [:span (use-style score-space-style)]

     [:div (use-style score-badge-style)
      [:img (use-style score-image-style {:src "images/errors.png" :title "Chybně"})]
      [:span (use-style score-number) bad-count]]]))

(defnc grid [{:keys [background-image items solved-items selected-expression selected-result reset select-item stats]}]
  [:div
   [:div (use-style status-row)
    [:button {:type "button" :on-click reset} "Nová hra"]
    [:div
     (item->string selected-expression)
     " = "
     (item->string selected-result)
     (when (and selected-expression selected-result)
       (if (= (:result selected-expression) (:result selected-result))
         [:img (use-style score-image-style {:src "images/right.png" :title "Správně"})]
         [:img (use-style score-image-style {:src "images/errors.png" :title "Chybně"})]
         ))
     ]
    [score {:stats stats}]]
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
