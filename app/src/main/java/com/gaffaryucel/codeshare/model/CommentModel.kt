package com.gaffaryucel.codeshare.model

import com.google.gson.annotations.SerializedName

class CommentModel{
    @SerializedName("postOwner")
    var postOwner: String? = null

    @SerializedName("commentOwner")
    var commentOwner: String? = null

    @SerializedName("commentId")
    var commentId: String? = null

    @SerializedName("postId")
    var postId: String? = null

    @SerializedName("time")
    var time: String? = null

    @SerializedName("commentText")
    var commentText: String? = null

    constructor(
        commentId: String? = null,
        commentOwner: String? = null,
        postOwner: String? = null,
        commentText: String? = null,
        time: String? = null,
    ) {
        this.commentId = commentId
        this.commentOwner = commentOwner
        this.postOwner = postOwner
        this.commentText = commentText
        this.time = time
    }
}