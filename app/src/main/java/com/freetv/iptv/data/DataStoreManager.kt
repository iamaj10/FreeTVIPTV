package com.freetv.iptv.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(
    name = "settings"
)

object DataStoreManager {

    private val PLAYLIST_URL =
        stringPreferencesKey("playlist_url")

    suspend fun savePlaylistUrl(
        context: Context,
        url: String
    ) {

        context.dataStore.edit { prefs ->

            prefs[PLAYLIST_URL] = url
        }
    }

    suspend fun getPlaylistUrl(
        context: Context
    ): String? {

        val prefs =
            context.dataStore.data.first()

        return prefs[PLAYLIST_URL]
    }

    suspend fun clearPlaylistUrl(
        context: Context
    ) {

        context.dataStore.edit { prefs ->

            prefs.remove(PLAYLIST_URL)
        }
    }
}