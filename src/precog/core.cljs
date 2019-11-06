(ns precog.core
  (:require-macros [precog.core])
  (:require
   [precog.parse]
   [precog.state :as state]
   [cljs-bean.core]
   ["preact" :as preact]
   ["preact/hooks" :as hooks]))


(defn fragment [children]
  (apply preact/createElement preact/Fragment #js {} children))

(defn use-js-props [c]
  (set! (.-__precog--use-bean c) true)
  c)

(defn render [el dom-node]
  (while (.hasChildNodes dom-node)
    (.removeChild dom-node (.-firstChild dom-node)))
  (preact/render el dom-node))

(def use-atom state/use-atom)
(def use-focus state/use-focus)

(def use-state hooks/useState)
(def use-reducer hooks/useReducer)
(def use-memo hooks/useMemo)
(def use-callback hooks/useCallback)
(def use-ref hooks/useRef)
(def use-context hooks/useContext)
(def use-effect hooks/useEffect)
(def use-layout-effect hooks/useLayoutEffect)

(def create-context preact/createContext)
(defn context-provider [context]
  (let [provider (.-Provider context)]
    (use-js-props provider)
    provider))
