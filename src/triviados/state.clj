(ns triviados.state
  (:require [triviados.trivia :as trivia]))

(def !state (atom {:question-counter 0
                   :questions []}))

(defn get-state []
  @!state)

(defn fetch-more-questions! []
  (let [questions (trivia/get-questions 100)]
    (swap! !state update :questions into questions)))

(defn get-last-question! []
  (if (-> @!state :questions count pos?)
    ;; if there are questions, get last
    (let [last-question (-> @!state :questions last)]
      (swap! !state (fn [state]
                      (-> state
                          (update :questions pop)
                          (update :question-counter inc)
                          (assoc :current-question last-question))))
      last-question)
    ;; otherwise, fetch more questions first
    (do
      (fetch-more-questions!)
      (get-last-question!))))
