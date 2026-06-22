package com.freetv.iptv.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.tv.material3.Card
import androidx.tv.material3.Text
import com.freetv.iptv.data.sampleChannels
import com.freetv.iptv.model.Channel

@Composable
fun HomeScreen(
    onChannelSelected: (Channel) -> Unit
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {

        items(sampleChannels) { channel ->

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