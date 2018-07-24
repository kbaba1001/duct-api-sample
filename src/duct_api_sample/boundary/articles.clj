(ns duct-api-sample.boundary.articles
  (:require duct.database.sql
            [clojure.java.jdbc :as jdbc]))

(defprotocol Articles
  (create-article [db user-id body])
  (update-article [db id body]))

(extend-protocol Articles
  duct.database.sql.Boundary

  (create-article [{:keys [spec]} user-id body]
    (let [results (jdbc/insert! spec :articles {:user_id user-id
                                                :body body})]
      (-> results ffirst val)))

  (update-article [{:keys [spec]} id body]
    (let [results (jdbc/update! spec :articles {:body body} ["id = ?" id])]
      (= 1 (first results)))))
