(ns triviados.trivia
  (:require [triviados.http :as http]
            [clojure.string :as str]))

(def api-url "https://opentdb.com/api.php")

(def entities->char
  {"&quot;" "\""
   "&#039;" "'"
   "&amp;" "&"})

(defn map->kws-as-regex [m]
  (re-pattern
   (reduce-kv (fn [s k _]
                (if s
                  (str s "|" k)
                  k))
              nil
              m)))

(defn replace-several [s replacements-map]
  (str/replace s (map->kws-as-regex replacements-map) replacements-map))

(defn fix-entities [s]
  (replace-several s entities->char))

(defn get-api-question-url [amount]
  (str api-url "?difficulty=easy&amount=" amount))

(defn get-questions [amount]
  (let [url (get-api-question-url amount)
        {:keys [results]} (http/http-get url)]
    (map (fn [question]
           (-> question
               (update :question fix-entities)
               (update :correct-answer fix-entities)
               (update :incorrect-answers (fn [incorrect-answers]
                                            (map fix-entities incorrect-answers)))))
         results)))

(comment
  (get-questions 1)
  ;; => [{:category "Vehicles",
  ;;      :type "multiple",
  ;;      :difficulty "easy",
  ;;      :question "The LS1 engine is how many cubic inches?",
  ;;      :correct-answer "346",
  ;;      :incorrect-answers ["350" "355" "360"]}]
  )
