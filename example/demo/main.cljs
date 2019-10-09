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
      [:div "World!!"]
      [:<> [:div "one"] [:div "two"]]
      [UsesJsProps {:title "hello wrapped"}]
      [:dl
       [dtdd {:dt "term"
              :dd "definition"}]
       [dtdd {:dt "term2"
              :dd "definiton2"}]]
      [:div
       [:div
        "clicked " c " times"]
       [:div
        [:button {:on-click (fn [_] (swap! state update :count inc))} "increment"]
        [:button {:on-click (fn [_] (swap! state update :count dec))} "decrement"]]]
      [:div
       [:label "lens input"
        [:input {:type    "text"
                 :value   input
                 :on-input (fn [e] (swap! state assoc :input (.. e -target -value)))}]]]
      [:label "atom input"
       [:input {:type "text"
                :value @*input
                :onInput (fn [e] (reset! *input (.. e -target -value)))}]]
      [:ul
       (for [x (range 10)]
         (html [:li x]))]
      [:<>
       [:div {:key "one"} "hello"]
       [:div {:key "two"} "world"]]])))

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
