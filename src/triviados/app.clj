(ns triviados.app
  (:require [seesaw.core :as ss]
            [seesaw.font :as ssf]
            [triviados.state :as state]))

(declare display-next-question!)
(defonce !current-frame (atom nil))

(defn display [f content]
  (ss/config! f :content content))

(defn next-question-component []
  (let [button (ss/button :text "Next question")]
    (ss/listen button :action (fn [e]
                                (display-next-question! @!current-frame)))
    button))

(defn navigation-component []
  (ss/grid-panel :columns 3
                 :items [(str "  Question number: " (:question-counter (state/get-state)))
                         (str "Category: " (get-in (state/get-state)
                                                   [:current-question :category]))
                         (next-question-component)]))

(defn question-component [question]
  (ss/text :text question
           :multi-line? true
           :wrap-lines? true
           :editable? false
           :margin 40
           :font (ssf/font :style #{:bold :italic}
                           :size 24)))

(defn prepend-letters-to-labels [labels]
  (doseq [[label letter] (map list labels ["A" "B" "C" "D" "E" "F"])]
    (ss/config! label :text (str letter ") " (ss/value label))))
  labels)

(defn get-answer-labels [correct-answer incorrect-answers]
  (let [label-spacing 10
        incorrect-answer-labels (map (fn [answer]
                                       (ss/text :text (str "𝘅 " answer)
                                                :editable? false
                                                :margin label-spacing
                                                :multi-line? true
                                                :wrap-lines? true
                                                :foreground "#c0392b"
                                                :font (ssf/font :size 18)))
                                     incorrect-answers)
        correct-answer-label (ss/text :text (str "✔ " correct-answer)
                                      :foreground "#16a085"
                                      :editable? false
                                      :margin label-spacing
                                      :multi-line? true
                                      :wrap-lines? true
                                      :font (ssf/font :style #{:bold}
                                                      :size 18))]
    (-> incorrect-answer-labels
        (conj correct-answer-label)
        shuffle
        prepend-letters-to-labels)))

(defn answers-component [correct-answer incorrect-answers]
  (ss/grid-panel :columns 1
                 :items (get-answer-labels correct-answer incorrect-answers)))

(defn display-next-question! [f]
  (let [{:keys [question correct-answer incorrect-answers]} (state/get-last-question!)]
    (display f (ss/border-panel
                :north (navigation-component)
                :center (question-component question)
                :south (answers-component correct-answer incorrect-answers)))))

(defn run-ui []
  (ss/native!)

  (let [f (reset! !current-frame (ss/frame :title "Triviados"
                                           :content "Loading..."
                                           :width 800
                                           :height 500))]
    (-> f ss/show!)
    (display-next-question! f)
    nil))

(comment
  (run-ui))
