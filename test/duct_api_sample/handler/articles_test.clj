(ns duct-api-sample.handler.articles-test
  (:require [clojure.test :refer :all]
            [duct-api-sample.test-utils :as utils :refer [db-spec db]]
            [duct.database.sql :refer :all]
            [clojure.java.jdbc :as jdbc]
            [integrant.core :as ig]
            [duct-api-sample.boundary.users :as users]
            duct-api-sample.handler.articles))

(use-fixtures :each
  utils/db-creanup
  (fn [f] (users/create-user db "user1@example.com" "password") (f)))

(deftest post-articles
  (testing "POST /articles"
    (let [handler (ig/init-key :duct-api-sample.handler.articles/create {:db db})
          response (handler {:ataraxy/result [nil "hello world"] :identity {:email "user1@example.com"}})]
      (is (= :ataraxy.response/created (first response)))
      (is (re-find #"/articles/\d+" (fnext response))))))
