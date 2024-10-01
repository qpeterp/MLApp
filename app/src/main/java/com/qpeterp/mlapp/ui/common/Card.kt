package com.qpeterp.mlapp.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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