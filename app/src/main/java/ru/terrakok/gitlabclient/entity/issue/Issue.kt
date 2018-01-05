package ru.terrakok.gitlabclient.entity.issue

import com.google.gson.annotations.SerializedName
import ru.terrakok.gitlabclient.entity.Assignee
import ru.terrakok.gitlabclient.entity.Author
import ru.terrakok.gitlabclient.entity.Milestone
import java.util.*

data class Issue(
        @SerializedName("id") val id: Long,
        @SerializedName("iid") val iid: Long,
        @SerializedName("state") val state: IssueState,
        @SerializedName("description") val description: String?,
        @SerializedName("author") val author: Author,
        @SerializedName("milestone") val milestone: Milestone?,
        @SerializedName("project_id") val projectId: Long,
        @SerializedName("assignees") val assignees: List<Assignee>,
        @SerializedName("updated_at") val updatedAt: Date?,
        @SerializedName("title") val title: String?,
        @SerializedName("created_at") val createdAt: Date,
        @SerializedName("labels") val labels: List<String>,
        @SerializedName("user_notes_count") val userNotesCount: Int,
        @SerializedName("due_date") val dueDate: Date?,
        @SerializedName("web_url") val webUrl: String?,
        @SerializedName("confidential") val confidential: Boolean,
        @SerializedName("upvotes") val upvotes: Int,
        @SerializedName("downvotes") val downvotes: Int
)