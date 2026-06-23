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
import androidx.compose.runtime.LaunchedEffect
import com.freetv.iptv.data.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.freetv.iptv.network.PlaylistDownloader
import com.freetv.iptv.model.Channel
import com.freetv.iptv.screen.HomeScreen
import com.freetv.iptv.ui.theme.FreeTVIPTVTheme
import com.freetv.iptv.parser.M3UParser
import com.freetv.iptv.screen.MainMenuScreen
import com.freetv.iptv.screen.URLInputScreen
import com.freetv.iptv.screen.LoadingScreen
import com.freetv.iptv.screen.SettingsScreen

enum class AppScreen {
    MENU,
    URL_INPUT,
    SETTINGS,
    CHANNELS,
    PLAYER
}
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val context = this

            var selectedChannel by remember {
                mutableStateOf<Channel?>(null)
            }

            var currentScreen by remember {
                mutableStateOf(AppScreen.MENU)
            }

            var channels by remember {
                mutableStateOf<List<Channel>>(
                    emptyList()
                )
            }

            var isLoading by remember {
                mutableStateOf(false)
            }

            var startupChecked by remember {
                mutableStateOf(false)
            }

            LaunchedEffect(Unit) {

                val savedUrl =
                    DataStoreManager.getPlaylistUrl(context)

                if (savedUrl != null) {

                    isLoading = true

                    val playlistContent =
                        withContext(Dispatchers.IO) {
                            PlaylistDownloader.download(savedUrl)
                        }

                    if (playlistContent != null) {

                        channels =
                            M3UParser.parse(playlistContent)

                        currentScreen =
                            AppScreen.CHANNELS
                    }

                    isLoading = false
                }

                startupChecked = true
            }

            val scope = rememberCoroutineScope()

            FreeTVIPTVTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {

                    if (isLoading) {

                        LoadingScreen()

                    }else {
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
                                            currentScreen = AppScreen.SETTINGS
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

                                        DataStoreManager.savePlaylistUrl(
                                            context,
                                            url
                                        )

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

                        AppScreen.SETTINGS -> {

                            BackHandler {
                                currentScreen = AppScreen.MENU
                            }

                            SettingsScreen(
                                onChangePlaylist = {
                                    currentScreen = AppScreen.URL_INPUT
                                },
                                onClearPlaylist = {

                                    scope.launch {

                                        DataStoreManager.clearPlaylistUrl(
                                            context
                                        )

                                        channels = emptyList()

                                        selectedChannel = null

                                        currentScreen = AppScreen.URL_INPUT
                                    }
                                }
                            )
                        }
                    }
                }
                }
            }
        }
    }
}