(ns triviados.trivia
  (:require [triviados.http :as http]))

(def api-url "https://opentdb.com/api.php")

(defn get-api-question-url [amount]
  (str api-url "?amount=" amount))

(defn get-questions [amount]
  (let [url (get-api-question-url amount)
        {:keys [results]} (http/http-get url)]
    results))

(comment
  (get-questions 1)
  ;; => [{:category "Vehicles",
  ;;      :type "multiple",
  ;;      :difficulty "easy",
  ;;      :question "The LS1 engine is how many cubic inches?",
  ;;      :correct-answer "346",
  ;;      :incorrect-answers ["350" "355" "360"]}]
  )
