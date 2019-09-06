(ns dv.main
  (:require
   ["preact/hooks" :as hooks]
   [dv.view :as view :refer-macros [defc]]))

(defn use-lens [*a f]
  (let [[value update-value] (hooks/useState (f @*a))]
    (hooks/useEffect
     (fn [_]
       (let [k (gensym "useLens")]
         (add-watch *a k
                    (fn update-lens-hook [_ _ _ new-state]
                      (update-value (f new-state))))
         (fn [] (remove-watch *a k)))))
    value))


(defc dtdd [{:keys [dt dd]}]
  [:<> [:dt dt] [:dd dd]])

(defc app [{:keys [state]}]
  (let [c (use-lens state #(get % :count 0))
        input (use-lens state #(get % :input ""))]
    (js/console.log input)
    [:div
     [:div "Hello"]
     [:div "World!!"]
     [:<> [:div "one"] [:div "two"]]
     [:dl
      [dtdd {:dt "term" :dd "definition"}]
      [dtdd {:dt "term2" :dd "definiton2"}]]
     [:div
      [:div
       "clicked " c " times"]
      [:div
       [:button {:onClick (fn [_] (swap! state update :count inc))} "increment"]
       [:button {:onClick (fn [_] (swap! state update :count dec))} "decrement"]]]
     [:input {:type "text" :value input :onInput (fn [e] (swap! state assoc :input (.. e -target -value)))}]
     [:ul
      (for [x (range 10)]
        [:li x])]
     [:<>
      [:div {:key "one"} "hello"]
      [:div {:key "two"} "world"]]]))

(def app-ele (js/document.getElementById "app"))

(defonce state (atom {}))

(defn render []
  (js/console.log ::render)
  (view/render [app {:state state}] app-ele)
  (js/console.log ::render-done))

(defn main! []
  (js/console.log ::main!)
  (render))

(defn reload! []
  (js/console.log ::reload!)
  (render))
