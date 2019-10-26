(ns precog.core
  (:require [precog.parse :refer [parse]]))

(defmacro html [form]
  (if-not (vector? form)
    (throw (ex-info "did not receive a vector!" {:form form}))
    (parse form)))
