package at.tugraz.onpoint.moodle

import android.net.Uri
import android.util.Log
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import java.util.*

class API {
    private val scheme = "https"
    private val authority = "moodle.divora.at"

    fun login() {
        val httpAsync = getRequestURI("login/token.php", mapOf()).httpGet().responseString { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    val ex = result.getException()
                    println(ex)
                }
                is Result.Success -> {
                    val data = result.get()
                    println(data)
                }
            }
        }
        httpAsync.join()
    }

    fun getRequestURI(path: String, query_params: Map<String,String>): String {
        val builder = Uri.Builder();
        builder.scheme(scheme).authority(authority).path(path)
        query_params.forEach { (key: String, value: String) -> builder.appendQueryParameter(key, value) }
        val request_url: String = builder.build().toString()
        return request_url;
    }
}
