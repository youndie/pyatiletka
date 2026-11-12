package io.github.youndie.pyatiletka.collector

/** The collector's identity towards GitHub, which refuses API requests without a `User-Agent`. The query arrives with B-07. */
object Collector {
    const val USER_AGENT: String = "pyatiletka-collector"
}
