(ns dv.view
  #?(:cljs (:require-macros [dv.view]))
  #?(:cljs
     (:require
      [cljs.reader :as reader]
      [cljs-bean.core :refer [bean ->js ->clj]]
      ["preact" :as preact])))

(defn ^:dynamic *ele* [el ref key props]
  `(cljs.core/js-obj "constructor" js/undefined
                     "type" (if (contains? #{"<" "<>"} ~el) Fragment ~el)
                     "ref" (or ~ref js/undefined)
                     "key" (or ~key js/undefined)
                     "props" (cljs-bean.core/->js (merge (cljs-bean.core/bean) ~props))))

(defn parse [form]
  (cond (vector? form)
        (let [[cmp & prpchl] form
              el             (cond (keyword? cmp) (name cmp)
                                   :else cmp)
              props?         (map? (first prpchl))
              props'         (if props? (first prpchl) {})
              ref            (:ref props')
              key            (:key props')
              children       (mapv (fn [c] (if (vector? c) (parse c) c))
                                   (if props? (rest prpchl) prpchl))
              props          (-> props' (dissoc :ref :key) (assoc :children children))]
          (*ele* el ref key props))
    :else form))

#?(:cljs (defn Fragment [props] (preact/Fragment props)))

#?(:clj
   (defmacro html [form]
     (if-not (vector? form)
       (throw (ex-info "did not receive a vector!" {:form form}))
       (parse form)))) 

#?(:cljs
   (defn render [el dom-node]
     (loop []
       (if (.hasChildNodes dom-node)
         (do (.removeChild dom-node (.-firstChild dom-node))
           (recur))
         :done))
     (preact/render el dom-node)))
