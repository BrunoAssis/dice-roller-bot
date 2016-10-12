(ns dice-roller-bot.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.util.response :refer [response]]
            [clojure.string :refer [join]]))

(defn roll-dice [faces]
  (case faces
    "f" (rand-nth [-1 0 1])
    (inc (rand-int (Integer/parseInt faces)))))

(defn dice [quantity faces]
  (let [quantity (Integer/parseInt quantity)
        throws (into [] (repeatedly quantity #(roll-dice faces)))] 
    {:quantity quantity
     :faces faces
     :throws throws
     :sum (reduce + throws)}))

(defn generate-dice-message [dice-text]
  (let [captures (re-find #"/(\d*)d(.*?)$" dice-text)
        quantity-text (get captures 1)
        quantity (if (= quantity-text "") "1" quantity-text)
        faces (get captures 2)
        dice (dice quantity faces)
        throws (get dice :throws)
        throws-text (join " + " throws)
        sum (get dice :sum)]
    (str "ROLL " quantity "d" faces ": [" throws-text "] = " sum)))

(defn generate-bot-reply [message]
  {:method "sendMessage"
   :text (generate-dice-message (get-in message ["message" "text"]))
   :chat_id (get-in message ["message" "chat" "id"])
   :reply_to_message_id (get-in message ["message" "message_id"])})

(defroutes app-routes
  (POST "/botWebhook" {message :body}
        (response (generate-bot-reply message)))
  (GET "/dice/:quantity/:faces" [quantity faces]
       (response (dice quantity faces)))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      wrap-json-response
      wrap-json-body))
