(ns precog.core
  (:require-macros [precog.core])
  (:require
   [precog.parse]
   [precog.state :as state]
   [cljs-bean.core]
   ["preact" :as preact]))


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

(def use-state preact/useState)
(def use-reducer preact/useReducer)
(def use-memo preact/useMemo)
(def use-callback preact/useCallback)
(def use-ref preact/useRef)
(def use-context preact/useContext)
(def use-effect preact/useEffect)
(def use-layout-effect preact/useLayoutEffect)
