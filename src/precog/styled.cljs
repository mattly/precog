(ns precog.styled
  (:require-macros [precog.styled])
  (:require 
   ["emotion" :as emotion]))

(defn css [& styles]
  (apply emotion/css styles))
