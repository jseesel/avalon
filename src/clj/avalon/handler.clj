(ns avalon.handler
  (:require [compojure.core :refer [GET defroutes context]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.params :refer [wrap-params]]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [include-js include-css]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.reload :refer [wrap-reload]]
            [environ.core :refer [env]]
            [avalon.api.group]))

(def mount-target
  [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]])

(def loading-page
  (html
   [:html
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1"}]
     (include-css (if (env :dev) "css/site.css" "css/site.min.css"))]
    [:body
     mount-target
     (include-js "js/app.js")]]))


(defroutes routes
  (GET "/" [] loading-page)
  (GET "/about" [] loading-page)

  (context "/api" []
           avalon.api.group/routes)
  
  (resources "/")
  (not-found "Not Found"))

(def app
  (let [handler (wrap-params #'routes)]
    (if (env :dev) (-> handler wrap-exceptions wrap-reload) handler)))