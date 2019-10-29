(ns precog.styled)

(defmacro styled [el style]
  `(let [style# (cond
                  (and (map? ~style)
                       (some (comp fn? val) ~style))
                  (fn [p#]
                    (css (into {} 
                               (map (fn [[k# forv#]]
                                      [k# (if (fn? forv#) (forv# p#) forv#)]) 
                                    ~style))))
                  
                  (or (map? ~style) (cljs.core/object? ~style))
                  (constantly (css ~style))

                  (fn? ~style)
                  (comp css ~style))]
       (fn [props#]
         (cljs.core/js-obj
          "constructor" js/undefined
          "type" ~(name el)
          "ref" (when-let [r# (:target-ref props#)] (name r#))
          "key" js/undefined
          "props" (cljs-bean.core/->js
                   (merge (cljs-bean.core/bean)
                          props#
                          {:class (style# props#)}))))))
