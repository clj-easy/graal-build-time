(defproject com.github.clj-easy/graal-build-time "0.0.1"
  :dependencies [[org.graalvm.nativeimage/svm "21.2.0" :scope "provided"]]
  :aot [clj-easy.graal-build-time]
  :java-source-paths ["src"]
  :profiles {:javac {:aot [clj-easy.graal-build-time]}}
)
