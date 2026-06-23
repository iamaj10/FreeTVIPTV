package com.freetv.iptv.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.tv.material3.Button
import androidx.tv.material3.Text
import androidx.compose.ui.unit.dp

@Composable
fun PlaylistsScreen(
    currentPlaylistUrl: String?,
    channelCount: Int,
    onChangePlaylist: () -> Unit,
    onClearPlaylist: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Current Playlist"
        )

        Text(
            text = currentPlaylistUrl
                ?: "No playlist configured"
        )

        Text(
            text = "Type: URL Playlist"
        )

        Text(
            text = "Channels Loaded: $channelCount"
        )

        Button(
            onClick = onChangePlaylist
        ) {
            Text("Change Playlist")
        }

        Button(
            onClick = onClearPlaylist
        ) {
            Text("Clear Playlist")
        }
    }
}