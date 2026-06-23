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
import com.freetv.iptv.screen.PlaylistsScreen
import com.freetv.iptv.screen.CategoryScreen
import com.freetv.iptv.screen.CategoryChannelsScreen
import com.freetv.iptv.screen.SearchScreen

enum class AppScreen {
    MENU,
    URL_INPUT,
    PLAYLISTS,
    CATEGORIES,
    CATEGORY_CHANNELS,
    SEARCH,
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

            var currentPlaylistUrl by remember {
                mutableStateOf<String?>(null)
            }

            var selectedCategory by remember {
                mutableStateOf<String?>(null)
            }

            var categoryScrollIndex by remember {
                mutableStateOf(0)
            }

            var channelScrollIndex by remember {
                mutableStateOf(0)
            }

            var selectedChannelIndex by remember {
                mutableStateOf(0)
            }

            LaunchedEffect(Unit) {

                val savedUrl =
                    DataStoreManager.getPlaylistUrl(context)
                currentPlaylistUrl = savedUrl

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
                            AppScreen.CATEGORIES
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
                                                currentScreen = AppScreen.CATEGORIES
                                            }
                                        "Search" -> {
                                            currentScreen = AppScreen.SEARCH
                                        }
                                            "Load Playlist" -> {
                                                currentScreen = AppScreen.URL_INPUT
                                            }

                                            "Playlists" -> {
                                                currentScreen = AppScreen.PLAYLISTS
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

                                            currentPlaylistUrl = url

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
                                                    AppScreen.CATEGORIES
                                            }
                                        }
                                    }
                                )
                            }

//                            AppScreen.CHANNELS -> {
//
//                                BackHandler {
//                                    currentScreen = AppScreen.MENU
//                                }
//
//                                HomeScreen(
//                                    channels = channels,
//                                    onChannelSelected = {
//                                        selectedChannel = it
//                                        currentScreen = AppScreen.PLAYER
//                                    }
//                                )
//                            }

                            AppScreen.PLAYER -> {

                                BackHandler {
                                    currentScreen = AppScreen.CATEGORY_CHANNELS
                                }

                                VideoPlayerScreen(
                                    streamUrl = selectedChannel!!.streamUrl
                                )
                            }

                            AppScreen.PLAYLISTS -> {

                                BackHandler {
                                    currentScreen = AppScreen.MENU
                                }

                                PlaylistsScreen(
                                    currentPlaylistUrl = currentPlaylistUrl,
                                    channelCount = channels.size,
                                    onChangePlaylist = {
                                        currentScreen = AppScreen.URL_INPUT
                                    },
                                    onClearPlaylist = {

                                        scope.launch {

                                            DataStoreManager.clearPlaylistUrl(
                                                context
                                            )

                                            currentPlaylistUrl = null

                                            channels = emptyList()

                                            selectedChannel = null

                                            currentScreen = AppScreen.URL_INPUT
                                        }
                                    }
                                )
                            }

                            AppScreen.CATEGORIES -> {

                                BackHandler {
                                    currentScreen = AppScreen.MENU
                                }

                                CategoryScreen(
                                    categories = channels
                                        .map { it.category }
                                        .distinct()
                                        .sorted(),

                                    initialIndex = categoryScrollIndex,

                                    onCategorySelected = { category, index ->

                                        categoryScrollIndex = index

                                        selectedCategory = category
                                        selectedChannelIndex = 0

                                        currentScreen =
                                            AppScreen.CATEGORY_CHANNELS
                                    }
                                )
                            }

                            AppScreen.CATEGORY_CHANNELS -> {

                                BackHandler {
                                    currentScreen =
                                        AppScreen.CATEGORIES
                                }

                                CategoryChannelsScreen(

                                    categoryName = selectedCategory ?: "Unknown",

                                    channels = channels.filter {
                                        it.category == selectedCategory
                                    },

                                    initialIndex = selectedChannelIndex,

                                    onChannelSelected = { channel, index ->

                                        selectedChannelIndex = index

                                        selectedChannel = channel

                                        currentScreen =
                                            AppScreen.PLAYER
                                    }
                                )
                            }
                            AppScreen.SEARCH -> {

                                BackHandler {
                                    currentScreen = AppScreen.MENU
                                }

                                SearchScreen(

                                    channels = channels,

                                    onChannelSelected = {

                                        selectedChannel = it

                                        currentScreen =
                                            AppScreen.PLAYER
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