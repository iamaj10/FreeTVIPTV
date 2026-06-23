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
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.freetv.iptv.network.PlaylistDownloader
import com.freetv.iptv.model.Channel
import com.freetv.iptv.screen.HomeScreen
import com.freetv.iptv.ui.theme.FreeTVIPTVTheme
import com.freetv.iptv.data.testPlaylist
import com.freetv.iptv.parser.M3UParser
import com.freetv.iptv.screen.MainMenuScreen
import com.freetv.iptv.screen.URLInputScreen

enum class AppScreen {
    MENU,
    URL_INPUT,
    CHANNELS,
    PLAYER
}
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            var selectedChannel by remember {
                mutableStateOf<Channel?>(null)
            }

            var currentScreen by remember {
                mutableStateOf(AppScreen.MENU)
            }

            var channels by remember {
                mutableStateOf(
                    M3UParser.parse(testPlaylist)
                )
            }

            var isLoading by remember {
                mutableStateOf(false)
            }

            val scope = rememberCoroutineScope()

            FreeTVIPTVTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {

                    when (currentScreen) {

                        AppScreen.MENU -> {

                            MainMenuScreen(
                                onMenuSelected = { item ->

                                    when (item) {

                                        "Channels" -> {
                                            currentScreen = AppScreen.CHANNELS
                                        }

                                        "Load Playlist" -> {
                                            currentScreen = AppScreen.URL_INPUT
                                        }

                                        "Settings" -> {
                                            // Future screen
                                        }
                                    }
                                }
                            )
                        }

                        AppScreen.URL_INPUT -> {

                            BackHandler {
                                currentScreen = AppScreen.MENU
                            }

                            URLInputScreen(
                                isLoading = isLoading,
                                onLoadClicked = { url ->

                                    if (
                                        !url.startsWith("http://") &&
                                        !url.startsWith("https://")
                                    ) {
                                        return@URLInputScreen
                                    }

                                    scope.launch {

                                        isLoading = true

                                        val playlistContent =
                                            withContext(Dispatchers.IO) {

                                                PlaylistDownloader.download(url)
                                            }

                                        isLoading = false

                                        if (playlistContent != null) {

                                            channels = M3UParser.parse(
                                                playlistContent
                                            )

                                            currentScreen =
                                                AppScreen.CHANNELS
                                        }
                                    }
                                }
                            )
                        }

                        AppScreen.CHANNELS -> {

                            BackHandler {
                                currentScreen = AppScreen.MENU
                            }

                            HomeScreen(
                                channels = channels,
                                onChannelSelected = {
                                    selectedChannel = it
                                    currentScreen = AppScreen.PLAYER
                                }
                            )
                        }

                        AppScreen.PLAYER -> {

                            BackHandler {
                                currentScreen = AppScreen.CHANNELS
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
}