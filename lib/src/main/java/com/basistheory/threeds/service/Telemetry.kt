package com.basistheory.threeds.service

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

internal object Telemetry {
    private const val INTAKE_URL =
        "https://http-intake.logs.datadoghq.com/v1/input/pubb96b84a13912504f4354f2d794ea4fab"

    private val client = OkHttpClient()
    private val jsonMediaType = "application/json".toMediaType()

    fun send(
        event: String,
        sessionId: String,
        attributes: Map<String, String> = emptyMap()
    ) {
        val payload = JSONObject().apply {
            put("application", "3ds-android")
            put("ddsource", "3ds-android")
            put("service", "3ds-android")
            put("event", event)
            put("sessionId", sessionId)
            put("message", event)
            attributes.forEach { (key, value) -> put(key, value) }
        }

        val request = Request.Builder()
            .url(INTAKE_URL)
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                response.close()
            }
        })
    }
}
