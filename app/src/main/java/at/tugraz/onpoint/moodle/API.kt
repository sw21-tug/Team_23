package at.tugraz.onpoint.moodle

import android.net.Uri
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.JsonObject

data class LoginErrorData(val error: String, val errorcode: String)
data class LoginSuccessData(val token: String, val privatetoken: String)
data class AssignmentError(val exception: String, val errorcode: String, val message: String)

data class AssignmentResponse(
    val courses: List<Course>
)
data class Course(
    val assignments: List<Assignment>
)
data class Assignment(
    val name: String,
    val intro: String,
    val duedate: Long,
    val introattachments: List<Attachment>,
)
data class Attachment(
    val filename: String,
    val fileurl: String,
)

open class API {
    private val scheme = "https"
    private var authority = ""
    var token = ""

    // https://moodle.divora.at/webservice/rest/server.php?wstoken=9bdd89912814d67a6b429c183505119f&wsfunction=mod_assign_get_assignments%20&moodlewsrestformat=json

    inline fun <reified T> getAssignments(crossinline onResponse: (data: T) -> Unit = {}) {
        val parameters = mapOf(
            "wstoken" to this.token,
            "wsfunction" to "mod_assign_get_assignments",
            "moodlewsrestformat" to "json"
        )
        request("/webservice/rest/server.php", parameters, { data: String ->
            val jsonObject = Gson().fromJson(data, JsonObject::class.java)
            if (jsonObject.has("errorcode")) {
                val callbackObject = Gson().fromJson(data, AssignmentError::class.java)
                onResponse(callbackObject as T)
            } else {
                val callbackObject = Gson().fromJson(data, AssignmentResponse::class.java)
                onResponse(callbackObject as T)
            }
        })
    }


    inline fun <reified T> login(
        username: String,
        password: String,
        crossinline onResponse: (data: T) -> Unit = {}
    ) {
        val service = "moodle_mobile_app"
        val parameters = mapOf("service" to service, "username" to username, "password" to password)
        request("login/token.php", parameters, { data: String ->
            val jsonObject = Gson().fromJson(data, JsonObject::class.java)
            if (jsonObject.has("errorcode")) {
                val callbackObject = Gson().fromJson(data, LoginErrorData::class.java)
                onResponse(callbackObject as T)
            } else {
                val loginSuccessData = Gson().fromJson(data, LoginSuccessData::class.java)
                this.token = loginSuccessData.token
                onResponse(loginSuccessData as T)
            }
        }, {
            onResponse(false as T)
        })
    }

    open fun request(
        path: String,
        query_params: Map<String, String> = mapOf(),
        onSuccess: (data: String) -> Unit = {},
        onError: (error: FuelError) -> Unit = {}
    ) {
        val builder = Uri.Builder()
        builder.scheme(scheme).authority(authority).path(path)
        query_params.forEach { (key: String, value: String) ->
            builder.appendQueryParameter(
                key,
                value
            )
        }
        val requestUrl: String = builder.build().toString()
        requestUrl.httpGet().responseString { _, _, result ->
            when (result) {
                is Result.Failure -> {
                    onError(result.error)
                }
                is Result.Success -> {
                    val data = result.get()
                    onSuccess(data)
                }
            }
        }
    }

    fun setAuthority(auth: String) {
        this.authority = auth
    }
}
