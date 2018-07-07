(ns duct-api-sample.handler.users
  (:require [ataraxy.response :as response]
            [struct.core :as st]
            [integrant.core :as ig]
            [buddy.sign.jwt :as jwt]
            [duct-api-sample.boundary.users :as users]))

; TODO emailは重複禁止にしたい。DBにもユニーク制約付けてなかった...
(def create-form-schema
  {:email [st/required st/email]
   :password [st/required st/string [st/min-count 8] [st/max-count 100]]})

; TODO body-paramsでインタフェースを統一したい
; TODO add tests
(defmethod ig/init-key ::create [_ {:keys [db]}]
  (fn [{[_ email password] :ataraxy/result body-params :body-params}]
    (if-let [errors (first (st/validate body-params create-form-schema))]
      [::response/bad-request errors]
      (let [id (users/create-user db email password)]
        [::response/created (str "/users/" id)]))))

(defmethod ig/init-key ::signin [_ {:keys [db jwt-secret]}]
  (fn [{[_ email password] :ataraxy/result}]
    (letfn [(with-token [user]
              (->> (jwt/sign {:email (:email user)} jwt-secret)
                   (assoc user :token)))]
      (if-let [user (users/signin-user db email password)]
        [::response/ok {:user (with-token user)}]
        [::response/forbidden "Not authorized"]))))
