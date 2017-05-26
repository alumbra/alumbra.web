(ns alumbra.web.graphql-voyager
  "Ring handler for exposing the [GraphQL Voyager](https://github.com/APIs-guru/graphql-voyager)"
  (:require [alumbra.web.common :as common]
            [ring.middleware.resource :refer [wrap-resource]]
            [hiccup.core :as hiccup]
            [hiccup.page :refer [html5 include-js]]))

;; ## Dependencies

(defn- make-head
  [{:keys [title versions]
    :or {title "GraphQL Voyager"}}]
  (let [{:keys [graphql-voyager react]
         :or {graphql-voyager "1.x"
              react           "15.4.2"}}
        versions]
    (common/head
      [:title title]
      (common/include-react react)
      (common/include "voyager.css" )
      (common/include "voyager.min.js"))))

;; ## Configuration

(defn- make-introspection-provider
  [{:keys [config]}]
  (let [{:keys [url]} config
        url-str (if url
                  (common/json url)
                  "window.location.origin + '/graphql'")]
    (str "function (introspectionQuery) {\n"
         "  return fetch(" url-str ", {\n"
         "    method: 'post',\n"
         "    headers: {\n"
         "      'content-type': 'application/json',\n"
         "      'accept': 'application/json',\n"
         "    },\n"
         "    body: JSON.stringify({query: introspectionQuery})"
         "  }).then(function(response) { return response.json(); });"
         "}")))

;; ## Render

(defn- render
  [options]
  (hiccup/html
    (html5
      (make-head options)
      [:body
       [:div#app]
       [:script
        "var introspectionProvider = " (make-introspection-provider options) ";\n"
        "GraphQLVoyager.init(\n"
        "  document.getElementById('app'),\n"
        "  {introspection: introspectionProvider}\n"
        ");"]])))

;; ## Handler

(defn handler
  "Create a Ring Handler exposing the [GraphQL Voyager][voy] web UI.
   Options are:

   - `:title`: content for HTML title tag.
   - `:versions`: versions of CSS/JS to fetch from CDN (jsDelivr) with keys
     `:react` and `:graphql-voyager`.
   - `:config`: a map with the `:url` key, pointing at the GraphQL endpoint to
     inspect.

   You should put this handler at a \"directory\" route since it'll be used
   to provide _all_ CSS/JS/HTML assets. E.g. if it's located at `/voyager`, the
   following assets will be provided:

   - `/voyager/voyager.css`,
   - `/voyager/voyager.min.js`,
   - `/voyager/voyager.worker.js`,
   - `/voyager/*`: the main HTML page.

   [voy]: https://github.com/APIs-guru/graphql-voyager"
  [options]
  (let [page (render options)]
    (-> (fn self
          ([_]
           {:status  200
            :headers {"content-type" "text/html;charset=UTF-8"}
            :body    page})
          ([request callback _]
           (callback (self request))))
        (wrap-resource "alumbra/web/graphql-voyager"))))
