package com.qpeterp.mlapp.ui.home

import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ScrollView
import android.widget.Scroller
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.qpeterp.mlapp.data.home.HomeCardData
import com.qpeterp.mlapp.ui.theme.MLAppTheme

@Composable
fun HomeScreen() {
    val cardDataList = listOf(
        HomeCardData(label = "MVVM 아키텍처란?", url = "https://learn.microsoft.com/en-us/dotnet/architecture/maui/mvvm#the-mvvm-pattern"),
        HomeCardData(label = "Google ML Kit 이란?", url = "https://www.youtube.com/watch?v=dQw4w9WgXcQ")
    )
    MLAppTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 46.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            cardDataList.map { cardData ->
                item {
                    HomeCard(
                        label = cardData.label,
                        url = cardData.url
                    )
                }
            }
        }
    }
}

@Composable
fun HomeCard(
    label: String,
    url: String,
    modifier: Modifier = Modifier
) {
    val informationState = remember { mutableStateOf(false) }

    Card(backgroundColor = Color.DarkGray,
        horizontalPadding = 20, onClick = {
            informationState.value = !informationState.value
        }
    ) {
        Column(
            modifier = modifier.padding(20.dp),
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = if (informationState.value) "!" else "?",
                    fontSize = 28.sp,
                    color = Color.Yellow
                )
                Text(
                    text = label,
                    fontSize = 24.sp,
                    color = Color.White
                )
            }

            AnimatedVisibility(visible = informationState.value) {
                Column{
                    Spacer(modifier = modifier.height(16.dp))
                    WebView(
                        url = url
                    )
                }
            }
        }
    }
}

@Composable
fun WebView(
    url: String
) {
    AndroidView(factory = {
        WebView(it).apply {
            this.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
            this.webViewClient = CustomWebViewClient()
        }
    }, update = {
        it.loadUrl(url)
    })
}

@Composable
fun Card(
    backgroundColor: Color,
    horizontalPadding: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = horizontalPadding.dp)
        .background(
            color = backgroundColor, shape = RoundedCornerShape(16.dp)
        )
        .clickable { onClick() }) {
        content()
    }
}