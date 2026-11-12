package io.github.youndie.pyatiletka.collector

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

// The shape of `portfolio.graphql`'s answer, as much of it as the collector reads. Not part of the
// snapshot contract: nothing outside this package sees these types.

@Serializable
internal data class GqlRequest(
    val query: String,
    val variables: Map<String, JsonElement>,
)

@Serializable
internal data class GqlResponse(
    val data: GqlData? = null,
    val errors: List<GqlError>? = null,
)

@Serializable
internal data class GqlError(
    val message: String,
    val type: String? = null,
)

@Serializable
internal data class GqlData(
    val repositoryOwner: GqlOwner?,
)

@Serializable
internal data class GqlOwner(
    val repositories: GqlRepositories,
)

@Serializable
internal data class GqlRepositories(
    val pageInfo: GqlPageInfo,
    val nodes: List<GqlRepo>,
)

@Serializable
internal data class GqlPageInfo(
    val hasNextPage: Boolean,
    val endCursor: String? = null,
)

@Serializable
internal data class GqlRepo(
    val nameWithOwner: String,
    val url: String,
    val visibility: String,
    val isArchived: Boolean,
    val isFork: Boolean,
    val pushedAt: String? = null,
    val defaultBranchRef: GqlBranch? = null,
    val pullRequests: GqlPullRequests,
    val backlog: GqlBlob? = null,
    val flatBacklog: GqlBlob? = null,
)

@Serializable
internal data class GqlBranch(
    val name: String,
    val target: GqlCommit? = null,
)

@Serializable
internal data class GqlCommit(
    val statusCheckRollup: GqlRollup? = null,
)

@Serializable
internal data class GqlRollup(
    val state: String,
    val contexts: GqlContexts? = null,
)

@Serializable
internal data class GqlContexts(
    val nodes: List<GqlContext>,
)

@Serializable
internal data class GqlContext(
    @SerialName("__typename") val typename: String,
    val name: String? = null,
    val conclusion: String? = null,
    val detailsUrl: String? = null,
    val context: String? = null,
    val state: String? = null,
    val targetUrl: String? = null,
)

@Serializable
internal data class GqlPullRequests(
    val nodes: List<GqlPullRequest>,
)

@Serializable
internal data class GqlPullRequest(
    val number: Int,
    val title: String,
    val isDraft: Boolean,
    val createdAt: String,
    val url: String,
    val author: GqlActor? = null,
    val commits: GqlPrCommits,
)

@Serializable
internal data class GqlActor(
    @SerialName("__typename") val typename: String,
    val login: String,
)

@Serializable
internal data class GqlPrCommits(
    val nodes: List<GqlPrCommit>,
)

@Serializable
internal data class GqlPrCommit(
    val commit: GqlCommit,
)

@Serializable
internal data class GqlBlob(
    val text: String? = null,
    val byteSize: Int? = null,
)
