package com.freetv.iptv.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.tv.material3.Card
import androidx.tv.material3.Text
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.itemsIndexed

@Composable
fun CategoryScreen(
    categories: List<String>,
    initialIndex: Int,
    onCategorySelected: (String, Int) -> Unit
){

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex
    )

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {

        itemsIndexed(categories) { index, category ->

            Card(
                onClick = {
                    onCategorySelected(category, index)
                }
            ) {

                Text(
                    text = category
                )
            }
        }
    }
}