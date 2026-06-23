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
import androidx.compose.material3.Text
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.runtime.LaunchedEffect
import com.freetv.iptv.model.Channel

@Composable
fun SearchScreen(
    channels: List<Channel>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onChannelSelected: (Channel) -> Unit
) {

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

    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
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

        if (searchQuery.isNotBlank()) {

            Text(
                text = "${filteredChannels.size} Results"
            )
        }

        TextField(
            modifier = Modifier.focusRequester(
                focusRequester
            ),

            value = searchQuery,

            onValueChange = {
                onSearchQueryChanged(it)
            }
        )

        if (
            searchQuery.isNotBlank() &&
            filteredChannels.isEmpty()
        ) {

            Text(
                text = "No channels found"
            )
        }


        LazyColumn {

            items(filteredChannels) { channel ->

                Card(
                    onClick = {
                        onChannelSelected(channel)
                    }
                ) {

                    Text(
                        text = "${channel.name} (${channel.category})"
                    )
                }
            }
        }
    }
}