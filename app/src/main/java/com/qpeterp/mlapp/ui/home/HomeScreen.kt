package com.qpeterp.mlapp.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.qpeterp.mlapp.ui.theme.MLAppTheme

@Composable
fun HomeScreen() {
    MLAppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 46.dp)
        ) {
            // TODO: 운동 아이콘으로 나중에 바꾸기
            HomeCard(onClick = { /** TODO: */ }, label = "운동 횟수 카운트", icon = Icons.Outlined.Add)
            HomeCard(onClick = { /** TODO: */ }, label = "다른 ML Kit 기능", icon = Icons.Outlined.Search)
        }
    }
}

@Composable
fun HomeCard(
    onClick: () -> Unit,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {

}