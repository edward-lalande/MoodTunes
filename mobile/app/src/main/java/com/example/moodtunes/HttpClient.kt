import com.google.gson.Gson
import okhttp3.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType

object HttpClient {
    val client = OkHttpClient()
    val gson = Gson()

    suspend inline fun <reified T> get(url: String): T? = request("GET", url)

    suspend inline fun <reified T> post(url: String, body: Any): T? =
        request("POST", url, gson.toJson(body))

    suspend inline fun <reified T> request(method: String, url: String, jsonBody: String? = null): T? {
        return withContext(Dispatchers.IO) {
            val body = jsonBody?.let {
                RequestBody.create("application/json; charset=utf-8".toMediaType(), it)
            }

            val request = Request.Builder()
                .url(url)
                .method(method, body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: return@withContext null

            gson.fromJson(responseBody, T::class.java)
        }
    }
}

class Call(val baseUrl: String) {
    suspend inline fun <reified T> get(path: String) =
        HttpClient.get<T>(baseUrl + path)
    suspend inline fun <reified T> request(
        method: String,
        url: String,
        jsonBody: String? = null
    ) =  HttpClient.request<T>(method, url, jsonBody)
}
