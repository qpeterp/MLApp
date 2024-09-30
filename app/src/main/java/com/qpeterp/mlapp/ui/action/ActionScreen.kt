package com.qpeterp.mlapp.ui.action

import android.content.Context
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qpeterp.mlapp.R
import com.qpeterp.mlapp.data.action.PoseType
import com.qpeterp.mlapp.utils.logE
import com.qpeterp.mlapp.viewmodel.action.ActionViewModel
import com.qpeterp.mlapp.viewmodel.action.ActionViewModelFactory
import com.qpeterp.mlapp.viewmodel.action.ImageAnalyzer
import com.qpeterp.mlapp.viewmodel.action.rememberTextToSpeech
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private lateinit var actionViewModel: ActionViewModel

@Composable
fun ActionScreen(modifier: Modifier = Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // 같은 타입 적어놓으면 에러 터짐. 내 어이도 같이 터짐 이게 맞냐?
    actionViewModel = viewModel(factory = ActionViewModelFactory())
    // count의 LiveData를 Compose 상태로 변환
    val count = actionViewModel.count.observeAsState(initial = 0)
    val squatState = actionViewModel.squatState.observeAsState(initial = 0)
    val isSpeaking = remember { mutableStateOf(false) }
    val tts = rememberTextToSpeech()

    isSpeaking.value = if (tts.value?.isSpeaking == true) {
        tts.value?.stop()
         false
    } else {
        tts.value?.speak(
            when(squatState.value) {
                PoseType.UP -> "올라가"
                PoseType.DOWN -> "내려가"
                PoseType.MAINTAIN -> "유지해"
                PoseType.DISHEVELED -> "다시앉아"
                else -> "오류발생"
            },
            TextToSpeech.QUEUE_FLUSH,
            null,
            ""
        )
        true
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(modifier = modifier.fillMaxWidth()) {
            AndroidView(
                factory = { context ->
                    val view = LayoutInflater.from(context).inflate(R.layout.screen_camera, null)
                    val previewView = view.findViewById<PreviewView>(R.id.previewView)
                    val cameraExecutor = Executors.newSingleThreadExecutor()

                    startCamera(
                        previewView = previewView,
                        context = context,
                        lifecycleOwner = lifecycleOwner,
                        cameraExecutor = cameraExecutor
                    )
                    view
                },
                modifier = modifier
                    .aspectRatio(9.5f / 16f) // 너비 비율에 맞춰서 높이 조절
            )
            Text(
                text = when(squatState.value) {
                    PoseType.UP -> "올라가"
                    PoseType.DOWN -> "내려가"
                    PoseType.MAINTAIN -> "유지해"
                    PoseType.DISHEVELED -> "다시앉아"
                    else -> "오류발생"
                },
                color = Color.Red,
                fontSize = 28.sp,
                modifier = modifier.align(Alignment.Center)
            )
        }
        Row(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.Absolute.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "의미 없이 그냥 예쁘라고 넣은 아이콘",
                tint = Color.Yellow,
                modifier = modifier.padding(start = 20.dp, end = 80.dp)
            )
            Text(
                text = "횟수 : ",
                color = Color.White,
                fontSize = 24.sp
            )
            Text(
                text = count.value.toString(),
                color = Color.White,
                fontSize = 24.sp,
            )
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "의미 없이 그냥 예쁘라고 넣은 아이콘",
                tint = Color.Yellow,
                modifier = modifier.padding(start = 80.dp, end = 20.dp)
            )
        }
    }
}

private fun startCamera(
    previewView: PreviewView,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    cameraExecutor: ExecutorService,
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) // 최신 이미지만 유지
            .build().also {
                it.setAnalyzer(cameraExecutor, ImageAnalyzer(actionViewModel)) // 분석기 설정
            }

        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis // 이미지 분석기도 함께 바인딩
            )
        } catch (e: Exception) {
            logE("카메라 초기화 중 에러 발생: ${e.localizedMessage}")
        }

    }, ContextCompat.getMainExecutor(context))
}