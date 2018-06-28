(ns duct-api-sample.test-utils
  (:require [duct.database.sql :refer :all]
            [clojure.java.jdbc :as jdbc]))

(def db-spec
  {:dbtype "postgresql"
   :dbname "duct-api-sample-test"
   :user "postgres"
   :password ""})

(def db (->Boundary db-spec))

(defn db-creanup [test-fn]
  (test-fn)
  (jdbc/delete! db-spec :users []))
