package at.tugraz.onpoint

import at.tugraz.onpoint.moodle.API
import at.tugraz.onpoint.moodle.LoginErrorData
import at.tugraz.onpoint.moodle.LoginSuccessData
import com.github.kittinunf.fuel.core.FuelError
import org.junit.Test

class MockedAPI : API() {
    override fun request(
        path: String,
        query_params: Map<String, String>,
        onSuccess: (data: String) -> Unit,
        onError: (error: FuelError) -> Unit
    ) {
        if (path == "login/token.php") {
            if (query_params["username"] == "pass") {
                onSuccess("{\"token\":\"01234567890\"}")
            } else {
                onSuccess("{\"error\":\"Invalid login, please try again\",\"errorcode\":\"invalidlogin\",\"stacktrace\":null,\"debuginfo\":null,\"reproductionlink\":null}")
            }
        }
    }
}

class MoodleAPITest {
    @Test
    fun verifyLoginRequest() {
        val moodleApi = MockedAPI()
        moodleApi.login("pass", "123") { response: Any ->
            assert(response is LoginSuccessData)
        }
        moodleApi.login("fail", "123") { response: Any ->
            assert(response is LoginErrorData)
        }
    }
}
