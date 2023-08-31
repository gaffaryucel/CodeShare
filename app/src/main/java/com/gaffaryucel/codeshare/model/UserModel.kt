package com.gaffaryucel.codeshare.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

class UserModel {
    @SerializedName("id")
    var id: String? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("email")
    var email: String? = null
    @SerializedName("createdAt")
    var createdAt: String? = null
    @SerializedName("updatedAt")
    var updatedAt: String? = null
    @SerializedName("deletedAt")
    var deletedAt: String? = null
    @SerializedName("bio")
    var bio: String? = null
    @SerializedName("profileImageUrl")
    var profileImageUrl: String? = null
    @SerializedName("followersCount")
    var followersCount: Int? = null
    @SerializedName("followingCount")
    var followingCount: Int? = null
    @SerializedName("postsCount")
    var postsCount: Int? = null
    @SerializedName("posts")
    var posts: Map<String, PostModel>? = null
    @SerializedName("friends")
    var friends: Map<String, String>? = null
    constructor(
        id: String? = null,
        name: String? = null,
        email: String? = null,
        createdAt: String? = null,
        updatedAt: String? = null,
        deletedAt: String? = null,
        bio: String? = null,
        profileImageUrl: String? = null,
        followersCount: Int? = null,
        followingCount: Int? = null,
        postsCount: Int? = null,
        posts :  Map<String, PostModel>? = null,
        friends :  Map<String, String>? = null,
    ){
        this.id = id
        this.name = name
        this.email = email
        this.createdAt = createdAt
        this.updatedAt = updatedAt
        this.deletedAt = deletedAt
        this.bio = bio
        this.profileImageUrl = profileImageUrl
        this.followersCount = followersCount
        this.followingCount = followingCount
        this.postsCount = postsCount
        this.posts = posts
        this.friends = friends
    }
    constructor()
}