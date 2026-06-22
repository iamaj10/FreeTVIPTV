package com.freetv.iptv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.freetv.iptv.model.Channel
import com.freetv.iptv.screen.HomeScreen
import com.freetv.iptv.ui.theme.FreeTVIPTVTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            var selectedChannel by remember {
                mutableStateOf<Channel?>(null)
            }

            FreeTVIPTVTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {

                    if (selectedChannel == null) {

                        HomeScreen(
                            onChannelSelected = {
                                selectedChannel = it
                            }
                        )

                    } else {

                        VideoPlayerScreen(
                            streamUrl = selectedChannel!!.streamUrl
                        )
                    }
                }
            }
        }
    }
}