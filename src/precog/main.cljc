(ns precog.main
  #?(:cljs (:require-macros [precog.main]))
  (:require
   [clojure.set :as set]
   #?@(:cljs
       [[precog.state :as state]
        [cljs.reader :as reader]
        [cljs-bean.core :refer [bean ->js ->clj]]
        ["preact" :as preact]])))

#?(:cljs (def Fragment preact/Fragment))

#?(:cljs
   (defn props->js [props-map]
     (cljs-bean.core/->js (merge (cljs-bean.core/bean) props-map))))

(defn ele [el ref key props]
  `(cljs.core/js-obj "constructor" js/undefined
                     "type" (if (contains? #{"<" "<>"} ~el) Fragment ~el)
                     "ref" (or ~ref js/undefined)
                     "key" (or ~key js/undefined)
                     "props" (if (or (string? ~el) (.-__precog--use-bean ~el))
                               (props->js ~props)
                               ~props)))

#?(:cljs
   (defn use-js-props [c]
     (set! (.-__precog--use-bean c) true)
     c))

(defn parse [form]
  (cond (vector? form)
        (let [[cmp & prpchl] form
              el             (cond (keyword? cmp) (name cmp)
                                   :else cmp)
              props?         (map? (first prpchl))
              props          (if props? (first prpchl) {})
              children       (mapv (fn [c] (if (vector? c) (parse c) c))
                                   (if props? (rest prpchl) prpchl))]
          (ele el 
               (:ref props)
               (:key props)
               (-> props
                   (dissoc :ref :key)
                   (assoc :children children))))
    :else form))

#?(:clj
   (defmacro html [form]
     (if-not (vector? form)
       (throw (ex-info "did not receive a vector!" {:form form}))
       (parse form)))) 

#?(:cljs
   (do
     (defn render [el dom-node]
       (loop []
         (if (.hasChildNodes dom-node)
           (do (.removeChild dom-node (.-firstChild dom-node))
               (recur))
           :done))
       (preact/render el dom-node))

     (def use-atom state/use-atom)
     (def use-lens state/use-lens)))
