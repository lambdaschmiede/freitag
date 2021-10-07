(ns freitag.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [freitag.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[freitag started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[freitag has shut down successfully]=-"))
   :middleware wrap-dev})
