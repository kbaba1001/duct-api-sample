(ns duct-api-sample.test-utils
  (:require [duct.database.sql :refer :all]
            [duct.core.env :as e]
            [clojure.java.jdbc :as jdbc]))

(def db-spec
  {:dbtype "postgresql"
   :dbname "duct-api-sample-test"
   :user (e/env ["DATABASE_POSTGRESQL_USERNAME" :or "postgres"])
   :password (e/env ["DATABASE_POSTGRESQL_PASSWORD" :or ""])})

(def db (->Boundary db-spec))

(defn db-creanup [test-fn]
  (test-fn)
  (jdbc/delete! db-spec :users []))
