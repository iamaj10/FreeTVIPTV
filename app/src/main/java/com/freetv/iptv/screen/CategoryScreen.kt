package com.freetv.iptv.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.tv.material3.Card
import androidx.tv.material3.Text

@Composable
fun CategoryScreen(
    categories: List<String>,
    onCategorySelected: (String) -> Unit
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {

        items(categories) { category ->

            Card(
                onClick = {
                    onCategorySelected(category)
                }
            ) {

                Text(
                    text = category
                )
            }
        }
    }
}