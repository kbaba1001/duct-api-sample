(ns duct-api-sample.handler.articles
  (:require [ataraxy.response :as response]
            [integrant.core :as ig]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [duct-api-sample.boundary.users :as users]
            [duct-api-sample.boundary.articles :as articles]))

; curl -v -X POST -H "Content-Type: application/json" -H "Authorization: Token eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImJvYkBleGFtcGxlLmNvbSJ9.zA2uWGmbweddKXovU3lPgKwskd23Jn1QoS4My6yBVCE" -d '{"body": "hello world"}' http://localhost:3000/articles
; TODO 記事を作成できないケース
(defmethod ig/init-key ::create [_ {:keys [db]}]
  (fn [request]
    (if-not (authenticated? request)
      (throw-unauthorized)
      (let [user-email (-> request :identity :email)
            user-id (:id (users/find-user-by-email db user-email))
            body (-> request :body-params :body)
            article-id (articles/create-article db user-id body)]
        [::response/created (str "/articles/" article-id)]))))
