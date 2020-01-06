(ns my-math.core
  (:require [my-math.grid :as grid]
            [my-math.state-machine :refer [app-state process-event]]
            [react-dom :as react-dom]
            [hx.react :as hx]
            [tilakone.core :as tk :refer [_]]))

(enable-console-print!)

(defn select-item [item]
  (case (:type item)
    :expression (process-event {:type :select-expression :data item})
    :result (process-event {:type :select-result :data item})))

(defn reset []
  (process-event {:type :reset-game}))

(def handlers {:select-item select-item
               :reset       reset})

(defn render []
  (let [quiz-state (:quiz-state @app-state)]
    (println (::tk/state @app-state))
    (println (dissoc quiz-state :items))
    (react-dom/render
      ;; hx/f transforms Hiccup into a React element.
      ;; We only have to use it when we want to use hiccup outside of `defnc` / `defcomponent`
      (hx/f [grid/grid (merge (:quiz-state @app-state)
                              handlers)])
      (. js/document getElementById "app"))))

(add-watch app-state :watcher
           render)

(render)

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
