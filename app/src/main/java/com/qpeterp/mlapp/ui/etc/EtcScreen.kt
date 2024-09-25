package com.qpeterp.mlapp.ui.etc

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qpeterp.mlapp.ui.theme.MLAppTheme

@Composable
fun EtcScreen() {
    MLAppTheme {
        Scaffold(modifier = Modifier.height(520.dp)) { innerPadding ->
            Greeting2(
                name = "애송이",
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "지옥에 온걸 환영한다 $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    MLAppTheme {
        Greeting2("애송이")
    }
}