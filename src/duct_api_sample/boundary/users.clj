(ns duct-api-sample.boundary.users
  (:require duct.database.sql
            [clojure.java.jdbc :as jdbc]
            [buddy.hashers :as hashers]))

(defprotocol Users
  (create-user [db email password])
  (find-user-by-email [db email])
  (signin-user [db email password]))

(extend-protocol Users
  duct.database.sql.Boundary

  (create-user [{:keys [spec]} email password]
    (let [pw-hash (hashers/derive password)
          results (jdbc/insert! spec :users {:email email
                                             :password_digest pw-hash})]
      (-> results ffirst val)))

  (find-user-by-email [{:keys [spec]} email]
    (first (jdbc/query spec ["SELECT * FROM users WHERE email=?" email])))

  (signin-user [db email password]
    (if-let [user (find-user-by-email db email)]
      (if (hashers/check password (:password_digest user))
        (dissoc user :password_digest)))))
