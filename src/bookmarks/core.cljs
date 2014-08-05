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
            [ankha.core :as ankha]
            [bookmarks.creator :refer [creator-view]]
            [bookmarks.manager :refer [manager-view]]
            [bookmarks.bookmarks :as bmarks]
            [bookmarks.settings :as settings]))

(enable-console-print!)

;; connect to weasel repl
(when-not (repl/alive?)
  (repl/connect "ws://localhost:9001" :verbose true))

(declare app-container
         app-state)

(defn create-app-state [mode]
  (set! app-state (atom {:mode (keyword mode)
                         :labels []
                         :items []})))

(defmulti mode-view :mode)
(defmethod mode-view :creator [data]
  creator-view)
(defmethod mode-view :manager [data]
  creator-view)

(defn create-label [folder]
  (select-keys folder ["id" "title" "dateAdded" "dateGroupModified"]))

(defn create-bookmark [item]
  (-> item
      (select-keys ["title" "dateAdded" "url"])
      (merge {"labels" [(get item "parentId")]
              "ids" [(get item "id")]})))

(defn app-view [{:keys [mode] :as app} owner]
  (reify
    om/IWillMount
    (will-mount [this]
      (go
       (let [labels (mapv create-label (<! (bmarks/get-children (settings/get-root-folder))))]
         (om/update! app :labels labels)
         (doseq [{:strs [id]} labels]
           (let [items (mapv create-bookmark (<! (bmarks/get-children id)))]
             (om/transact! app :items (fn [all]
                                        (concat all items))))))))
    om/IRender
    (render [this]
      (html
       [:div {:class "pure-g"}
        [:div {:class "pure-u-3-5"}
         (om/build (mode-view app) app)]
        #_[:div {:class "pure-u-2-5"}
         (om/build ankha/inspector app)]]))))

(defn render
  "Renders the app to the DOM. idempotent!"
  []
  (om/root ;;omdev/dev-component
   app-view
   app-state
   {:target (. js/document (getElementById "app"))}))

(defn ^:export init
  "initializes the "
  [app-id mode]
  (->> app-id
       gdom/getElement
       (set! app-container))
  (create-app-state mode)
  (render))
