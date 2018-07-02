(ns duct-api-sample.test-utils
  (:require [duct.database.sql :refer :all]
            [duct.core.env :as e]
            [clojure.java.jdbc :as jdbc]))

(def db-spec
  {:connection (jdbc/get-connection {:connection-uri (e/env "TEST_DB_URL")})})

(def db (->Boundary db-spec))

(defn db-creanup [test-fn]
  (test-fn)
  (jdbc/delete! db-spec :users []))
