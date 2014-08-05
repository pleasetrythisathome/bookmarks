(ns bookmarks.bookmarks
  (:require-macros [cljs.core.async.macros :as asyncm :refer [go go-loop]])
  (:require [shodan.console :as console :include-macros true]
            [cljs.core.async :as async :refer [<! >! put! chan map<]]
            [bookmarks.settings :as settings]))

(def bookmarks (.-bookmarks js/chrome))

(defn async-callback
  "apply method-name to js obj with args and put result passed into callback onto a channel"
  [obj method-name & args]
  (let [out (chan)
        method (aget obj method-name)]
    (.apply method obj (clj->js (concat args [#(put! out %)])))
    out))

(defn delete [id]
  (.remove bookmarks id))

(defn create [parent title url]
  (.create bookmarks #js {:parentId parent
                          :title title
                          :url url}))

(defn move [id parent]
  (.move bookmarks id #js {:parentId parent}))

(defn get-children [id]
  (map< js->clj (async-callback bookmarks "getChildren" id)))

(comment
  (.move bookmarks "21" "2")
  (delete "21")

  (go
   (console/log (<! (get-folders))))

  (go
   (console/log (<! (async-callback bookmarks "getTree"))))

  (go
   (doseq [folder (<! (async-callback bookmarks "getChildren" "2"))]
     (console/log folder)
     (doseq [bookmark (<! (async-callback bookmarks "getChildren" (.-id folder)))]
       ;;(console/log bookmark)
       ))))
