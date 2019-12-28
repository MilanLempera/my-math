(ns my-math.core
  (:require [devcards.core :as dc :include-macros true]
            [hx.react :as hx :refer [defnc]]))

(enable-console-print!)

(println "This text is printed from src/my-math/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defnc title [text]
       [:h1 text])

(dc/defcard {:this "is a map"})
(dc/defcard (hx/f [title "test-title"]))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )


