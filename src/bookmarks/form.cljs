(ns bookmarks.form
  (:require-macros [cljs.core.async.macros :as asyncm :refer [go go-loop]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string :as string]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :as async :refer [<! >! put! chan]]))

(defn checker [& validators]
  (fn [& args]
    (reduce (fn [errs check]
              (if (apply check args)
                errs
                (conj errs (:msg (meta check)))))
            []
            validators)))

(defn validator [msg fun]
  (with-meta fun {:msg msg}))

(defn get-value [e]
  (.. e -target -value))

(defn filterm [f m]
  (reduce-kv (fn [out k v]
               (if (f v)
                 (assoc out k v)
                 out))
             {}
             m))

(defn make-vector [v]
  (cond-> v
          keyword? vector))

(defn input [data owner {:keys [korks
                                placeholder
                                type
                                icon
                                name
                                input-class
                                label-class
                                label
                                autofocus

                                on-enter
                                on-change
                                on-focus
                                on-blur
                                on-key-up]
                         :or {korks []
                              placeholder ""
                              type "text"
                              icon ""
                              name ""
                              input-class ""
                              label-class ""
                              label ""
                              autofocus false

                              on-change (partial om/update! data korks)
                              on-enter identity}
                         :as opts}]
  (reify
    om/IDisplayName
    (display-name [this]
      "input")
    om/IInitState
    (init-state [this]
      {:disabled false
       :error false})
    om/IRenderState
    (render-state [this {:keys [disabled error checked]}]
      (let [korks (make-vector korks)
            value (get-in data korks)]
        (html
         [:label {:class label-class}
          [:input (merge {:class (cond-> input-class
                                         (not (string/blank? icon)) (str " offset")
                                         error (str " error"))
                          :on-change (comp on-change get-value)
                          :on-key-down (fn [e]
                                         (case (.-keyCode e)
                                           13 (on-enter)
                                           nil))}
                         (filterm identity (select-keys opts [:on-focus
                                                              :on-blur
                                                              :on-key-up]))
                         {:value value
                          :checked checked}
                         (select-keys opts [:type
                                            :name
                                            :autofocus
                                            :read-only
                                            :placeholder]))]
          [:span
           [:span]]
          [:span {:class "label"}
           label]
          (when-not (string/blank? icon)
            [:i {:class (str "absolute left " icon)}])])))))

(defn form [app owner {:keys [inputs
                              render-fn
                              on-submit]}]
  (reify
    om/IInitState
    (init-state [this]
      (reduce (fn [state {:keys [korks]}]
                (assoc-in state (make-vector korks) nil))
              {}
              inputs))
    om/IRenderState
    (render-state [this state]
      (html
       [:form {:class "pure-form"
               :on-submit (fn [e]
                            (.preventDefault e)
                            (on-submit state))}
        (let [cmps (map (fn [{:keys [korks] :as opts}]
                          (om/build input state {:react-key (string/join (map name korks) "/")
                                                 :opts (merge opts
                                                              {:on-change (partial om/set-state! owner korks)})}))
                        inputs)]
          (render-fn (zipmap (mapv :id inputs) cmps)))]))))
