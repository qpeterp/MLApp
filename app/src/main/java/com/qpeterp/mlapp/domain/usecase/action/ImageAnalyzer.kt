package com.qpeterp.mlapp.domain.usecase.action

import android.content.Context
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import com.qpeterp.mlapp.presentation.viewmodel.action.ActionViewModel
import com.qpeterp.mlapp.utils.log

class ImageAnalyzer(
    actionViewModel: ActionViewModel, context: Context
) : ImageAnalysis.Analyzer {
    // 영상 포즈 감지
    private val options =
        PoseDetectorOptions.Builder().setDetectorMode(PoseDetectorOptions.STREAM_MODE).build()
    private val poseDetector = PoseDetection.getClient(options)
    private val exerciseClassification = ExerciseClassification(actionViewModel, context)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return

        val image: InputImage =
            InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        poseDetector.process(image)
            .addOnSuccessListener { pose ->
                // 분석이 성공적으로 완료되면 결과 처리
                exerciseClassification.classifyExercise(pose)
            }.addOnFailureListener { e ->
                // 분석이 실패했을 때 예외 처리
                log("image error is :\n\n $e")
            }.addOnCompleteListener {
                // 이미지 분석이 완료되었음을 CameraX에 알림 (이거 필수)
                imageProxy.close()
            }
    }
}