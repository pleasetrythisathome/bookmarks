(ns bookmarks.core
  (:require [bookmarks.server :refer [start-server]]))

(defn -main [& args]
  (start-server))
