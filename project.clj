(defproject duct-api-sample "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [buddy/buddy-hashers "1.3.0"]
                 [duct/core "0.6.2"]
                 [duct/module.logging "0.3.1"]
                 [duct/module.web "0.6.4"]
                 [duct/module.ataraxy "0.2.0"]
                 [duct/database.sql.hikaricp "0.3.3"]
                 [duct/middleware.buddy "0.1.0"]
                 [ragtime "0.7.2"]
                 [org.postgresql/postgresql "42.1.4"]]
  :plugins [[duct/lein-duct "0.10.6"]]
  :main ^:skip-aot duct-api-sample.main
  :resource-paths ["resources" "target/resources"]
  :prep-tasks     ["javac" "compile" ["run" ":duct/compiler"]]
  :aliases {"migrate"  ["run" "-m" "user/migrate"]
            "rollback" ["run" "-m" "user/rollback"]}
  :profiles
  {:dev  [:project/dev :profiles/dev]
   :repl {:prep-tasks   ^:replace ["javac" "compile"]
          :repl-options {:init-ns user}}
   :uberjar {:aot :all}
   :profiles/dev {}
   :project/dev  {:source-paths   ["dev/src"]
                  :resource-paths ["dev/resources"]
                  :dependencies   [[integrant/repl "0.2.0"]
                                   [eftest "0.4.1"]
                                   [spyscope "0.1.6"]]
                  :injections [(require 'spyscope.core)]}})
