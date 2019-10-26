(ns demo.main
  (:require
   ["preact/hooks" :as hooks]
   [precog.core :as precog :refer [html use-atom use-focus]]))

(defn dtdd [{:keys [dt dd]}]
  (html [:<> [:dt dt] [:dd dd]]))

(defn UsesJsProps [props]
  (html [:h2 "wrapped: " (.-title props)]))
(precog/use-js-props UsesJsProps)

(defn has-children [{:keys [title children]}]
  (html [:div
         [:h2 title]
         children]))

(defn clicker []
  (let [*clicks (use-atom 0)
        clicks @*clicks]
    (html
     [:div
      [:div
       "clicked " clicks " times: "
       (if (odd? clicks) "odd" "even")
       ", "
       (cond
         (neg? clicks) [:strong "how do you have negative clicks???"]
         (zero? clicks) [:em "none"]
         (= 1 clicks) [:em "try harder"]
         (= 2 clicks) [:u "that's company"]
         :else [:strong "that's a crowd!"])]
      [:div
       [:button {:onClick (fn [_] (swap! *clicks inc))} "increment"]
       [:button {:onClick (fn [_] (swap! *clicks dec))} "decrement"]]])))

(defn lens-input [{:keys [state]}]
  (let [input (use-focus state get :input "")]
    (html
     [:div
      [:label "atom focus input"
       [:input {:type    "text"
                :value   input
                :onInput (fn [e] (swap! state assoc :input (.. e -target -value)))}]
       " " (count input)]])))

(defn atom-input []
  (let [*input (use-atom "foo")]
    (html
     [:label "atom input"
      [:input {:type    "text"
               :value   @*input
               :onInput (fn [e] (reset! *input (.. e -target -value)))}]
      " "
      (cond
        (zero? (count @*input)) "empty"
        :else [:strong (count @*input)])])))

(defn app [{:keys [state]}]
  (let [hello "hello"]
    (html
     [:div
      [:div hello]
      (when true
        [:strong "World!!"])
      [:<> [:div "one"] [:div "two"]]
      [UsesJsProps {:title "hello wrapped"}]
      [has-children {:title "hello children"}
       [:div "why hello papa"]
       [:div "yes hello"]]
      [:dl
       [dtdd {:dt "term"
              :dd "definition"}]
       [dtdd {:dt "term2"
              :dd "definiton2"}]]
      [clicker]
      [lens-input {:state state}]
      [atom-input]
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
