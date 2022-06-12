(ns triviados.http
  (:require [org.httpkit.client :as http]
           [cheshire.core :as cheshire]
           [camel-snake-kebab.core :as cskc]
           [camel-snake-kebab.extras :as cske]))

(defn- transform-keys-to-kebab-case [m]
  (cske/transform-keys cskc/->kebab-case-keyword m))

(defn http-get [url]
  (let [{:keys [error] :as response} @(http/get url)]
    (if error
      (throw (ex-info (.getMessage error) {:error error :response response}))
      (some-> (:body response)
              (cheshire/parse-string true)
              transform-keys-to-kebab-case))))
