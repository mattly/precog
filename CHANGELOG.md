# Changelog

## 0.0.9 - 20191025

Lots of breaking changes here:

- *breaking* move `precog.main` to `precog.core`, split `html` macro into a clj namespace and main cljs functions into theirs; parse/ele go into `precog.parse` cljc namespace.
- fix demo breakage from previous breaking out into functions
- extract atom-watching helper into own function
- *breaking* rename `use-lens` to `use-focus`, since _lens_ implies two-way binding
- *maybe breaking* remove unused cljss library from deps
- add experimental `styled` component helper relying on emotion

## 0.0.8 - 20191018

- *breaking* remove `bind-handler`, it doesn't provide much real wins over inline functions, at much higher complexity.
- `use-lens` now accepts supplimental arguments which will be applied to the lens function alongside the state.

## 0.0.7 - 20191015

- Fix a bug with the previous `bind-handler` change. I should probably start thinking about tests.

## 0.0.6 - 20191015

- moved a small bit of the compiled output back to a more verbose form, it saved virtually nothing in a gzipped bundle and cost a few milliseconds of render time
- *breaking* `bind-handler` now requires an event-flags map as its second argument, and accepts supplimental arguments that will be passed to the handling function. So `(bind-handler #{} myfn 1 2)` will call `(myfn state event 1 2)`

## 0.0.5 - 20191013

- A few small tweaks to the element constructor and preliminary tests show reducing the re-gzip bundle size in `:advanced` compilation mode of the precompiled templates from 50kb to 8kb.

## 0.0.4 - 20191010

- Move HTML file for demo out of `target/` and to `example/` so lein doesn't clobber it on builds
- Basic support for parsing the contents of some expressions:
    - `list`: not sure why you'd need this, but it becomes a fragment
    - `do`, `for`, `let`, `if` (and its ilk), `when` (and its ilk), `case`, `cond` have their expressions parsed
  I havent' ever needed to use threading macros in the process of constructing a view, and feel supporting those
  is not worth the effort at this point.
- New & Imporved `use-atom` that doesn't make a new atom on every change
- Added `bind-handler` whichs takes an atom, an optional set of event-processing flags, and a processing function,
  and swaps the contents of the atom with the processing function, perhaps stopping or extracting the event's target first.

## 0.0.3 - 20191010

- Don't rename props such as `on-input` to `onInput` anymore. Perhaps it could be done for DOM elements and not JS components, but the inconsistency would bug me. We'd have to offer a way to supplant the built-in transforms, and then they couldn't be done at compile-time. I feel this is one of those things better shoved into the face of the library consumer.

## 0.0.2 - 20191008

- Make clojure maps the default props for function components. Use bean to convert props to js for DOM components or other components that have been "annotated" with `precog/use-js-props`

## 0.0.1 - 20191001

- Initial Release
