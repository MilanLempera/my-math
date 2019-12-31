(ns my-math.grid
  (:require [devcards.core :as dc :include-macros true]
            [hx.react :as hx :refer [defnc]]
            [stylefy.core :as stylefy :refer [use-style]]))

(stylefy/init)

(defn calculate-columns [item-count]
  (cond
    (>= item-count 40) 8
    (>= item-count 28) 7
    (>= item-count 24) 6
    (>= item-count 20) 5
    :else 4)
  )

(defn grid-style [item-count]
  (let [columns-count (calculate-columns item-count)]
    {:display               "grid"
     :grid-template-columns (str "repeat(" columns-count ", 1fr)")
     :grid-gap              "5px"
     :grid-auto-rows        "minmax(100px, auto)"
     :font-size             "18px"
     :background-position   "center"
     :background-repeat     "no-repeat"
     :background-size       "cover"
     ;:background-image      "url('https://live.staticflickr.com/7433/14216021893_a243b314ee.jpg')"
     }))

(def grid-item-style
  {:display          "flex"
   :align-items      "center"
   :justify-content  "center"
   :box-shadow       "3px 3px 8px -3px rgba(0,0,0,0.75)"
   :border-radius    "3px"
   :background-color "#dedede"
   :user-select      "none"
   ::stylefy/mode    {:hover {:background-color "rgb(98, 131, 213)"}}
   })

(def selected-grid-item-style
  (merge grid-item-style
         {:background-color "#f5f262"
          ::stylefy/mode    {:hover {:background-color "#e8e42a"}}
          }))

(def solved-grid-item-style
  (merge grid-item-style
         {:visibility "hidden"}))

(defnc grid-item [{:keys [item is-selected is-solved on-click]}]
  (let [styles (cond
                 is-selected selected-grid-item-style
                 is-solved solved-grid-item-style
                 :else grid-item-style)]
    [:div (use-style styles {:on-click on-click})
     (if-let [operation (:operation item)]
       (str (:operand1 item) " " (:symbol operation) " " (:operand2 item))
       (:result item)
       )
     ]))

(defnc grid [{:keys [items selected-items solved-items select-item]}]
  [:div (use-style (grid-style (count items)))
   (for [item items]

     [grid-item {:is-selected (selected-items item)
                 :is-solved   (solved-items item)
                 :item        item
                 :on-click    #(select-item item)}])])

(def base-props {:items (range 12) :selected-items #{1} :solved-items #{2 3 8 9 11 21 32 34 45}})

(dc/defcard (hx/f [grid (merge base-props {:items (range 12)})]))
(dc/defcard (hx/f [grid (merge base-props {:items (range 20)})]))
(dc/defcard (hx/f [grid (merge base-props {:items (range 24)})]))
(dc/defcard (hx/f [grid (merge base-props {:items (range 28)})]))
(dc/defcard (hx/f [grid (merge base-props {:items (range 35)})]))
(dc/defcard (hx/f [grid (merge base-props {:items (range 48)})]))
