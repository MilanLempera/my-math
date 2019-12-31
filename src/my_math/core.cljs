(ns my-math.core
  (:require [my-math.grid :as grid]
            [my-math.generator :as generator]
            [react-dom :as react-dom]
            [hx.react :as hx]))

(enable-console-print!)

(println "This text is printed from src/my-math/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(def cases (->> generator/case-seq
                (take 12)
                (map #(assoc % :id (random-uuid)))
                ))

(def results (map #(select-keys %1 [:result :id]) cases))

(defonce app-state
         (atom {
                :items          (shuffle (concat cases results))
                :selected-items (set [(second cases)])
                :solved-items   (set [(first cases) (first results)])
                }))

(defn select-item [item]
  (swap! app-state assoc :selected-items #{item}))

(def handlers {:select-item select-item})

(defn render []
  (react-dom/render
    ;; hx/f transforms Hiccup into a React element.
    ;; We only have to use it when we want to use hiccup outside of `defnc` / `defcomponent`
    (hx/f [grid/grid (merge @app-state
                            handlers)])
    (. js/document getElementById "app")))

(add-watch app-state :watcher
           render)

(render)

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )


