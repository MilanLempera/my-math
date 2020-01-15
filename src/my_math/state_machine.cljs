(ns my-math.state-machine
  (:require
    [my-math.generator :as generator]
    [my-math.images :refer [get-random-image]]
    [tilakone.core :as tk :refer [_]]))

(defn init-game-state []
  (let [expressions (->> generator/case-seq
                         (take 10)
                         (map #(-> %
                                   (assoc :id (random-uuid))
                                   (assoc :type :expression))))

        results (->> expressions
                     (map #(-> %
                               (select-keys [:result :id])
                               (assoc :type :result))))
        ]
    {
     :items               (shuffle (concat expressions results))
     :selected-expression nil
     :selected-result     nil
     :solved-items        #{}
     :background-image    (get-random-image)
     :stats               {:errors      []
                           :right-count 0
                           :bad-count   0}
     }))


(def common-transitions
  [
   {::tk/on :reset-game, ::tk/to :no-selection ::tk/actions [:reset-game]}
   {::tk/on _}
   ])

(defn with-common-transitions
  ([] (with-common-transitions []))
  ([state-transitions]
   (into [] (concat state-transitions common-transitions))))

(def app-states
  [
   {::tk/name        :no-selection
    ::tk/transitions (with-common-transitions [{::tk/on      :select-expression,
                                                ::tk/to      :expression-selected
                                                ::tk/actions [:set-expression]}
                                               {::tk/on      :select-result,
                                                ::tk/to      :result-selected
                                                ::tk/actions [:set-result]}])}
   {::tk/name        :expression-selected
    ::tk/transitions (with-common-transitions [{::tk/on      :select-expression,
                                                ::tk/to      :expression-selected
                                                ::tk/actions [:set-expression]}
                                               {::tk/on      :select-result,
                                                ::tk/to      :pair-selected
                                                ::tk/actions [:set-result]}
                                               ])}

   {::tk/name        :result-selected
    ::tk/transitions (with-common-transitions [{::tk/on      :select-expression,
                                                ::tk/to      :pair-selected
                                                ::tk/actions [:set-expression]}
                                               {::tk/on      :select-result,
                                                ::tk/to      :result-selected
                                                ::tk/actions [:set-result]}
                                               ])}

   {::tk/name        :pair-selected
    ::tk/enter       {::tk/actions [:check-result]}
    ::tk/transitions (with-common-transitions [{::tk/on      :answer-is-right,
                                                ::tk/to      :right-answer
                                                ::tk/actions [:answer-is-right]}
                                               {::tk/on      :answer-is-bad,
                                                ::tk/to      :bad-answer
                                                ::tk/actions [:answer-is-bad]}
                                               ])}

   {::tk/name        :right-answer
    ::tk/leave       {::tk/actions [:clear-selected]}
    ::tk/transitions (with-common-transitions [{::tk/on      :select-expression,
                                                ::tk/to      :expression-selected
                                                ::tk/actions [:set-expression]}
                                               {::tk/on      :select-result,
                                                ::tk/to      :result-selected
                                                ::tk/actions [:set-result]}])}

   {::tk/name        :bad-answer
    ::tk/leave       {::tk/actions [:clear-selected]}
    ::tk/transitions (with-common-transitions [{::tk/on      :select-expression,
                                                ::tk/to      :expression-selected
                                                ::tk/actions [:set-expression]}
                                               {::tk/on      :select-result,
                                                ::tk/to      :result-selected
                                                ::tk/actions [:set-result]}])}

   {::tk/name        :finished
    ::tk/enter       {::tk/actions [:show-stats]}
    ::tk/transitions common-transitions}
   ])

; FSM has states, a function to execute actions, and current state and value:

(def current-action (atom nil))

(defmulti process-action ::tk/action)

(def app-state-machine
  {::tk/states  app-states
   ::tk/action! (fn [fsm]
                  (let [action-data (:data @current-action)]
                    (process-action fsm action-data)))
   ::tk/state   :no-selection
   :quiz-state  (init-game-state)})

; Lets apply same inputs to our FSM:

(def app-state
  (atom app-state-machine))

(defn process-event [{:keys [type] :as action}]
  (reset! current-action action)
  (reset! app-state (tk/apply-signal @app-state type))
  (reset! current-action nil))

(defmethod process-action :set-expression [fsm action-data]
  (assoc-in fsm [:quiz-state :selected-expression] action-data))

(defmethod process-action :set-result [fsm action-data]
  (assoc-in fsm [:quiz-state :selected-result] action-data))

(defmethod process-action :reset-game [fsm]
  (update fsm :quiz-state init-game-state))

(defmethod process-action :check-result [{:keys [quiz-state] :as fsm}]
  (let [current-pair ((juxt :selected-expression :selected-result) quiz-state)
        results (map :result current-pair)
        event (if (apply = results)
                :answer-is-right
                :answer-is-bad)]
    (js/setTimeout #(process-event {:type event}) 250))
  fsm)

(defmethod process-action :answer-is-right [fsm]
  (let [selected-expression (get-in fsm [:quiz-state :selected-expression])
        selected-result (get-in fsm [:quiz-state :selected-result])]
    (-> fsm
        (update-in [:quiz-state :stats :right-count] inc)
        (update-in [:quiz-state :solved-items] #(conj % selected-expression selected-result))
        (assoc-in [:quiz-state :selected-expression] nil)
        (assoc-in [:quiz-state :selected-result] nil))))

(defmethod process-action :answer-is-bad [fsm]
  (let [error (select-keys (:quiz-state fsm) [:selected-expression :selected-result])]
    (-> fsm
        (update-in [:quiz-state :stats :errors] conj error)
        (update-in [:quiz-state :stats :bad-count] inc)
        (assoc-in [:quiz-state :selected-expression] nil)
        (assoc-in [:quiz-state :selected-result] nil))))

(defmethod process-action :clear-selected [fsm]
  (println (get-in fsm [:quiz-state :stats]))
  (update-in fsm [:quiz-state] dissoc :select-expression :select-result))

(comment

  (process-event {:type :select-expression :data 2})
  (process-event {:type :select-result :data 1})
  (process-event {:type :reset-game})
  (::tk/state @app-state)
  (:quiz-state @app-state)

  app-state-machine

  (assoc-in app-state-machine [:quiz-state :selected-expression] 1)

  )
