package com.rogertsai.mymvvm.data.model

import com.google.gson.annotations.SerializedName

data class Repo(
        val id: Int,
        @SerializedName("name") val name: String,
        @SerializedName("full_name") val full_name: String,
        @SerializedName("description") val description: String,
        @SerializedName("stargazers_count") val stars: Int,
        @SerializedName("owner") val owner: Owner
)