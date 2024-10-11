package com.qpeterp.mlapp.presentation.view.action

import android.content.Context
import android.speech.tts.TextToSpeech
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qpeterp.mlapp.domain.model.action.ExerciseType
import com.qpeterp.mlapp.presentation.viewmodel.action.ActionViewModel
import com.qpeterp.mlapp.presentation.viewmodel.action.ActionViewModelFactory
import com.qpeterp.mlapp.utils.logE
import com.qpeterp.mlapp.domain.usecase.action.ImageAnalyzer
import com.qpeterp.mlapp.domain.usecase.action.PhoneOrientationDetector
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private lateinit var phoneOrientationDetector: PhoneOrientationDetector // TODO: viewModel 이나 다른 곳으로 옯겨서 관리.

@Composable
fun ActionScreen(
    modifier: Modifier = Modifier,
    actionViewModel: ActionViewModel = viewModel(factory = ActionViewModelFactory())
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // count의 LiveData를 Compose 상태로 변환
    val count = actionViewModel.count.observeAsState()
    val squatState = actionViewModel.squatState.observeAsState()
    val tts = rememberTextToSpeech()
    val isSpeaking = remember { mutableStateOf(false) }
    val selectedOption = remember { mutableStateOf(ExerciseType.SQUAT) }

    isSpeaking.value = if (tts.value?.isSpeaking == true) {
        tts.value?.stop()
        false
    } else {
        tts.value?.speak(
            squatState.value?.message,
            TextToSpeech.QUEUE_FLUSH, // TODO: 이거 뭐하는건지 확인 ㄱㄱ
            null,
            ""
        )
        true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            AndroidView(
                factory = { context ->
                    val preview = PreviewView(context).apply {
                        layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
                        layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
                    }
                    val cameraExecutor = Executors.newSingleThreadExecutor()

                    startCamera(
                        previewView = preview,
                        context = context,
                        lifecycleOwner = lifecycleOwner,
                        cameraExecutor = cameraExecutor,
                        actionViewModel = actionViewModel
                    )
                    preview
                },
                modifier = modifier
                    .fillMaxSize()
            ) {}

            Text(
                text = squatState.value?.message ?: "오류발생",
                color = Color.Red,
                fontSize = 28.sp,
                modifier = modifier.align(Alignment.Center)
            )
        }
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "의미 없이 그냥 예쁘라고 넣은 아이콘",
                tint = Color.Yellow
            )
            Text(
                text = "횟수 : ${count.value}",
                color = Color.White,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "의미 없이 그냥 예쁘라고 넣은 아이콘",
                tint = Color.Yellow
            )
        }
    }

    Column(
        Modifier
            .background(Color(0x55FFFF00), shape = RoundedCornerShape(16.dp))
            .padding(end = 20.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedOption.value == ExerciseType.SQUAT,
                onClick = {
                    selectedOption.value = ExerciseType.SQUAT
                    actionViewModel.setExerciseType(ExerciseType.SQUAT)
                },
                colors = RadioButtonColors(
                    selectedColor = Color.Yellow,
                    unselectedColor = Color.Yellow,
                    disabledSelectedColor = Color.Yellow,
                    disabledUnselectedColor = Color.Yellow
                )
            )
            Text(ExerciseType.SQUAT.label)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedOption.value == ExerciseType.PUSH_UP,
                onClick = {
                    selectedOption.value = ExerciseType.PUSH_UP
                    actionViewModel.setExerciseType(ExerciseType.PUSH_UP)
                },
                colors = RadioButtonColors(
                    selectedColor = Color.Yellow,
                    unselectedColor = Color.Yellow,
                    disabledSelectedColor = Color.Yellow,
                    disabledUnselectedColor = Color.Yellow
                )
            )
            Text(ExerciseType.PUSH_UP.label)
        }
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            phoneOrientationDetector.unregister()
        }
    }
}

private fun startCamera(
    previewView: PreviewView,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    cameraExecutor: ExecutorService,
    actionViewModel: ActionViewModel
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    phoneOrientationDetector = PhoneOrientationDetector(context)

    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) // 최신 이미지만 유지
            .build().also {
                it.setAnalyzer(
                    cameraExecutor,
                    ImageAnalyzer(actionViewModel, context)
                ) // 분석기 설정
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