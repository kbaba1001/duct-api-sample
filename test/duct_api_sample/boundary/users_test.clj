(ns duct-api-sample.boundary.users-test
  (:require [duct-api-sample.test-utils :as utils :refer [db-spec db]]
            [duct.database.sql :refer :all]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [integrant.core :as ig]
            [integrant.repl.state :refer [config system]]
            [duct-api-sample.boundary.users :as users]
            [fipp.edn :refer (pprint) :rename {pprint fipp}]))

(use-fixtures :each utils/db-creanup)

; TODO will write validation
(deftest test-create-user
  (testing "create user"
    (let [user-id (users/create-user db "user1@example.com" "password")
          user (first (jdbc/query db-spec ["SELECT email FROM users WHERE id=?" user-id]))]
      (is (= "user1@example.com" (:email user))))))

(deftest test-signin-user
  (defn setup-user []
    (users/create-user db "user1@example.com" "password"))

  (testing "signin user"
    (let [_ (setup-user)
          result (users/signin-user db "user1@example.com" "password")]
      (is (= "user1@example.com" (:email result)))))

  (testing "invalid password"
    (let [_ (setup-user)
          result (users/signin-user db "user1@example.com" "invalid-password")]
      (is (nil? result))))

  (testing "invalid email"
    (let [_ (setup-user)
          result (users/signin-user db "unkown-user@example.com" "password")]
      (is (nil? result)))))
