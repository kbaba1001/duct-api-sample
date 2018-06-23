(ns duct-api-sample.boundary.users-test
  (:require duct.database.sql
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [integrant.core :as ig]
            [integrant.repl.state :refer [config system]]
            [duct-api-sample.boundary.users :as users]
            [fipp.edn :refer (pprint) :rename {pprint fipp}]))

(defn db []
  (-> system (ig/find-derived-1 :duct.database/sql) val))

(defn db-spec []
  (:spec (db)))

(defn clean-up-users [test-fn]
  (test-fn)
  (jdbc/delete! (db-spec) :users []))

(use-fixtures :each clean-up-users)

; ----------------------

(deftest test-create-user
  (testing "create user"
    (let [user-id (users/create-user (db) "user1@example.com" "password")
          user (first (jdbc/query (db-spec) ["SELECT email FROM users WHERE id=?" user-id]))]
      (is (= "user1@example.com" (:email user))))))

(deftest test-signin-user
  (testing "signin user"
    (let [_ (users/create-user (db) "user1@example.com" "password")
          result (users/signin-user (db) "user1@example.com" "password")]
      (is (= "user1@example.com" (:email result))))))
