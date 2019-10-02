(ns precog.state
  (:require
   ["preact/hooks" :as hooks]))

(defn use-lens [*store f]
  (let [[value update-value] (hooks/useState (f @*store))]
    (hooks/useEffect
     (fn [_]
       (let [k (gensym "useLens")]
         (add-watch *store k
                    (fn update-lens-hook [_ _ _ new-state]
                      (update-value (f new-state))))
         (fn [] (remove-watch *store k)))))
    value))

(defn use-atom [default-val]
  (let [[*value update-value] (hooks/useState (fn [] (atom default-val)))]
    (hooks/useEffect
     (fn [_]
       (let [k (gensym "useAtom")]
         (add-watch *value k
                    (fn update-atom-hook [_ _ _ new-state]
                      (update-value (atom new-state)))))))
    *value))
