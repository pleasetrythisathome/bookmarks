(ns bookmarks.creator
  (:require-macros [cljs.core.async.macros :as asyncm :refer [go go-loop]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [goog.dom :as gdom]
            [shodan.console :as console :include-macros true]
            [cljs.core.async :as async :refer [<! >! put! chan]]))

(defn creator-view [data owner]
  (reify
    om/IRender
    (render [this]
      (html
       [:div {:id "creator"}
        "This is the creator view"]))))
