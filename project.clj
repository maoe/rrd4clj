(defproject org.clojars.pdelong/rrd4clj "1.0.2"
   :description "RRD API for Clojure"
   :dependencies
   [  [org.clojure/clojure         "1.10.0"]
      [org.clojure/clojure-contrib "1.2.0"]
      [org.rrd4j/rrd4j             "3.5"]  ]
   :license {:name "New BSD License"}
   :main rrd4clj.examples
   :namespaces [rrd4clj.examples rrd4clj.core rrd4clj.io]
   :url "https://github.com/pdelong42/rrd4clj"  )
