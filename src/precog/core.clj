(ns precog.core
  (:require 
   [precog.parse :refer [parse]]
   [cljs.tagged-literals :refer [read-js]]))

(defmacro html [form]
  (if-not (vector? form)
    (throw (ex-info "did not receive a vector!" {:form form}))
    (parse form)))

(defmacro use-memo [f deps]
  `(useMemo ~f ~(read-js deps)))

(defmacro bind-memo [f deps]
  `(useMemo (partial ~f ~@deps) ~(read-js deps)))

(defmacro bind-callback [f deps]
  `(useCallback (partial ~f ~@deps) ~(read-js deps)))

(defmacro use-callback [f deps]
  `(useCallback ~f ~(read-js deps)))
