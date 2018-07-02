(ns duct-api-sample.boundary.users-test
  (:require [duct-api-sample.test-utils :as utils :refer [db-spec db]]
            [duct.database.sql :refer :all]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [duct-api-sample.boundary.users :as users]))

(use-fixtures :each utils/db-creanup)

; TODO will write validation
(deftest test-create-user
  (testing "create user"
    (let [user-id (users/create-user db "user1@example.com" "password")
          user (first (jdbc/query db-spec ["SELECT email FROM users WHERE id=?" user-id]))]
      (is (= "user1@example.com" (:email user))))))

(use-fixtures :each
  utils/db-creanup
  (fn [f]
    (users/create-user db "user1@example.com" "password")
    (f)))

(deftest test-signin-user
  (testing "signin user"
    (let [result (users/signin-user db "user1@example.com" "password")]
      (is (= "user1@example.com" (:email result)))))

  (testing "invalid password"
    (let [result (users/signin-user db "user1@example.com" "invalid-password")]
      (is (nil? result))))

  (testing "invalid email"
    (let [result (users/signin-user db "unkown-user@example.com" "password")]
      (is (nil? result)))))
