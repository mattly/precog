(ns precog.styled
  (:require-macros [precog.styled])
  (:require 
   [cljs-bean.core :refer [->js bean]]
   ["emotion" :as emotion]))

(defn css [& styles]
  (->> styles
       (map (fn [s] (cond (object? s) s (map? s) (->js (merge (bean) s)))))
       (apply emotion/css)))
