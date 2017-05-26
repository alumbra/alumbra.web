(defproject alumbra/web "0.1.0-SNAPSHOT"
  :description "Ring Handlers for exposing GraphQL Web Tools."
  :url "https://github.com/alumbra/alumbra.web"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"
            :year 2017
            :key "mit"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring/ring-core "1.6.1"]
                 [hiccup "1.0.5"]
                 [cheshire "5.7.1"]]
  :profiles {:codox
             {:plugins [[lein-codox "0.10.3"]]
              :dependencies [[codox-theme-rdash "0.1.2"]]
              :codox {:project {:name "alumbra/web"}
                      :metadata {:doc/format :markdown}
                      :themes [:rdash]
                      :source-uri "https://github.com/alumbra/alumbra.web/blob/v{version}/{filepath}#L{line}"
                      :namespaces [alumbra.web.graphql-voyager
                                   alumbra.web.graphiql-workspace]}}}
  :aliases {"codox" ["with-profile" "+codox" "codox"]}
  :pedantic? :abort)
