package com.freetv.iptv.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.Text
import androidx.compose.material3.TextField

@Composable
fun URLInputScreen(
    onLoadClicked: (String) -> Unit
) {

    var url by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Playlist URL"
        )

        TextField(
            value = url,
            onValueChange = {
                url = it
            }
        )

        Button(
            onClick = {
                onLoadClicked(url)
            }
        ) {
            Text("Load Playlist")
        }
    }
}