(ns duct-api-sample.boundary.users
  (:require duct.database.sql
            [clojure.java.jdbc :as jdbc]
            [buddy.hashers :as hashers]))

(defprotocol Users
  (create-user [db email password])
  (signin-user [db email password]))

(extend-protocol Users
  duct.database.sql.Boundary

  ; 動作確認方法
  ;  (duct-api-sample.boundary.users/create-user (db) "user1@example.com" "password")
  (create-user [{db :spec} email password]
    (let [pw-hash (hashers/derive password)
          results (jdbc/insert! db :users {:email email
                                           :password_digest pw-hash})]
      (-> results ffirst val)))

  (signin-user [{db :spec} email password]
    (if-let [user (-> (jdbc/query db ["SELECT * FROM users WHERE email=?" email]) first)]
      (if (hashers/check password (:password_digest user))
        (dissoc user :password_digest)))))
