(ns freitag.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[freitag started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[freitag has shut down successfully]=-"))
   :middleware identity})
