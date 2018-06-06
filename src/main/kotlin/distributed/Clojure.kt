package distributed

import clojure.java.api.Clojure

val requireFn = Clojure.`var`("clojure.core", "require")

val calculator = Clojure.`var`("me.juhezi.local-calculate", "calculate")

val evolve = Clojure.`var`("me.juhezi.core", "single_individual_evolve")
