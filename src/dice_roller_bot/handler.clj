(ns dice-roller-bot.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn json-response [body] 
  {:status 200 :headers {"Content-Type" "application/json"} :body body})

(defn roll-dice [faces]
  (case faces
    "f" (rand-nth [-1 0 1])
    (inc (rand-int (Integer/parseInt faces)))))

(defn dice-response [quantity faces]
  (let [throws (into [] (repeatedly quantity #(roll-dice faces)))] 
    (str "{"
         "\"quantity\":" quantity ","
         "\"faces\":\"" faces "\","
         "\"throws\":\"" throws "\","
         "\"sum\":" (reduce + throws)
         "}")))

(defroutes app-routes
  (GET "/" []
       "Hello World")
  (GET "/dice/:quantity/:faces" [quantity faces]
       (json-response (dice-response (Integer/parseInt quantity) faces)))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
