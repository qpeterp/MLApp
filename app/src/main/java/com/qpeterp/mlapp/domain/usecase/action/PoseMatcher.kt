package com.qpeterp.mlapp.domain.usecase.action

import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import com.qpeterp.mlapp.domain.model.action.TargetPose
import com.qpeterp.mlapp.domain.model.action.TargetShape
import com.qpeterp.mlapp.utils.log
import kotlin.math.abs
import kotlin.math.atan2

class PoseMatcher {
    companion object {
        private const val OFFSET = 15.0
    }

    fun match(pose: Pose, targetPose: TargetPose): Boolean {
        targetPose.targets.forEach { target ->
            val (firstLandmark, middleLandmark, lastLandmark) = extractLandmark(pose, target)
            //Check landmark is null
            if (landmarkNotFound(firstLandmark, middleLandmark, lastLandmark)) {
                return false
            }
            val angle = calculateAngle(firstLandmark!!, middleLandmark!!, lastLandmark!!)
            val targetAngle = target.angle
            log("${firstLandmark.landmarkType} $angle !!!!!!!!!!!!!!!")
            if (!anglesMatch(angle, targetAngle)) {
                return false
            }
        }
        return true
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

    private fun landmarkNotFound(
        firstLandmark: PoseLandmark?,
        middleLandmark: PoseLandmark?,
        lastLandmark: PoseLandmark?
    ): Boolean {
        return firstLandmark == null || middleLandmark == null || lastLandmark == null
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

        var absoluteAngle = abs(angle)

        if (absoluteAngle > 180) {
            absoluteAngle = 360 - absoluteAngle
        }
        return absoluteAngle
    }

    private fun anglesMatch(angle: Double, targetAngle: Double): Boolean {
        return angle < targetAngle + OFFSET && angle > targetAngle - OFFSET
    }
}