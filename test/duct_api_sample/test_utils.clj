(ns duct-api-sample.test-utils
  (:require [duct.database.sql :refer :all]
            [duct.core.env :as e]
            [clojure.java.jdbc :as jdbc]))

(def db-spec
  {:connection (jdbc/get-connection {:connection-uri (e/env "TEST_DB_URL")})})

(def db (->Boundary db-spec))

(defn drop-table [table-name]
    (jdbc/delete! db-spec table-name []))

(defn drop-all-tables []
  (do
    (drop-table :articles)
    (drop-table :users)))

(defn db-creanup [test-fn]
  (test-fn)
  (drop-all-tables))
