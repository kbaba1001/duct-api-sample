(ns user
  (:require [duct.core.env :as e]
            [ragtime.jdbc :as jdbc]
            [ragtime.repl :as r-repl]))

(defn dev
  "Load and switch to the 'dev' namespace."
  []
  (require 'dev)
  (in-ns 'dev)
  :loaded)

(defn- migration-config [env]
  (let [urls {"dev" (e/env "DEV_DB_URL")
              "test" (e/env "TEST_DB_URL")}]
    {:datastore  (jdbc/sql-database {:connection-uri (urls env)})
     :migrations (jdbc/load-resources "migrations")}))

(defn migrate
  ([] (migrate "dev"))
  ([env] (r-repl/migrate (migration-config env))))

(defn rollback
  ([] (rollback "dev"))
  ([env] (r-repl/rollback (migration-config env))))
