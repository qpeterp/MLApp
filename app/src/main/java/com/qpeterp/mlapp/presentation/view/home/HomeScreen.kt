package com.qpeterp.mlapp.presentation.view.home

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.qpeterp.mlapp.domain.model.home.HomeCardData
import com.qpeterp.mlapp.presentation.view.common.Card
import com.qpeterp.mlapp.presentation.view.theme.MLAppTheme

@Composable
fun HomeScreen() {
    val cardDataList = listOf(
        HomeCardData(label = "MVVM 아키텍처란?", url = "https://learn.microsoft.com/en-us/dotnet/architecture/maui/mvvm#the-mvvm-pattern"),
        HomeCardData(label = "Google ML Kit 이란?", url = "https://developers.google.com/ml-kit/guides?hl=ko"),
        HomeCardData(label = "Pose Detector 란?", url = "https://developers.google.com/ml-kit/vision/pose-detection?hl=ko")
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
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
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

            AnimatedVisibility(
                visible = informationState.value,
                enter = fadeIn() + expandIn(),
                exit = fadeOut() + shrinkVertically(),
            ) {
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
        }
    }, update = {
        it.loadUrl(url)
    })
}