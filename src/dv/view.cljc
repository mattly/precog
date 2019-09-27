(ns dv.view
  #?(:cljs (:require-macros [dv.view]))
  #?(:cljs (:require 
            [cljs-bean.core :refer [bean ->js ->clj]]
            ["preact" :as preact])))

#?(:cljs
   (defn compile-template [form]
     (cond
       (vector? form)
       (let [[c & pc] form
             component (cond (contains? #{:< :<>} c) preact/Fragment
                             (keyword? c) (name c)
                             :else c)
             [props children] (if (map? (first pc))
                                [(first pc) (rest pc)]
                                [{} pc])]
         (js-obj "constructor" js/undefined
                 "type" component
                 "ref" (or (:ref props) js/undefined)
                 "key" (or (:key props) js/undefined)
                 "props" (-> props
                             (dissoc :key :ref)
                             (assoc :children (to-array (map compile-template children)))
                             (merge (bean))
                             (->js))))
       
       (coll? form)
       (apply array (doall (map compile-template form)))
       
       :else form)))

#?(:cljs
   (defn from-js [props]
     (->clj (bean props))))

#?(:cljs
   (defn render [tmpl dom-node]
     (loop []
       (if (.hasChildNodes dom-node)
         (do (.removeChild dom-node (.-firstChild dom-node))
             (recur))
         :done))
     (preact/render (compile-template tmpl) dom-node)))

#?(:clj
   (defmacro defc [cname binds & forms]
     (let [bind (or (first binds) '_)]
       `(defn ~cname [props#]
          (let [~bind (from-js props#)]
            ~@(butlast forms)
            (compile-template ~(last forms)))))))
