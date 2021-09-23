(ns single-segment-example
  "Single segment namespaces generate no resulting package.
  As such, they can't be included for build time registration.

  This namespace is referenced by our graal build time test app,
  it should generate a warning and be properly skipped.")

(defn dummy [])
