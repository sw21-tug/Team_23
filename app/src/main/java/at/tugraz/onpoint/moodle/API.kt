package at.tugraz.onpoint.moodle

import android.net.Uri
import android.util.Log
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.requests.CancellableRequest
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.util.*

data class LoginErrorData(val error: String, val errorcode: String);
data class LoginSuccessData(val token: String, val privatetoken: String);

open class API {
    private val scheme = "https"
    private val authority = "moodle.divora.at"

    inline fun <reified T> login(username: String, password: String, crossinline onResponse: (data: T) -> Unit = {}) {
        val service: String = "moodle_mobile_app"
        val parameters = mapOf("service" to service, "username" to username, "password" to password)
        request("login/token.php", parameters, { data: String ->
            val jsonObject = Gson().fromJson<JsonObject>(data, JsonObject::class.java)
            if (jsonObject.has("errorcode")) {
                val callbackObject = Gson().fromJson(data, LoginErrorData::class.java) as T
                onResponse(callbackObject);
            } else {
                val callbackObject = Gson().fromJson(data, LoginSuccessData::class.java) as T
                onResponse(callbackObject);
            }
        })
    }

    open fun request(path: String, query_params: Map<String,String> = mapOf(), onSuccess: (data: String) -> Unit = {}, onError: (error: FuelError) -> Unit = {}) {
        val builder = Uri.Builder();
        builder.scheme(scheme).authority(authority).path(path)
        query_params.forEach { (key: String, value: String) -> builder.appendQueryParameter(key, value) }
        val request_url: String = builder.build().toString()
        request_url.httpGet().responseString { request, response, result ->
            when(result) {
                is Result.Failure -> {
                    onError(result.error);
                }
                is Result.Success -> {
                    val data = result.get();
                    onSuccess(data);
                }
            }
        }
    }
}
