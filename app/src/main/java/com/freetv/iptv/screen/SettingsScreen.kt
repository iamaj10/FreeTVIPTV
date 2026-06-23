package com.freetv.iptv.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.tv.material3.Button
import androidx.tv.material3.Text

@Composable
fun SettingsScreen(
    onChangePlaylist: () -> Unit,
    onClearPlaylist: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {

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