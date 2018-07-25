(ns duct-api-sample.handler.users-test
  (:require [duct-api-sample.test-utils :as utils :refer [db-spec db]]
            [duct.database.sql :refer :all]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [integrant.core :as ig]
            duct-api-sample.handler.users))

(use-fixtures :each utils/db-creanup)

(deftest test-handler-users
  ; TODO
  ; * boundaryのテストがあるのでdbをスタブしても良い気がする (https://github.com/bguthrie/shrubbery)
  (testing "POST /users"
    (let [handler (ig/init-key :duct-api-sample.handler.users/create {:db db})
          response (handler {:body-params {:email "user1@example.com" :password "password"}})]
      (is (= :ataraxy.response/created (first response)))
      (is (re-find #"/users/\d+" (fnext response)))
      (is (some? (first (jdbc/query db-spec ["SELECT TRUE FROM users WHERE email=?" "user1@example.com"]))))))

  (testing "POST /users/signin"
    (let [handler (ig/init-key :duct-api-sample.handler.users/signin {:db db :jwt-secret "xxx"})
          response (handler {:body-params {:email "user1@example.com" :password "password"}})]
      (is (= :ataraxy.response/ok (first response)))
      (is (= "user1@example.com" (-> response fnext :user :email))))))
