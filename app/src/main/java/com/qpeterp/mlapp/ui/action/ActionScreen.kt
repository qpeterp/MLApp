package com.qpeterp.mlapp.ui.action

import android.content.Context
import android.view.LayoutInflater
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.qpeterp.mlapp.R
import com.qpeterp.mlapp.utils.logE
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private val actionViewModel = ActionViewModel()

@Composable
fun ActionScreen(modifier: Modifier = Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current

    Column(modifier = modifier.fillMaxWidth()) {
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
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}

private fun startCamera(
    previewView: PreviewView,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    cameraExecutor: ExecutorService
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
                it.setAnalyzer(cameraExecutor, ImageAnalyzer()) // 분석기 설정
            }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

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