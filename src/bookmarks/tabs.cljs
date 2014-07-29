(ns bookmarks.tabs
  (:require-macros [cljs.core.async.macros :as asyncm :refer [go go-loop]])
  (:require [shodan.console :as console :include-macros true]
            [cljs.core.async :as async :refer [<! >! put! chan]]))

(def tabs (.-tabs js/chrome))

(defn get-current-tab
  "gets the currently active tab and returns it on a channel"
  []
  (let [out (chan)]
    (.getCurrent tabs #(put! out %))
    out))

(defn update-tab-url
  "updates the url of a tab. causes the tab to navigate to that url"
  [tab-id url]
  (.update tabs tab-id #js{:url url}))

(defn navigate-current
  "opens a url in the current tab"
  [url]
  (go
   (update-tab-url (.-id (<! (get-current-tab))))))
