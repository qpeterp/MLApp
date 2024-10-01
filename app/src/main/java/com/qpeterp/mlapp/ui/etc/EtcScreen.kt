package com.qpeterp.mlapp.ui.etc

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import com.qpeterp.mlapp.ui.common.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qpeterp.mlapp.data.etc.EtcCardData
import com.qpeterp.mlapp.ui.theme.MLAppTheme
import com.qpeterp.mlapp.utils.log

@Composable
fun EtcScreen() {
    val cardDataList = listOf(
        EtcCardData(label = "팔 굽혀 펴기 인식", onClick = {})
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
                    EtcCard(
                        label = cardData.label,
                        modifier = Modifier.padding(20.dp),
                    ) {
                        log("asdfasdfasfsdfsfasdffdsf")
                    }
                }
            }
        }
    }
}

@Composable
fun EtcCard(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        backgroundColor = Color.DarkGray,
        horizontalPadding = 20,
        onClick = onClick
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable { onClick() },
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = "It mean build or tools",
                tint = Color.Yellow
            )
            Text(
                text = label,
                fontSize = 24.sp,
                color = Color.White
            )
        }
    }
}
