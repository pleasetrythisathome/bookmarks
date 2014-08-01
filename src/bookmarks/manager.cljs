(ns bookmarks.manager
  (:require-macros [cljs.core.async.macros :as asyncm :refer [go go-loop]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [goog.dom :as gdom]
            [shodan.console :as console :include-macros true]
            [cljs.core.async :as async :refer [<! >! put! chan]]
            [bookmarks.form :refer [form]]))

(def inputs [{:id :email
              :korks :email}
             {:id :first-name
              :korks [:name :first]}
             {:id :last-name
              :korks [:name :last]}])

(defn render-form [{:keys [email first-name last-name] :as inputs}]
  (html
   [:div
    [:div
     email]
    [:div
     first-name]
    [:div
     last-name]]))

(defn manager-view [data owner]
  (reify
    om/IRender
    (render [this]
      (html
       [:div {:id "manager"}
        (om/build form data {:opts {:inputs inputs
                                    :render-fn render-form
                                    :on-submit (fn [vals]
                                                 (console/log vals))}})]))))
