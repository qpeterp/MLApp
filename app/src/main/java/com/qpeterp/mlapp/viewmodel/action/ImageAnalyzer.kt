package com.qpeterp.mlapp.viewmodel.action

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import com.qpeterp.mlapp.data.action.TargetPose
import com.qpeterp.mlapp.data.action.TargetShape
import com.qpeterp.mlapp.utils.log

class ImageAnalyzer(actionViewModel: ActionViewModel) : ImageAnalysis.Analyzer {
    // 영상 포즈 감지
    private val options = PoseDetectorOptions.Builder()
        .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
        .build()
    private val poseDetector = PoseDetection.getClient(options)
    private val targetSquatDownPose: TargetPose = TargetPose(
        listOf(
            TargetShape(
                PoseLandmark.LEFT_ANKLE, PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_HIP, 85.0
            ),
            TargetShape(
                PoseLandmark.RIGHT_ANKLE, PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_HIP, 85.0
            ),
        )
    )
    private val targetSquatUpPose: TargetPose = TargetPose(
        listOf(
            TargetShape(
                PoseLandmark.LEFT_ANKLE, PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_HIP, 180.0
            ),
            TargetShape(
                PoseLandmark.RIGHT_ANKLE, PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_HIP, 180.0
            ),
        )
    )
    private val poseMatcher = PoseMatcher()
    private val onPoseDetected: (pose: Pose) -> Unit = { pose ->
        val isSquatDown = poseMatcher.match(pose, targetSquatDownPose)
        val isSquatUp = poseMatcher.match(pose, targetSquatUpPose)
        when {
            isSquatDown -> {
                if (actionViewModel.isSquatDown.value == true) {
                    actionViewModel.squatDownState()
                }
            }
            isSquatUp -> {
                if (actionViewModel.isSquatDown.value == false) {
                    log("!!!!!!스쿼트 횟수 : ${actionViewModel.count.value}")
                    actionViewModel.addCount()
                    actionViewModel.squatDownState()
                }
            }
        }
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return

        val image: InputImage =
            InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        poseDetector.process(image)
            .addOnSuccessListener { pose ->
                // 분석이 성공적으로 완료되면 결과 처리
                if (pose.getPoseLandmark(25) == null || pose.getPoseLandmark(26) == null) return@addOnSuccessListener
                if (pose.getPoseLandmark(25)!!.inFrameLikelihood < 0.9 || pose.getPoseLandmark(26)!!.inFrameLikelihood < 0.9) return@addOnSuccessListener // if pose.getPoseLandmark(25) null, i will die

                onPoseDetected(pose)
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