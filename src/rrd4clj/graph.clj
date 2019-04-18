(ns rrd4clj.graph
   (:require [clojure.zip :as z])
   (:import
      [java.awt Color Font]
      org.rrd4j.graph.RrdGraphDef  )  )

(defprotocol GraphElement (add [x gr] "add"))

(defprotocol SourcedGraphElement (stack [x] "boolean"))

(deftype Stack [name color legend]
  GraphElement
  (add [x gr] (.stack gr name color legend))
  SourcedGraphElement
  (stack [x] x))

(deftype Area [name color legend]
  GraphElement
  (add [x gr] (.area gr name color legend))
  SourcedGraphElement
  (stack [x] (->Stack name color legend)))

(deftype Line [name color legend]
  GraphElement
  (add [x gr] (.line gr name color legend))
  SourcedGraphElement
  (stack [x] (->Stack name color legend)))

(deftype DataSource [name rrd-path ds-name consol-fun]
  GraphElement
  (add [x gr] (.datasource gr name rrd-path ds-name consol-fun)))

(deftype CDefSource [name reverse-polish-notation]
  GraphElement
  (add [x gr] (.datasource gr name reverse-polish-notation)))

(defn- sourced? [elem]
  (instance? rrd4clj.graph.SourcedGraphElement elem))

;; public API
(def area Area)
(def line Line)
(def data-source DataSource)
(def cdef-source CDefSource)

(defn stack-of [& elems]
  (let [z (z/seq-zip elems)]
    (loop [loc (z/next z) replace? false]
      (cond
        (z/end? loc) (z/root loc)
        replace?
          (if (sourced? (z/node loc))
            (recur (z/next (z/edit loc stack)) true)
            (recur (z/next loc) true))
        :else
          (if (sourced? (z/node loc))
            (recur (z/next loc) true)
            (recur (z/next loc) false))))))

(defn graph
   #^{  :doc "Creates a new RRD Graph definition object"
        :arglists '()  }
   [  path
      {  :keys
         [  alt-auto-scale alt-auto-scale-max alt-y-grid alt-y-mrtg
            anti-aliasing background-image base color draw-x-grid
            draw-y-grid end-time first-day-of-week force-rules-legend
            height image-format image-info image-quality interlaced
            large-font lazy logarithmic max-value min-value no-legend
            no-minor-grid only-graph overlay-image pool-used rigid
            show-signature small-font start-time step
            text-anti-aliasing time-axis title unit units-exponent
            units-length value-axis vertical-label width  ]
         :or
         {  start-time RrdGraphDef/DEFAULT_START
            end-time RrdGraphDef/DEFAULT_END
            pool-used true
            time-axis nil
            value-axis nil
            alt-y-grid false
            no-minor-grid false
            alt-y-mrtg false
            alt-auto-scale false
            alt-auto-scale-max false
            units-exponent false
            units-length RrdGraphDef/DEFAULT_UNITS_LENGTH
            vertical-label ""
            width RrdGraphDef/DEFAULT_WIDTH
            height RrdGraphDef/DEFAULT_HEIGHT
            interlaced false
            image-info nil
            image-format RrdGraphDef/DEFAULT_IMAGE_FORMAT
            background-image nil
            overlay-image nil
            unit nil
            lazy false
            min-value nil
            max-value nil
            rigid false
            base RrdGraphDef/DEFAULT_BASE
            logarithmic false
            color nil
            no-legend false
            only-graph false
            force-rules-legend false
            title ""
            step nil
            small-font nil
            large-font nil
            draw-x-grid true
            draw-y-grid true
            image-quality RrdGraphDef/DEFAULT_IMAGE_QUALITY
            anti-aliasing true
            text-anti-aliasing true
            show-signature true
            first-day-of-week nil  }  }
      & more  ]
   (let [gr-def (RrdGraphDef.)]
      (doto gr-def
         (.setFilename path)
         (.setTimeSpan start-time end-time)
         (.setAntiAliasing anti-aliasing)
         (.setTextAntiAliasing text-anti-aliasing)
         (.setTitle title)
         (.setHeight height)
         (.setWidth width)
         (if small-font (.setSmallFont small-font))
         (if large-font (.setLargeFont large-font))
         (.setImageFormat image-format)  )
      (doseq [x (flatten more)] (add x gr-def))
      gr-def))
