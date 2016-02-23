(ns avalon.utils)

(defn row [& children]
  [:div.row children])

(defn col [& children]; {:keys [width m-width] :or {width 4 m-width 12}}]
  [:div.col-xs-12.col-sm-4 children])