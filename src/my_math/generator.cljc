(ns my-math.generator)

(def operations
  {
   :addition    {
                 :type     :addition
                 :symbol   "+"
                 :function +
                 }
   :subtraction {
                 :type     :subtraction
                 :symbol   "-"
                 :function -
                 }}
  )

(def settings
  {
   :operations   #{:addition :subtraction}
   :numbers-from 0
   :numbers-to   10
   })

(defn get-number []
  (let [from (:numbers-from settings)
        to (:numbers-to settings)
        range (+ (Math/abs from) to 1)]
    (+ (rand-int range) from)))

(defn get-numbers [n]
  (->> get-number
       repeatedly
       (take n)))

(defn check-result-range [{:keys [result]}]
  (let [from (:numbers-from settings)
        to (:numbers-to settings)]
    (<= from result to)))

(defn create-case [op1 op2 operation result]
  {
   :operand1  op1
   :operand2  op2
   :operation operation
   :result    result
   })

(defmulti generate-case :type)
(defmethod generate-case :addition [operation]
  (let [[op1 op2] (get-numbers 2)
        f (:function operation)
        result (f op1 op2)]

    (create-case
      op1
      op2
      operation
      result)
    ))

(defmethod generate-case :subtraction [operation]
  (let [[op2 op1] (sort (get-numbers 2))
        f (:function operation)
        result (f op1 op2)]

    (create-case
      op1
      op2
      operation
      result)))


(def operation-seq (cycle (:operations settings)))

(def case-seq (->> operation-seq
                   (map operations)
                   (map generate-case)
                   (filter check-result-range)
                   ))


(take 5 case-seq)


