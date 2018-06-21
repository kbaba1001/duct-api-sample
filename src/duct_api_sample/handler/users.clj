(ns duct-api-sample.handler.users
  (:require duct.database.sql
            [ataraxy.response :as response]
            [integrant.core :as ig]
            [buddy.sign.jwt :as jwt]
            [duct-api-sample.boundary.users :as users]))

(defmethod ig/init-key ::create [_ {:keys [db]}]
  (fn [{[_ email password] :ataraxy/result}]
    (let [id (users/create-user db email password)]
      [::response/created (str "/users/" id)])))

; TODO このメソッドは ::signin の中でletとかで定義したほうが良いのでは？
(defn with-token [user jwt-secret]
  (->> (jwt/sign {:email (:email user)} jwt-secret)
       (assoc user :token)))

(defmethod ig/init-key ::signin [_ {:keys [db jwt-secret]}]
  (fn [{[_ email password] :ataraxy/result}]
    (if-let [user (users/signin-user db email password)]
      [::response/ok {:user (with-token user jwt-secret)}]
      {:status 403 :headers {} :body "Not authorized"})))
