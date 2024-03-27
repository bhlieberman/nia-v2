(ns app.model.server-components
  (:require [clojure.java.io :as io]
            [com.wsscode.pathom.connect :as pc]
            [taoensso.timbre :as log]))

(pc/defresolver get-server-resource
  "Retrieves a static asset from the server, like
   an SVG Element that's already been rendered
   in another application."
  [env _]
  ;; this won't be hardcoded later...
  (log/info "retrieving SVG from server")
  {:svg/parens (slurp (io/resource "public/parens.svg"))})