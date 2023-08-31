package com.gaffaryucel.codeshare.model

import com.google.gson.annotations.SerializedName

class Stories {
    @SerializedName("storyId")
    var storyId: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("imageUrl")
    var imageUrl: String? = null

    @SerializedName("timestamp")
    var timestamp: Long = 0

    @SerializedName("likes")
    var likes: Map<String, String>? = null

    @SerializedName("comments")
    var comments: Map<String, CommentModel>? = null

    @SerializedName("tags")
    var tags: List<String> = ArrayList()

    @SerializedName("viewers")
    var viewers : Map<String, String>? = null

    constructor(
        storyId: String? = null,
        name: String? = null,
        imageUrl: String? = null,
        timestamp: Long = 0,
        likes: Map<String, String>? = null,
        comments: Map<String, CommentModel>? = null,
        tags: List<String> = ArrayList(),
        viewers : Map<String, String>? = null
    ) {
        this.storyId = storyId
        this.name = name
        this.imageUrl = imageUrl
        this.timestamp = timestamp
        this.likes = likes
        this.comments = comments
        this.tags = tags
        this.viewers = viewers
    }
    constructor()
}
