package ru.terrakok.gitlabclient.model.data.server

import io.reactivex.Single
import retrofit2.http.*
import ru.terrakok.gitlabclient.entity.*
import ru.terrakok.gitlabclient.entity.event.Event
import ru.terrakok.gitlabclient.entity.event.EventAction
import ru.terrakok.gitlabclient.entity.event.EventTarget
import ru.terrakok.gitlabclient.entity.mergerequest.MergeRequest
import ru.terrakok.gitlabclient.entity.mergerequest.MergeRequestScope
import ru.terrakok.gitlabclient.entity.mergerequest.MergeRequestState
import ru.terrakok.gitlabclient.entity.mergerequest.MergeRequestViewType
import java.util.*

/**
 * @author Konstantin Tskhovrebov (aka terrakok). Date: 28.03.17
 */
interface GitlabApi {
    companion object {
        const val API_PATH = "api/v4"
    }

    @FormUrlEncoded
    @POST("oauth/token")
    fun auth(
            @Field("client_id") appId: String,
            @Field("client_secret") appKey: String,
            @Field("code") code: String,
            @Field("redirect_uri") redirectUri: String,
            @Field("grant_type") type: String = "authorization_code"
    ): Single<TokenData>

    @GET("$API_PATH/projects")
    fun getProjects(
            @Query("archived") archived: Boolean?,
            @Query("visibility") visibility: Visibility?,
            @Query("order_by") orderBy: OrderBy?,
            @Query("sort") sort: Sort?,
            @Query("search") search: String?,
            @Query("simple") simple: Boolean?,
            @Query("owned") owned: Boolean?,
            @Query("membership") membership: Boolean?,
            @Query("starred") starred: Boolean?,
            @Query("page") page: Int,
            @Query("per_page") pageSize: Int
    ): Single<List<Project>>

    @GET("$API_PATH/projects/{id}")
    fun getProject(
            @Path("id") id: Long
    ): Single<Project>

    @GET("$API_PATH/projects/{id}/repository/files/{file_path}")
    fun getFile(
            @Path("id") id: Long,
            @Path("file_path") filePath: String,
            @Query("ref") branchName: String
    ): Single<File>

    @GET("$API_PATH/user")
    fun getMyUser(): Single<User>

    @GET("$API_PATH/issues")
    fun getMyIssues(
            @Query("state") state: IssueState?,
            @Query("labels") labels: String?,
            @Query("milestone") milestone: String?,
            @Query("iids") iids: Array<Long>?,
            @Query("order_by") orderBy: OrderBy?,
            @Query("sort") sort: Sort?,
            @Query("search") search: String?,
            @Query("page") page: Int,
            @Query("per_page") pageSize: Int
    ): Single<List<Issue>>

    @GET("$API_PATH/events")
    fun getEvents(
            @Query("action") action: EventAction?,
            @Query("target_type") targetType: EventTarget?,
            @Query("before") beforeDay: String?,
            @Query("after") afterDay: String?,
            @Query("sort") sort: Sort?,
            @Query("page") page: Int,
            @Query("per_page") pageSize: Int
    ): Single<List<Event>>

    @GET("$API_PATH/merge_requests")
    fun getMergeRequests(
            @Query("state") state: MergeRequestState?,
            @Query("milestone") milestone: String?,
            @Query("view") viewType: MergeRequestViewType?,
            @Query("labels") labels: String?,
            @Query("created_before") createdBefore: Date?,
            @Query("created_after") createdAfter: Date?,
            @Query("scope") scope: MergeRequestScope?,
            @Query("author_id") authorId: Int?,
            @Query("assignee_id") assigneeId: Int?,
            @Query("my_reaction_emoji") meReactionEmoji: String?,
            @Query("order_by") orderBy: OrderBy?,
            @Query("sort") sort: Sort?,
            @Query("page") page: Int,
            @Query("per_page") pageSize: Int
    ): Single<List<MergeRequest>>

    fun getProjectMergeRequests(
            @Path("project_id") projectId: Int,
            @Query("state") state: MergeRequestState?,
            @Query("milestone") milestone: String?,
            @Query("view") viewType: MergeRequestViewType?,
            @Query("labels") labels: String?,
            @Query("created_before") createdBefore: Date?,
            @Query("created_after") createdAfter: Date?,
            @Query("scope") scope: MergeRequestScope?,
            @Query("author_id") authorId: Int?,
            @Query("assignee_id") assigneeId: Int?,
            @Query("my_reaction_emoji") meReactionEmoji: String?,
            @Query("order_by") orderBy: OrderBy?,
            @Query("sort") sort: Sort?,
            @Query("page") page: Int,
            @Query("per_page") pageSize: Int
    ): Single<List<MergeRequest>>

    @GET("$API_PATH/projects/{project_id}/merge_requests/{merge_request_id}")
    fun getMergeRequest(
            @Path("project_id") projectId: Int,
            @Path("merge_request_id") mergeRequestId: Int
    ): Single<MergeRequest>

    @GET("$API_PATH/projects/{project_id}/merge_requests/{merge_request_id}/commits")
    fun getMergeRequestCommits(
            @Path("project_id") projectId: Int,
            @Path("merge_request_id") mergeRequestId: Int
    ): Single<List<Commit>>

    @GET("$API_PATH/projects/{project_id}/merge_requests/{merge_request_id}/changes")
    fun getMergeRequestChanges(
            @Path("project_id") projectId: Int,
            @Path("merge_request_id") mergeRequestId: Int
    ): Single<MergeRequest>

    @GET("$API_PATH/projects/{project_id}/merge_requests/{merge_request_id}/notes")
    fun getMergeRequestNotes(
            @Path("project_id") projectId: Int,
            @Path("merge_request_id") mergeRequestId: Int
    ): Single<List<Note>>
}