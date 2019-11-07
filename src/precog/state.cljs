(ns precog.state
  (:require
   [clojure.data :refer [diff]]
   ["preact/hooks" :as hooks]))

(defn- use-atom-watcher [*atom on-update]
  (hooks/useEffect
   (fn atom-watcher [_]
     (let [k (gensym)]
       (add-watch *atom k
                  (fn atom-watch-hook [_ _ _ new-state]
                    (on-update new-state)))
       (fn [] (remove-watch *atom k))))))

(defn use-focus 
  "focuses into an atom, leveraging use-state to update when the value at the (f @*store) changes"
  [*store f & args]
  (let [[value update-value] (hooks/useState (apply f @*store args))]
    (use-atom-watcher *store (fn [v] 
                               (let [next-v (apply f v args)]
                                 (when-not (= value next-v)
                                   (update-value next-v)))))
    value))

(defn use-atom 
  "provides an atom which updates state when its value changes"
  [default-val]
  (let [[*store _update-store] (hooks/useState (fn [] (atom default-val)))
        [_val update-value]  (hooks/useState @*store)]
    (use-atom-watcher *store update-value)
    *store))
