package com.qpeterp.mlapp.ui.action

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.qpeterp.mlapp.utils.log

class ImageAnalyzer : ImageAnalysis.Analyzer {
    private val actionViewModel = ActionViewModel()

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return

        val image: InputImage =
            InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        actionViewModel.poseDetector.process(image)
            .addOnSuccessListener { results ->
                // 분석이 성공적으로 완료되면 결과 처리
                log("image result is :\n\n ${results.allPoseLandmarks}")
                val landmarks = results.allPoseLandmarks // 분석 결과에서 관절 정보 가져오기
                for (landmark in landmarks) {
                    val position = landmark.position // (x, y) 좌표
                    val landmarkType = landmark.landmarkType // 관절의 종류
                    val confidence = landmark.inFrameLikelihood // 확신도

                    log("Landmark Type: $landmarkType, Position: $position, Confidence: $confidence")
                }
            }
            .addOnFailureListener { e ->
                // 분석이 실패했을 때 예외 처리
                log("image error is :\n\n $e")
            }
            .addOnCompleteListener {
                // 이미지 분석이 완료되었음을 CameraX에 알림 (이거 필수)
                imageProxy.close()
            }
    }
}