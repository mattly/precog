(ns demo.main
  (:require
   ["preact/hooks" :as hooks]
   [precog.main :as precog :refer [html use-atom use-lens]]))

(defn dtdd [{:keys [dt dd]}]
  (html [:<> [:dt dt] [:dd dd]]))

(defn UsesJsProps [props]
  (html [:h2 "wrapped: " (.-title props)]))

(precog/use-js-props UsesJsProps)

(defn app [{:keys [state]}]
  (let [*input (use-atom "fee")
        c (use-lens state #(get % :count 0))
        input (use-lens state #(get % :input ""))
        hello "hello"]
    (html
     [:div
      [:div hello]
      (when true
        [:strong "World!!"])
      [:<> [:div "one"] [:div "two"]]
      [UsesJsProps {:title "hello wrapped"}]
      [:dl
       [dtdd {:dt "term"
              :dd "definition"}]
       [dtdd {:dt "term2"
              :dd "definiton2"}]]
      [:div
       [:div
        "clicked " c " times: "
        (if (odd? c) "odd" "even")
        ", "
        (case c
          0 [:em "none"]
          1 [:em "try harder"]
          2 [:u "that's company"]
          [:strong "that's a crowd!"])]
       [:div
        [:button {:onClick (fn [_] (swap! state update :count inc))} "increment"]
        [:button {:onClick (fn [_] (swap! state update :count dec))} "decrement"]]]
      [:div
       [:label "lens input"
        [:input {:type    "text"
                 :value   input
                 :onInput (fn [e] (swap! state assoc :input (.. e -target -value)))}]]]
      [:label "atom input"
       [:input {:type "text"
                :value @*input
                :onInput (fn [e] (reset! *input (.. e -target -value)))}]
       " "
       (cond
         (zero? (count @*input)) "empty"
         :default [:strong "some"])]
      [:ul
       (for [x (range 10)]
         [:li x])]
      (let [greet "hello"
            thing "world"]
        (list
         [:div {:key "one"} greet]
         [:div {:key "two"} thing]))])))

(def app-ele (js/document.getElementById "app"))

(defonce state (atom {}))

(defn render []
  (js/console.log ::render)
  (precog/render (html [app {:state state}]) app-ele)
  (js/console.log ::render-done))

(defn main! []
  (js/console.log ::main!)
  (render))

(defn reload! []
  (js/console.log ::reload!)
  (render))
