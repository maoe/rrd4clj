(ns rrd4clj.examples
   (:use rrd4clj.core rrd4clj.imports)
   (:require
      [rrd4clj.io    :as io]
      [rrd4clj.graph :as  g]  )
   (:use clojure.contrib.import-static)
   (:import
      [java.awt Color Font]
      [java.io File]  )
   (:gen-class)  )

(import-static org.rrd4j.ConsolFun AVERAGE FIRST LAST MAX MIN TOTAL)
(import-static org.rrd4j.DsType ABSOLUTE COUNTER DERIVE GAUGE)
(import-static org.rrd4j.core.Util getTimestamp getTime)

(import-all)

(defn demo-dir []
   (let
      [  home-dir (File. (System/getProperty "user.home"))
         demo-dir (File. (format "%s%srrd4clj-demo" home-dir File/separator))  ]
      (when-not
         (.exists demo-dir)
         (.mkdir  demo-dir)  )
      demo-dir  )  )

(defn demo-path
   [file]
   (format "%s%s%s" (demo-dir) File/separator file)  )


(defn min-max-demo []
   (let
      [  start      (getTime)
         end        (+ start (* 300 300))
         rrd-path   (demo-path "minmax.rrd")
         graph-path (demo-path "minmax.png")  ]
      (let
         [  rrdi
            (RrdDb.
               (rrd rrd-path
                  {  :start-time (dec start)
                     :step       300  }
                  (->DataSource "a" GAUGE 600 Double/NaN Double/NaN)
                  (->RoundRobinArchive AVERAGE 0.5  1 300)
                  (->RoundRobinArchive MIN     0.5 12 300)
                  (->RoundRobinArchive MAX     0.5 12 300)  )  )  ]

         ;; update
         (apply io/update_rrd rrdi
            (for [t (range start end 300)]
               (sample t (+ 50 (* 50 (Math/sin (/ t 3000.0)))))  )  )

         ;; graph
         (io/graph
            (g/graph graph-path
               {  :width 450
                  :height 250
                  :image-format "PNG"
                  :start-time start
                  :end-time (+ start 86400)
                  :title "rrd4clj's MINMAX demo"
                  :anti-aliasing false  }
               (g/->DataSource "a" rrd-path "a" AVERAGE)
               (g/->DataSource "b" rrd-path "a" MIN)
               (g/->DataSource "c" rrd-path "a" MAX)
               (g/->CDefSource "d" "a,-1,*")
               (g/->Area "a" (Color/decode "0xb6e4") "real")
               (g/->Line "b" (Color/decode "0x22e9") "min")
               (g/->Line "c" (Color/decode "0xee22") "max")
               (g/stack-of
                  (g/->Area "d" (Color/decode "0xb6e4") "inv")
                  (g/->Area "d" (Color/decode "0xfffe") "stack")
                  (g/->Area "d" (Color/decode "0xeffe") "stack2")  )  )  )  )  )  )

(defn -main [] (min-max-demo))
