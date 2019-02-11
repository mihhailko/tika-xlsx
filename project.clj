(defproject tika-xlsx "0.1.0"
  :description "Compile Apache Tika json files to desirable xlsx format"
  :url ""
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [cheshire "5.8.0"]
                 [dk.ative/docjure "1.12.0"]]
  :main ^:skip-aot tika-xlsx.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
