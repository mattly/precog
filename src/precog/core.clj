(ns precog.core
  (:require 
   [cljs.tagged-literals :refer [read-js]]))

(defn ele [el ref key props]
  (read-js {:constructor `js/undefined
            :type        el
            :ref         ref
            :key         key
            :props       (cond
                           (string? el) (read-js props)
                           :else `(if (.-__precog--use-bean ~el)
                                    ~(read-js props)
                                    ~props))}))

(defn parse [form]
  (cond
    (vector? form)
    (let [[cmp & prpchl] form
          el             (cond (keyword? cmp) (name cmp)
                               :else cmp)
          props?         (map? (first prpchl))
          props          (if props? (first prpchl) {})
          children       (mapv parse
                               (if props? (rest prpchl) prpchl))]
      (if (contains? #{"<" "<>"} el)
        `(fragment ~children)
        (ele el
             (:ref props)
             (:key props)
             (-> props
                 (dissoc :ref :key)
                 (assoc :children (read-js children))))))

    (list? form)
    (case (first form)
      list
      (parse (into ["<>"] (map parse (rest form))))
      
      for
      (list `cljs.core/clj->js
            (concat (butlast form)
                    (list (parse (last form)))))

      (do let when when-not when-let when-first when-some)
      (concat (butlast form)
              (list (parse (last form))))

      (if if-not if-let if-some)
      (concat (take 2 form)
              (map parse (take-last 2 form)))

      case
      (concat (take 2 form)
              (mapcat (fn [[clause expr]] [clause (parse expr)])
                      (->> form (drop 2) (butlast) (partition 2)))
              (list (parse (last form))))

      cond
      (conj (mapcat (fn [[clause expr]] [clause (parse expr)])
                    (->> form rest (partition 2)))
            (first form))

      form)

    :else
    form))

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
