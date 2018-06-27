(ns duct-api-sample.handler.users-test
  (:require [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [integrant.core :as ig]
            [integrant.repl.state :refer [config system]]))

; TODO ig/find-derived-1 でテスト環境を引っ張ってくるより, duct.database.sql.Boundary のオブジェクトを作ってやるほうが良いのかもしれない
(defn db []
  (-> system (ig/find-derived-1 :duct.database/sql) val))

(defn db-spec []
  (:spec (db)))

(defn clean-up-users [test-fn]
  (test-fn)
  (jdbc/delete! (db-spec) :users []))

(use-fixtures :each clean-up-users)

(deftest post-users
  ; TODO
  ; * boundaryのテストがあるのでdbをスタブしても良い気がする (https://github.com/bguthrie/shrubbery)
  ; * handlerで jwt-secretを使ってないから渡す必要ないかも
  (testing "POST /users"
    (let [handler (ig/init-key :duct-api-sample.handler.users/create {:db (db)
                                                                      :jwt-secret "xxxxxxxxxxxxxxxxxxxx"})
          response (handler {:ataraxy/result [1 "user1@example.com" "password"]})]
      (is (= :ataraxy.response/created (first response)))
      (is (re-find #"/users/\d+" (fnext response)))
      (is (some? (first (jdbc/query (db-spec) ["SELECT TRUE FROM users WHERE email=?" "user1@example.com"])))))))


; 次の hoge の引数は :foo キーを持つ Map を1つ受け取る。
; :foo の value は要素数3の配列を期待しており、
; この配列の添字1, 2 の位置の値を変数 email, password に
; 代入してfunction内で使用する
;
; (defn hoge [{[_ email password] :foo}]
;   (str email password))
; (hoge {:foo [1 2 3]})
; #=> "23"
