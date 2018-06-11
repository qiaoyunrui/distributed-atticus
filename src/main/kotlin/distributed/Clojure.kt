package distributed

import clojure.java.api.Clojure

val requireFn = Clojure.`var`("clojure.core", "require")

val calculator = Clojure.`var`("me.juhezi.local-calculate", "calculate")

val decoder = Clojure.`var`("me.juhezi.local-calculate", "decode")

val evolve = Clojure.`var`("me.juhezi.core", "single_individual_evolve")
