import com.example.moodtunes.DataObject.NormalMoodRequest
import com.google.gson.Gson
import okhttp3.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType

object HttpClient {
    val client = OkHttpClient()
    val gson = Gson()

    suspend inline fun <reified T> get(url: String): T? = request("GET", url)

    suspend inline fun <reified T> getProtected(url: String, token: String): T? {
        return request(method = "GET", url = url, token = token)
    }

    suspend inline fun <reified T> post(url: String, body: Any? = null): T? =
        request("POST", url, gson.toJson(body))

    suspend inline fun <reified T> postProtected(url: String, token: String, body: Any? = null): T? =
        request("POST", url = url, token = token, jsonBody = gson.toJson(body))

    suspend inline fun <reified T> patchProtected(url: String, token: String, body: Any? = null): T? =
        request("PATCH", url = url, token = token, jsonBody = gson.toJson(body))

    suspend inline fun <reified T> deleteProtected(url: String, token: String): T? =
        request("DELETE", url = url, token = token)

    suspend inline fun <reified T> request(method: String, url: String, jsonBody: String? = null, token: String? = null): T? {
        return withContext(Dispatchers.IO) {
            val body = jsonBody?.let {
                RequestBody.create("application/json; charset=utf-8".toMediaType(), jsonBody)
            }
            println("[MoodTunes] url: ${url}");
            println("[MoodTunes] body: ${body.toString()}")
            val requestBuild = Request.Builder()
                .url(url)
                .method(method, body)

            if (token != null) {
                requestBuild.addHeader("Authorization", "Bearer $token")
            }

            val request = requestBuild.build()

            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (!response.isSuccessful || responseBody == null) {
                    return@withContext null
                }

                gson.fromJson(responseBody, T::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}

class Call(val baseUrl: String) {
    suspend inline fun <reified T> get(path: String) =
        HttpClient.get<T>(baseUrl + path)

    suspend inline fun <reified T> getProtected(path: String, token: String) =
        HttpClient.getProtected<T>(url = baseUrl + path, token = token)

    suspend inline fun <reified T> post(url: String, body: Any? = null) =
        HttpClient.post<T>(if (url.startsWith("http://") || url.startsWith("https://")) url else baseUrl + url, body)

    suspend inline fun <reified T> patchProtected(url: String, token: String, body: Any? = null) =
        HttpClient.patchProtected<T>(if (url.startsWith("http://") || url.startsWith("https://")) url else baseUrl + url, token = token, body)

    suspend inline fun <reified T> deleteProtected(path: String, token: String) =
        HttpClient.deleteProtected<T>(baseUrl + path, token)

    suspend inline fun <reified T> request(
        method: String,
        url: String,
        jsonBody: String? = null,
        token: String? = null
    ) =  HttpClient.request<T>(method, url, jsonBody, token)
}

val api = Call("http://10.0.2.2:8080")
