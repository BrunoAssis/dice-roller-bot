(ns dice-roller-bot.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]))

(defn roll-dice [faces]
  (case faces
    "f" (rand-nth [-1 0 1])
    (inc (rand-int (Integer/parseInt faces)))))

(defn dice-response [quantity faces]
  (let [throws (into [] (repeatedly quantity #(roll-dice faces)))] 
    (response {:quantity quantity
               :faces faces
               :throws throws
               :sum (reduce + throws)})))

(defroutes app-routes
  (GET "/dice/:quantity/:faces" [quantity faces]
       (dice-response (Integer/parseInt quantity) faces))
  (route/not-found "Not Found"))

(def app
  (wrap-json-response app-routes))
