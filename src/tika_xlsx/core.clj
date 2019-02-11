(ns tika-xlsx.core
  (:require [cheshire.core :refer [generate-string parse-string]])
  (:gen-class))

(use 'dk.ative.docjure.spreadsheet)

(use 'clojure.java.shell)

(def rows [{["filename"
             "File Name"] "Faili nimi"}
           {["created"
             "date"
             "meta:creation-date"
             "Creation-Date"
             "xmpDM:releaseDate"
             "pdf:docinfo:created"
             "dcterms:created"
             "Date/Time"] "Loomiskuupäev"}
           {["title"
             "dc:title"] "Pealkiri"}
           {["pdf:docinfo:creator"
             "Author"
             "producer"
             "dc:contributor"
             "dc:creator"
             "contributor"
             "meta:last-author"
             "Last-Author"
             "xmp:CreatorTool"
             "Application-Name"
             "Software"] "Looja (autor/programm)"}
           {["tika:file_ext"
             "Content-Type"] "Vorming (MimeType)"}
           {["Content-Type"] "Vormingu täisnimetus"}
           {["pdf:PDFVersion"] "Vormingu versioon"}
           {["validity"] "Valiidsus"}
           {["pdfaid:conformance"] "Korrektsus"}
           {["Content-Length"
             "File Size"] "Faili suurus"}
           {["X-TIKA:digest:MD5"] "Checksum MD5"}
           {["filename"] "Millistest failidest koosneb?"}
           {["xmpTPg:NPages"
            "Page-Count"] "Lehekülgede arv"}
           {["tiff:ImageLength"
             "tiff:ImageWidth"
             "Image Width"
             "Image Height"
             "Exif Image Width"
             "Exif Image Height"] "Lehekülje suurus (a x b)"}
           {["meta:word-count"
             "Word-Count"] "Sõnade arv"}
           {["xmpDM:audioSampleRate"] "Kestvus (ms)"}])

(def fls
  (let [fnames (:out (sh "./get-filenames"))
        fspl (clojure.string/split fnames #"\n")]
    fspl))

(defn get-j-keys [fle]
  (keys (first (parse-string (slurp (str "extracts/" fle))))))

(defn get-j-val [fle ky]
  (get (first (parse-string (slurp (str "extracts/" fle)))) ky))

(defn get-val-vec [fle kyvec]
  (map #(get-j-val fle %) kyvec))

(defn get-kyvec [mp]
  (first mp))

(def samples (let [query-result (let [kyvecs (map first (map get-kyvec rows))
                                      estnames (map second (map get-kyvec rows))
                                      fls fls]
                                  (for [f fls
                                       :let [respective-val-vecs (map #(get-val-vec f %) kyvecs)]]
                                   respective-val-vecs))]
               query-result))

(defn cons-filename [fname sample]
  (cons (clojure.string/split fname #"\.json") (rest (vec sample))))

(def samples-w-names (map #(cons-filename %1 %2) fls samples))

(defn remove-nils [sq]
  (filter #(= (class %) java.lang.String) sq))

(defn seq-to-str [sq]
  (let [joined (clojure.string/join "; " (set (remove-nils sq)))]
    joined))

(defn generate-xlsx [filename] (let [wb (create-workbook "Sheet1"
                                                         (let [content (map vec (for [s samples-w-names]
                                                                                  (for [ss s]
                                                                                    (seq-to-str ss))))
                                                               header (vec (map second (map get-kyvec rows)))]
                                                           (vec (cons header content))))]
                                 (save-workbook! filename wb)))

(defn -main
  "Generate xlsx file"
  [& args]
  (when args
    (do (generate-xlsx (first args))
        (System/exit 0))
    (do (println "Desired .xlsx file name missing!\nQuitting...")
        (System/exit 0))))
