package com.freetv.iptv.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.tv.material3.Card
import androidx.tv.material3.Text
import com.freetv.iptv.model.Channel
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.itemsIndexed

@Composable
fun CategoryChannelsScreen(
    categoryName: String,
    channels: List<Channel>,
    initialIndex: Int,
    onChannelSelected: (Channel, Int) -> Unit
){

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex
    )

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {

        item {

            Text(
                text = categoryName
            )

            Text(
                text = "${channels.size} Channels"
            )
        }

        itemsIndexed(channels) { index, channel ->

            Card(
                onClick = {
                    onChannelSelected(channel, index)
                }
            ) {

                Text(
                    text = channel.name
                )
            }
        }
    }
}