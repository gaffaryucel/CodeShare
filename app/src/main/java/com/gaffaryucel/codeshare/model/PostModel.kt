package com.gaffaryucel.codeshare.model

import com.google.gson.annotations.SerializedName

class PostModel {
    @SerializedName("post_id")
    var postId: String? = null

    @SerializedName("user_id")
    var userId: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("content")
    var content: String? = null

    @SerializedName("timestamp")
    var timestamp: String? = null

    @SerializedName("likes")
    var likes: Map<String, String>? = null

    constructor(
        postId: String? = null,
        userId: String? = null,
        description: String? = null,
        content: String? = null,
        timestamp: String? = null,
        likes: Map<String, String>? = null,
    ) {
        this.postId = postId
        this.userId = userId
        this.description = description
        this.content = content
        this.timestamp = timestamp
        this.likes = likes
    }
    constructor()

}