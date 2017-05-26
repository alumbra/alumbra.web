(ns alumbra.web.graphiql-workspace
  "Ring handler for exposing the [GraphiQL Workspace](https://github.com/OlegIlyenko/graphiql-workspace)"
  (:require [alumbra.web.common :as common]
            [hiccup.core :as hiccup]
            [hiccup.page :refer [html5]]))

;; ## Dependencies

(defn- make-head
  [{:keys [title versions]
    :or {title "GraphiQL Workspace"}}]
  (let [{:keys [bootstrap react graphiql graphiql-workspace]
         :or {bootstrap          "3.3.7"
              graphiql           "0.10.2"
              graphiql-workspace "1.0.4"
              react              "15.4.2"}}
        versions
        include-npm #(common/include-jsdelivr-npm
                       :graphiql-workspace
                       %
                       graphiql-workspace)]
    (common/head
      [:title title]
      (common/include-bootstrap bootstrap)
      (common/include-jsdelivr :graphiql "graphiql.css" graphiql)
      (common/include-react react)
      (include-npm "graphiql-workspace.min.css")
      (include-npm "graphiql-workspace.min.js"))))

;; ## Configuration

(defn- make-configuration
  [{:keys [config]}]
  (str "new graphiqlWorkspace.AppConfig('graphiql', "
       (common/json (into {} config))
       ")"))

;; ## Render

(defn- render
  [options]
  (hiccup/html
    (html5
      (make-head options)
      [:body
       [:div#app.graphiql-workspace]
       [:script
        "var config = " (make-configuration options) ";\n"
        "ReactDOM.render(\n"
        "  React.createElement(\n"
        "    graphiqlWorkspace.GraphiQLWorkspace,\n"
        "    {config: config}\n"
        "  ),\n"
        "  document.getElementById('app')\n"
        ");"]])))

;; ## Handler

(defn handler
  "Create a Ring Handler exposing the [GraphiQL Workspace][wrk] web UI.
   Options are:

   - `:title`: content for HTML title tag.
   - `:versions`: versions of CSS/JS to fetch from CDN (jsDelivr) with keys
     `:bootstrap`, `:graphiql`, `:graphiql-workspace` and `:react`.
   - `:config`: map passed to GraphiQL Workspace's `AppConfig`.

   For example, to preconfigure a URL and query, you'd create a handler like:

   ```clojure
   (handler
     {:config
      {:defaultUrl   \"https://some.graphql.endpoint/graphql\"
       :defaultQuery \"{ awesomeness { totalCount } }\"}})
   ```

   [wrk]: https://github.com/OlegIlyenko/graphiql-workspace"
  [options]
  (let [page (render options)]
    (fn self
      ([_]
       {:status  200
        :headers {"content-type" "text/html;charset=UTF-8"}
        :body    page})
      ([request callback _]
       (callback (self request))))))
