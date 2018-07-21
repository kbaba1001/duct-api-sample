(ns duct-api-sample.boundary.users-test
  (:require [duct-api-sample.test-utils :as utils :refer [db-spec db]]
            [duct.database.sql :refer :all]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [duct-api-sample.boundary.users :as users]))

(deftest test-user
  (do
    ; TODO will write spec
    (testing "create user"
      (let [user-id (users/create-user db "user1@example.com" "password")
            user (first (jdbc/query db-spec ["SELECT email FROM users WHERE id=?" user-id]))]
        (is (= "user1@example.com" (:email user)))))

    (testing "find user by email"
      (let [result (users/find-user-by-email db "user1@example.com")]
        (is (= "user1@example.com" (:email result)))
        (is (int? (:id result)))))

    (testing "signin user"
      (let [result (users/signin-user db "user1@example.com" "password")]
        (is (= "user1@example.com" (:email result)))))

    (testing "invalid password"
      (let [result (users/signin-user db "user1@example.com" "invalid-password")]
        (is (nil? result))))

    (testing "invalid email"
      (let [result (users/signin-user db "unkown-user@example.com" "password")]
        (is (nil? result))))

    (utils/drop-table :users)))
