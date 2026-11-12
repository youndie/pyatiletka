package io.github.youndie.pyatiletka.core.screen

/**
 * Why a screen shows a problem — the reasons feature-refresh and feature-settings name, without the
 * layer that found them. Both skins word them; [detail] is for a reason that has no fixed wording.
 */
data class Problem(
    val kind: Kind,
    val detail: String? = null,
) {
    enum class Kind { NoNetwork, NoToken, TokenRejected, OwnerNotFound, NotPublished, NewerSchema, Other }
}
