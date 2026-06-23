package com.freetv.iptv

import android.os.Bundle
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
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

            val playlistPicker = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
            ) { uri: Uri? ->

                if (uri != null) {

                    val content = contentResolver
                        .openInputStream(uri)
                        ?.bufferedReader()
                        ?.use { it.readText() }

                    if (content != null) {

                        channels = M3UParser.parse(content)

                        currentScreen = AppScreen.CHANNELS
                    }
                }
            }

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
                                onLoadClicked = { url ->

                                    // We'll implement download next
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