(ns duct-api-sample.handler.users-test
  (:require [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [integrant.core :as ig]
            [integrant.repl.state :refer [config system]]
            [ring.mock.request :as mock]
            [ataraxy.response :as response]
            [duct-api-sample.handler.users :as users]))

; (deftest smoke-test
;   (testing "example page exists"
;     (let [handler  (ig/init-key :duct-sample.handler/example {})
;           response (handler (mock/request :get "/example"))]
;       (is (= :ataraxy.response/ok (first response)) "response ok"))))

(defn db []
  (-> system (ig/find-derived-1 :duct.database/sql) val))

(defn clean-up-users [test-fn]
  (test-fn)
  (jdbc/delete! (:spec (db)) :users []))

(use-fixtures :each clean-up-users)


; (deftest post-users
;   (testing "POST /users"
;     (let [handler (ig/init-key :duct-api-sample.handler.users/create {:db (db)
;                                                                       :jwt-secret "xxxxxxxxxxxxxxxxxxxx"})
;           response (handler (mock/request :post "/users" "\"email\": \"user1 @example.com\", \"password\": \"password\""))]
;       (is (= {:status 201
;               :headers {"content-type" "application/josn"}
;               :body {:key "/users/1"}}
;              response)))))

(deftest post-users
  (testing "POST /users"
    (let [handler (ig/init-key :duct-api-sample.handler.users/create {:db (db)
                                                                      :jwt-secret "xxxxxxxxxxxxxxxxxxxx"})
          response (handler {:ataraxy/result [1 "user1@example.com" "password"]})]
      (is (= :ataraxy.response/created (first response)))
      (is (re-find #"/users/\d+" (fnext response))))))



; 次の hoge の引数は :foo キーを持つ Map を1つ受け取る。
; :foo の value は要素数3の配列を期待しており、
; この配列の添字1, 2 の位置の値を変数 email, password に
; 代入してfunction内で使用する
;
; (defn hoge [{[_ email password] :foo}]
;   (str email password))
; (hoge {:foo [1 2 3]})
; #=> "23"
