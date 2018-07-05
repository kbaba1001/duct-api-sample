(ns dev
  (:refer-clojure :exclude [test])
  (:require [clojure.repl :refer :all]
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

; (test [#'duct-api-sample.boundary.users-test/test-create-user]) こんな感じで使う
(defn test
  ([] (eftest/run-tests (eftest/find-tests "test")))
  ([vars] (eftest/run-tests vars)))

(defn db []
  (-> system (ig/find-derived-1 :duct.database/sql) val))

(defn q [sql]
  (jdbc/query (:spec (db)) sql))

(clojure.tools.namespace.repl/set-refresh-dirs "dev/src" "src" "test")

(when (io/resource "local.clj")
  (load "local"))

(integrant.repl/set-prep! (comp duct/prep read-config))
