(ns precog.state
  (:require
   [cljs-bean.core :as bean :refer [->clj bean]]
   ["preact/hooks" :as hooks]))

(defn use-lens [*store f & args]
  (let [[value update-value] (hooks/useState (apply f @*store args))]
    (hooks/useEffect
     (fn [_]
       (let [k (gensym "useLens")]
         (add-watch *store k
                    (fn update-lens-hook [_ _ _ new-state]
                      (update-value (apply f new-state args))))
         (fn [] (remove-watch *store k)))))
    value))

(defn use-atom [default-val]
  (let [[*store update-store] (hooks/useState (fn [] (atom default-val)))
        [value update-value]  (hooks/useState @*store)]
    (hooks/useEffect
     (fn [_]
       (let [k (gensym "useAtom")]
         (add-watch *store k
                    (fn update-atom-hook [_ _ _ new-state]
                      (update-value new-state)))
         (fn [] (remove-watch *store k)))))
    *store))
