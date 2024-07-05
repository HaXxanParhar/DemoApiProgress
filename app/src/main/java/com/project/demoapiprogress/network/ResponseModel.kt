package com.thrubal.app.models.auth

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class ResponseModel {

    @SerializedName("status")
    @Expose
    var status: Boolean = false

    @SerializedName("action")
    @Expose
    var action: String? = null

    @SerializedName("errors")
    var errors: List<String>? = null
}