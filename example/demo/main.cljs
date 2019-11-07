(ns demo.main
  (:require
   [precog.core :as precog :refer [html use-atom use-focus]]
   [precog.styled :refer [styled css]]))

(def AtomContext (precog/create-context (atom {})))
(def AtomProvider (precog/context-provider AtomContext))

(defn dtdd [{:keys [dt dd]}]
  (html [:<> [:dt dt] [:dd dd]]))

(defn UsesJsProps [props]
  (html [:h2 "wrapped: " (.-title props)]))
(precog/use-js-props UsesJsProps)

(defn has-children [{:keys [title children]}]
  (html [:div
         [:h2 title]
         children]))

(def padded (styled :div #js {:margin "20px 0"}))

(defn !button [p]
  (css #js
   {:border       "1px solid #ccc"
    :borderRadius "5px"
    :marginRight  (get p :ml 0)
    :padding      "3px 5px"}))

(defn clicker []
  (let [*clicks (use-atom 0)
        clicks @*clicks
        incr (precog/use-callback (fn [] (swap! *clicks inc)) [*clicks])
        decr (precog/use-callback (fn [] (swap! *clicks dec)) [*clicks])]
    (html
     [:div {:id "clicker"}
      [padded
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
        [:button {:onClick incr :class (!button {:ml "5px"})} "increment"]
        [:button {:onClick  decr
                  :class    (!button {})
                  :disabled (not (pos? clicks))}
         "decrement"]]]])))

(defn set-input [state e]
  (swap! state assoc :input (.. e -target -value)))

(defn lens-input [{:keys [state]}]
  (let [input (use-focus state get :input "")
        onInput (precog/bind-callback set-input [state])]
    (html
     [:div
      [:label "atom focus input"
       [:input {:type    "text"
                :value   input
                :onInput onInput}]
       " " (count input)]])))

(defn lens-context-input []
  (let [state (precog/use-context AtomContext)
        input (use-focus state get :input "")
        onInput (precog/bind-callback set-input [state])]
    (html [:div
           [:label "atom context input"
            [:input {:type    "text"
                     :value   input
                     :onInput onInput}]]])))

(defn atom-input []
  (let [*input (use-atom "foo")
        onInput (precog/use-callback (fn [e] (reset! *input (.. e -target -value))) [*input])
        context-input (use-focus (precog/use-context AtomContext) get :input)
        length (precog/bind-memo (fn [i j] (+ (count i) (count j))) [(use-focus *input identity) context-input])]
    (html
     [:div
      [:label "atom input"
       [:input {:type    "text"
                :value   @*input
                :onInput onInput}]
       " this: "
       (cond
         (zero? (count @*input)) "empty"
         :else [:strong (count @*input)])]
      [:div "memo w/ shared: " length]])))

(defn app [{:keys [state]}]
  (let [derp (html [:div (for [x (range 3)] [:li x])])
        hello "hello"]
    (js/console.log derp)
    (html
     [AtomProvider {:value state}
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
       [lens-context-input]
       [atom-input]
       [:ul
        [:li -1]
        (for [x (range 10)]
          [:li {:key x} x])
        [:li 10]]
       (let [greet "hello"
             thing "world"]
         (list
          [:div {:key "one"} "greet: " greet]
          [:div {:key "two"} "thing: " thing]))]])))

(def app-ele (js/document.getElementById "app"))

(defonce state (atom {}))

(defn render []
  (js/console.log ::render)
  (precog/render (html [app {:state state}]) app-ele)
  ; (precog/render (precog/div {} "hello") app-ele)
  (js/console.log ::render-done))

(defn main! []
  (js/console.log ::main!)
  (render))

(defn reload! []
  (js/console.log ::reload!)
  (render))
