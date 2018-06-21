(ns user
  (:refer-clojure :exclude [test]))

(defn dev
  "Load and switch to the 'dev' namespace."
  []
  (require 'dev)
  (in-ns 'dev)
  :loaded)

(defn test
  "Load and switch to the 'test' namespace."
  []
  (require 'test)
  (in-ns 'test)
  :loaded)
