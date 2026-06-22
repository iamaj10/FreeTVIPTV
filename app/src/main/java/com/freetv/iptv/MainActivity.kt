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
import androidx.activity.compose.BackHandler
import com.freetv.iptv.model.Channel
import com.freetv.iptv.screen.HomeScreen
import com.freetv.iptv.ui.theme.FreeTVIPTVTheme
import com.freetv.iptv.data.testPlaylist
import com.freetv.iptv.parser.M3UParser

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            var selectedChannel by remember {
                mutableStateOf<Channel?>(null)
            }

            val channels = remember {
                M3UParser.parse(testPlaylist)
            }

            FreeTVIPTVTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {

                    if (selectedChannel == null) {

                        HomeScreen(
                            channels = channels,
                            onChannelSelected = {
                                selectedChannel = it
                            }
                        )

                    } else {

                        BackHandler {
                            selectedChannel = null
                        }

                        VideoPlayerScreen(
                            streamUrl = selectedChannel!!.streamUrl
                        )
                    }
                }
            }
        }
    }
}