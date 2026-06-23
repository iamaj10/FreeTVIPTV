package com.freetv.iptv.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.tv.material3.Card
import androidx.tv.material3.Text

@Composable
fun MainMenuScreen(
    onMenuSelected: (String) -> Unit
) {

    val menuItems = listOf(
        "Channels",
        "Search",
        "Load Playlist",
        "Playlists"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {

        items(menuItems) { item ->

            Card(
                onClick = {
                    onMenuSelected(item)
                }
            ) {

                Text(
                    text = item
                )
            }
        }
    }
}