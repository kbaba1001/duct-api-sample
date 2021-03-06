(ns duct-api-sample.handler.users
  (:require [ataraxy.response :as response]
            [struct.core :as st]
            [integrant.core :as ig]
            [buddy.sign.jwt :as jwt]
            [duct-api-sample.boundary.users :as users]))

(def unique-email
  {:message "already used"
   :optional true
   :validate (fn [v db]
               (nil? (users/find-user-by-email db v)))})

(defn- create-form-schema [db]
  {:email [st/required st/email [unique-email db]]
   :password [st/required st/string [st/min-count 8] [st/max-count 100]]})

; TODO 気をつけないと params で Mass Assignment になりそうなのが微妙
(defmethod ig/init-key ::create [_ {:keys [db]}]
  (fn [{{:keys [email password] :as params} :body-params}]
    (if-let [errors (first (st/validate params (create-form-schema db)))]
      [::response/bad-request errors]
      (let [id (users/create-user db email password)]
        [::response/created (str "/users/" id)]))))


(defmethod ig/init-key ::signin [_ {:keys [db jwt-secret]}]
  (fn [{{:keys [email password]} :body-params}]
    (letfn [(with-token [user]
              (->> (jwt/sign {:email (:email user)} jwt-secret)
                   (assoc user :token)))]
      (if-let [user (users/signin-user db email password)]
        [::response/ok {:user (with-token user)}]
        [::response/forbidden "Not authorized"]))))
