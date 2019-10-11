# CLJS/Preact Base

This is an experiment with using CLJS and [Preact](https://preactjs.com) together. Most of the browser view engines for clojurescript rely on [React](https://reactjs.org), which is fine and dandy, but has become quite large, and is owned by Facebook, a company whose work I would rather not use.

Precog is *extremely* opinionated about:

- You as the clojurescript app creator should only be creating function components, not class components
- Rerender triggers should be managed by Preact Hooks
- Your clojurescript components should behave like clojurescript as much as possible
- Interop with JS components should be easy and lightweight.

This is still very much a work in progress. If you're interested in collaborating, drop me a line and I'll work on getting some documentation for a development environment up and going.

## Example

``` clojure
(ns demo.main
  (:require
   [precog.main :as precog :refer [html use-atom]]))

(defn my-form [props]
  (let [input (use-atom "")]
    (html [:form
           [:input {:onInput (fn [e] (reset! input (.. e -target -value)))
                    :value @input
                    :type :text}]])))

(defn render []
  (precog/render (html [my-form])
                 (js/document.getElementById "app"))
```

## Hiccup Precompilation

Precog has its own (super-basic right now) hiccup interpreter, which is precompiled at the clojurescript compilation stage. There is currently a minimal transformation of property key names from kebab-case to camelCase.

## Hooks

in addition to requiring `preact/hooks` yourself and using them, precog also provides `use-atom` and `use-lens` hooks for a more clojure-like experience. The `use-atom` hook is hacky and might go away.
