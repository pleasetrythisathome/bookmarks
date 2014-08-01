(ns bookmarks.core
  (:require-macros [cljs.core.async.macros :as asyncm :refer [go go-loop]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [goog.dom :as gdom]
            [weasel.repl :as repl]
            [shodan.console :as console :include-macros true]
            [cljs.core.async :as async :refer [<! >! put! chan]]
            [omdev.core :as omdev]
            [bookmarks.creator :refer [creator-view]]
            [bookmarks.manager :refer [manager-view]]))

(enable-console-print!)

;; connect to weasel repl
(when-not (repl/alive?)
  (repl/connect "ws://localhost:9001" :verbose true))

(declare app-container
         app-state)

(defmulti mode-view :mode)
(defmethod mode-view :creator [data]
  creator-view)
(defmethod mode-view :manager [data]
  manager-view)

(defn app-view [{:keys [mode] :as data} owner]
  (reify
    om/IRender
    (render [this]
      (om/build (mode-view data) data))))

(defn render
  "Renders the app to the DOM. idempotent!"
  []
  (omdev/dev-component
   app-view
   app-state
   {:target (. js/document (getElementById "app"))}))

(defn ^:export init
  "initializes the "
  [app-id mode]
  (->> app-id
       gdom/getElement
       (set! app-container))
  (set! app-state (atom {:mode (keyword mode)}))
  (render))
