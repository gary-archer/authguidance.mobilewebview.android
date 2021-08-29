package com.authguidance.mobileweb.plumbing.errors

import android.util.Base64
import org.json.JSONObject
import java.text.SimpleDateFormat

/*
 * An error entity for the UI
 */
class UIError(
    var area: String,
    val errorCode: String,
    userMessage: String
) : RuntimeException(userMessage) {

    var utcTime: String
    var appAuthCode: String? = ""
    var details: String? = ""

    /*
     * Initialise state
     */
    init {

        val formatter = SimpleDateFormat("MMM dd yyyy HH:mm")
        this.utcTime = formatter.format(System.currentTimeMillis())
    }

    /*
     * Serialize the error based on data
     */
    fun toJson(): String {

        val data = JSONObject()
        data.put("area", this.area)
        data.put("errorCode", this.errorCode)
        data.put("userMessage", this.message)

        if (!this.appAuthCode.isNullOrBlank()) {
            data.put("appAuthCode", this.appAuthCode)
        }

        // These fields are serialized as base 64 to prevent issues with dangerous characters
        val details = this.details
        if (!details.isNullOrBlank()) {
            val detailsEncoded = Base64.encode(this.details!!.toByteArray(), Base64.DEFAULT or Base64.NO_WRAP)
            data.put("details", String(detailsEncoded))
        }

        if (this.stackTrace.isNotEmpty()) {
            val stack = this.stackTrace.joinToString(separator = "\n")
            val stackEncoded = Base64.encode(stack.toByteArray(), Base64.DEFAULT or Base64.NO_WRAP)
            data.put("stack", String(stackEncoded))
        }

        return data.toString()
    }
}
