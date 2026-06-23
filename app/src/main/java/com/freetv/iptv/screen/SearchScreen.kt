package com.freetv.iptv.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.Text
import com.freetv.iptv.model.Channel

@Composable
fun SearchScreen(
    channels: List<Channel>,
    onChannelSelected: (Channel) -> Unit
) {

    var searchQuery by remember {
        mutableStateOf("")
    }

    val filteredChannels = remember(
        searchQuery,
        channels
    ) {

        if (searchQuery.isBlank()) {

            emptyList()

        } else {

            channels.filter {

                it.name.contains(
                    searchQuery,
                    ignoreCase = true
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Search Channels"
        )

        TextField(
            value = searchQuery,

            onValueChange = {
                searchQuery = it
            }
        )

        LazyColumn {

            items(filteredChannels) { channel ->

                Card(
                    onClick = {
                        onChannelSelected(channel)
                    }
                ) {

                    Text(
                        text = channel.name
                    )
                }
            }
        }
    }
}