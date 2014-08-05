(ns bookmarks.settings
  (:require-macros [cljs.core.async.macros :as asyncm :refer [go go-loop]])
  (:require [shodan.console :as console :include-macros true]
            [cljs.core.async :as async :refer [<! >! put! chan]]))


(defn get-root-folder []
  "2")
