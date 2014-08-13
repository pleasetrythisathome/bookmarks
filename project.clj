(defproject bookmarks "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [;; clojure
                 [org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2311"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]

                 ;; om
                 [om "0.7.1"]
                 [sablono "0.2.21"]
                 [prismatic/om-tools "0.3.2"]
                 [omdev "0.1.3-SNAPSHOT"]

                 ;; dev tools
                 [shodan "0.3.0"]
                 [ankha "0.1.3"]]

  :plugins [[lein-cljsbuild "1.0.3"]]
  :hooks [leiningen.cljsbuild]

  :source-paths ["src"]
  :main bookmarks.core)
PP
