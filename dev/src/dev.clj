(ns dev
  (:refer-clojure :exclude [test])
  (:require [duct.database.sql :refer :all]
            [duct.core.env :as e]
            [clojure.repl :refer :all]
            [fipp.edn :refer [pprint]]
            [clojure.tools.namespace.repl :refer [refresh]]
            [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [duct.core :as duct]
            [duct.core.repl :as duct-repl]
            [eftest.runner :as eftest]
            [integrant.core :as ig]
            [integrant.repl :refer [clear halt go init prep reset]]
            [integrant.repl.state :refer [config system]]))

(duct/load-hierarchy)

(defn read-config []
  (duct/read-config (io/resource "dev.edn")))

(defn test
  ([] (eftest/run-tests (eftest/find-tests "test")))
  ([vars] (eftest/run-tests vars)))

(def db-spec
  {:connection (jdbc/get-connection {:connection-uri (e/env "DEV_DB_URL")})})

(def db (->Boundary db-spec))

(defn q [sql]
  (jdbc/query db-spec sql))

(clojure.tools.namespace.repl/set-refresh-dirs "dev/src" "src" "test")

(when (io/resource "local.clj")
  (load "local"))

(integrant.repl/set-prep! (comp duct/prep read-config))
