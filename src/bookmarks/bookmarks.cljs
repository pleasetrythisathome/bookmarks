(ns bookmarks.tabs
  (:require-macros [cljs.core.async.macros :as asyncm :refer [go go-loop]])
  (:require [shodan.console :as console :include-macros true]
            [cljs.core.async :as async :refer [<! >! put! chan]]))

(def bookmarks (.-bookmarks js/chrome))

(defn async-method
  "apply method-name to js obj with args and put result passed into callback onto a channel"
  [obj method-name & args]
  (let [out (chan)
        method (aget obj method-name)]
    (.apply method obj (clj->js (concat args [#(put! out %)])))
    out))

(defn remove-bookmark [id]
  (.remove bookmarks id))

(go
 (console/log (<! (async-method bookmarks "getChildren" "2"))))
