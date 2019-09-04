(ns dv.main
  (:require
   [cljs-bean.core :refer [bean ->clj ->js]]
   ["preact" :as preact]))

(defn ele [node]
  (cond (string? node) node
        (list? node) (->js (map ele node))

        (vector? node)
        (let [[c & pc] node
              component (if (keyword? c) (name c) c)
              [props childs] (if (map? (first pc))
                               [(->js (first pc)) (rest pc)]
                               [#js {} pc])
              children (map ele childs)]
          (apply preact/h component props children))))

(defn app [props]
  (ele [:div 
        [:div "Hello"] 
        [:div "World?"] 
        (list 
         [:div {:key "one"} "hello"]
         [:div {:key "two"} "world"])]))

(def app-ele (js/document.getElementById "app"))

(defn render []
  (js/console.log ::render)
  (loop []
    (if (.hasChildNodes app-ele)
      (do (.removeChild app-ele (.-firstChild app-ele))
        (recur))
      :done))
  (preact/render (ele [app]) app-ele))

(defn main! []
  (js/console.log ::main!)
  (render))

(defn reload! []
  (js/console.log ::reload!)
  (render))
