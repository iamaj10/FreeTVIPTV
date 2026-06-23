package com.freetv.iptv.network

import okhttp3.OkHttpClient
import okhttp3.Request

object PlaylistDownloader {

    private val client = OkHttpClient()

    fun download(url: String): String? {

        return try {

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request)
                .execute()
                .use { response ->

                    response.body?.string()
                }

        } catch (e: Exception) {

            e.printStackTrace()
            null
        }
    }
}