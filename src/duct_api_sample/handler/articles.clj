(ns duct-api-sample.handler.articles
  (:require [ataraxy.response :as response]
            [integrant.core :as ig]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [struct.core :as st]
            [duct-api-sample.boundary.users :as users]
            [duct-api-sample.boundary.articles :as articles]))

(def form-schema
  {:body [st/required st/string [st/max-count 140]]})

(defn authenticate_user! [request function]
  (if-not (authenticated? request)
    [::response/unauthorized "invalid token"]
    (function)))

; curl -v -X POST -H "Content-Type: application/json" -H "Authorization: Token eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImJvYkBleGFtcGxlLmNvbSJ9.zA2uWGmbweddKXovU3lPgKwskd23Jn1QoS4My6yBVCE" -d '{"body": "hello world"}' http://localhost:3000/articles
; TODO 記事を作成できないケース
(defmethod ig/init-key ::create [_ {:keys [db]}]
  (fn [{{:keys [body] :as params} :body-params
        {:keys [email]} :identity
        :as request}]
    (authenticate_user! request
                        #(if-let [errors (first (st/validate params form-schema))]
                           [::response/bad-request errors]
                           (let [user-id (:id (users/find-user-by-email db email))
                                 article-id (articles/create-article db user-id body)]
                             [::response/created (str "/articles/" article-id)])))))

(defmethod ig/init-key ::update [_ {:keys [db]}]
  (fn [{[_ article-id] :ataraxy/result :as request}]
    (authenticate_user! request
                        #(if-let [errors (first (st/validate (:body-params request) form-schema))]
                           [::response/bad-request errors]
                           (if (articles/update-article db article-id (get-in request [:body-params :body]))
                             [::response/no-content]
                             [::response/not-found])))))

(defmethod ig/init-key ::destroy [_ {:keys [db]}]
  (fn [{[_ article-id] :ataraxy/result :as request}]
    (authenticate_user! request
                        #(if (articles/delete-article db article-id)
                           [::response/no-content]
                           [::response/not-found]))))
