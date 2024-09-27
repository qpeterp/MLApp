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
import kotlin.math.abs
import kotlin.math.atan2

class ImageAnalyzer(actionViewModel: ActionViewModel) : ImageAnalysis.Analyzer {
    // 영상 포즈 감지
    private val options = PoseDetectorOptions.Builder()
        .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
        .build()
    private val poseDetector = PoseDetection.getClient(options)
    private val targetSquatDownPose: TargetPose = TargetPose(
        listOf(
            TargetShape(
                PoseLandmark.LEFT_ANKLE, PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_HIP, 90.0
            ),
            TargetShape(
                PoseLandmark.RIGHT_ANKLE, PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_HIP, 90.0
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
                    log("스쿼트 앉았다!!!!!!!!!!!!")
                    actionViewModel.squatDownState()
                }
            }
            isSquatUp -> {
                if (actionViewModel.isSquatDown.value == false) {
                    log("섰1다!!!!!!!!!!!!")
                    log("!!!!!!스쿼트 횟수 : ${actionViewModel.count.value}")
                    actionViewModel.addCount()
                    actionViewModel.squatDownState()
                }
            }
        }
    }

//    companion object {
//        private const val offset = 15.0
//    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return

        val image: InputImage =
            InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        poseDetector.process(image)
            .addOnSuccessListener { pose ->
                // 분석이 성공적으로 완료되면 결과 처리
                log("image result is :\n\n ${pose.allPoseLandmarks}")
                onPoseDetected(pose)

//                targetPose.targets.forEach { target ->
//                    val (firstLandmark, middleLandmark, lastLandmark) = extractLandmark(
//                        pose,
//                        target
//                    )
//
//                    if (landmarkNotFound(firstLandmark, middleLandmark, lastLandmark)) return@addOnSuccessListener
//
//                    val angle = calculateAngle(firstLandmark!!, middleLandmark!!, lastLandmark!!) // landmarkNotFound remove null
//                    targetPose.targets.forEach { target ->
//                        if (abs(angle - target.angle) > offset) {
//                            return@addOnSuccessListener
//                        }
//                    }
//
//                    // 인식이 제대로 되면 실행되는 곳
//
//                    private val onPoseDetected: (pose: Pose) -> Unit = { pose ->
//                        val isOSign = poseMatcher.match(pose, targetPoseOSign)
//                        val isXSign = poseMatcher.match(pose, targetPoseXSign)
//                        when {
//                            isOSign -> {
//                                //detect O Sign
//                            }
//                            isXSign -> {
//                                //detect X Sign
//                            }
//                        }
//                    }
//                }
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

    private fun calculateAngle(
        firstLandmark: PoseLandmark,
        middleLandmark: PoseLandmark,
        lastLandmark: PoseLandmark
    ): Double {
        val angle = Math.toDegrees(
            (atan2(
                lastLandmark.position.y - middleLandmark.position.y,
                lastLandmark.position.x - middleLandmark.position.x
            ) - atan2(
                firstLandmark.position.y - middleLandmark.position.y,
                firstLandmark.position.x - middleLandmark.position.x
            )).toDouble()
        )
        val absoluteAngle = abs(angle)
//        if (absoluteAngle > 180) {
//            absoluteAngle = 360 - absoluteAngle
//        }
        return absoluteAngle
    }

    private fun landmarkNotFound(firstLandmark: PoseLandmark?, middleLandmark: PoseLandmark?, lastLandmark: PoseLandmark?): Boolean {
        return if (firstLandmark == null || middleLandmark == null || lastLandmark == null) true else false
    }

    private fun extractLandmark(
        pose: Pose,
        target: TargetShape
    ): Triple<PoseLandmark?, PoseLandmark?, PoseLandmark?> {
        return Triple(
            extractLandmarkFromType(pose, target.firstLandmarkType),
            extractLandmarkFromType(pose, target.middleLandmarkType),
            extractLandmarkFromType(pose, target.lastLandmarkType)
        )
    }

    private fun extractLandmarkFromType(pose: Pose, landmarkType: Int): PoseLandmark? {
        return pose.getPoseLandmark(landmarkType)
    }
}