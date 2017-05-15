(ns alumbra.web.common
  (:require [hiccup.page :refer [include-js include-css]]
            [clojure.string :as string]
            [cheshire.core :as json]))

;; ## CDNs

(defn include
  [file]
  (if (string/ends-with? file ".css")
    (include-css file)
    (include-js file)))

(defn include-jsdelivr
  [group file version]
  (include
    (format "//cdn.jsdelivr.net/%s/%s/%s"
            (name group)
            version
            file)))

(defn include-jsdelivr-npm
  [artifact file version]
  (include-jsdelivr :npm file (str (name artifact) "@" version)))

;; ## Artifacts

;; ### React

(defn include-react
  [version]
  (list
    (include-jsdelivr :react "react.min.js" version)
    (include-jsdelivr :react "react-dom.min.js" version)))

;; ### Bootstrap

(defn include-bootstrap
  [version]
  (include-jsdelivr :bootstrap "css/bootstrap.min.css" version))

;; ## Tags

(defn head
  [& contents]
  [:head
   [:meta {:chaarset "UTF-8"}]
   [:meta {:name "viewport", :content "width=device-width, initial-scale=1"}]
   contents])

;; ## Helpers

(defn json
  [value]
  (json/generate-string value {:pretty true}))
