package com.qpeterp.mlapp.domain.usecase.action

import android.content.Context
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import com.qpeterp.mlapp.domain.model.action.ExerciseType
import com.qpeterp.mlapp.domain.model.action.PoseType
import com.qpeterp.mlapp.domain.model.action.TargetPose
import com.qpeterp.mlapp.domain.model.action.TargetShape
import com.qpeterp.mlapp.presentation.viewmodel.action.ActionViewModel
import com.qpeterp.mlapp.utils.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ExerciseClassification(
    private val actionViewModel: ActionViewModel,
    context: Context
) {
    private val phoneOrientationDetector = PhoneOrientationDetector(context = context)
    private val targetSquatMovePose: TargetPose = TargetPose(
        listOf(
            TargetShape(
                PoseLandmark.LEFT_ANKLE, PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_HIP, 95.0
            ),
            TargetShape(
                PoseLandmark.RIGHT_ANKLE, PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_HIP, 95.0
            ),
        )
    )
    private val targetSquatBasicPose: TargetPose = TargetPose(
        listOf(
            TargetShape(
                PoseLandmark.LEFT_ANKLE, PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_HIP, 180.0
            ),
            TargetShape(
                PoseLandmark.RIGHT_ANKLE, PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_HIP, 180.0
            ),
        )
    )
    private val targetPushUpBasicPose: TargetPose = TargetPose(
        listOf(
            TargetShape(
                PoseLandmark.RIGHT_WRIST,
                PoseLandmark.RIGHT_ELBOW,
                PoseLandmark.RIGHT_SHOULDER,
                160.0
            ),
            TargetShape(
                PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_HEEL, 170.0
            ),
            TargetShape(
                PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP, 90.0
            ),
        )
    )
    private val targetPushUpMovePose: TargetPose = TargetPose(
        listOf(
            TargetShape(
                PoseLandmark.RIGHT_WRIST,
                PoseLandmark.RIGHT_ELBOW,
                PoseLandmark.RIGHT_SHOULDER,
                80.0
            ),
            TargetShape(
                PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_HEEL, 170.0
            ),
        )
    )
    private var isSquatDownTimeValid = false // 유지 여부 체크
    private var squatJob: Job? = null
    private val poseMatcher = PoseMatcher()
    private lateinit var movePose: TargetPose
    private lateinit var basicPose: TargetPose
    private val onPoseDetected: (pose: Pose, exerciseType: ExerciseType) -> Unit = { pose, exerciseType ->
        when (exerciseType) {
            ExerciseType.SQUAT -> {
                movePose = targetSquatMovePose
                basicPose = targetSquatBasicPose
            }
            ExerciseType.PUSH_UP -> {
                movePose = targetPushUpMovePose
                basicPose = targetPushUpBasicPose
            }
        }
        val isMovePose = poseMatcher.match(pose, movePose)
        val isBasicPose = poseMatcher.match(pose, basicPose)
        when {
            isMovePose -> {
                if (actionViewModel.squatState.value == PoseType.DOWN || actionViewModel.squatState.value == PoseType.DISHEVELED) {
                    actionViewModel.setSquatState(PoseType.MAINTAIN)
                    squatJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(3000)
                        isSquatDownTimeValid = true
                        actionViewModel.setSquatState(PoseType.UP)
                    }
                }
            }

            isBasicPose -> {
                if (actionViewModel.squatState.value == PoseType.UP) {
                    if (isSquatDownTimeValid) {
                        actionViewModel.addCount()
                        actionViewModel.setSquatState(PoseType.DOWN)
                    }
                } else {
                    if (actionViewModel.squatState.value != PoseType.DOWN) {
                        actionViewModel.setSquatState(PoseType.DISHEVELED)
                    }
                }
                // 상태 초기화
                isSquatDownTimeValid = false // 카운트가 올라가면 다시 false로 리셋
                squatJob?.cancel() // 타이머 취소
            }
        }
    }

    fun classifyExercise(pose: Pose) {
        val exerciseType = actionViewModel.exerciseType.value
        when(exerciseType) {
            ExerciseType.SQUAT -> {
                if (pose.getPoseLandmark(25) == null || pose.getPoseLandmark(26) == null) return
                if (pose.getPoseLandmark(25)!!.inFrameLikelihood < 0.92 || pose.getPoseLandmark(26)!!.inFrameLikelihood < 0.92) return
                if (!phoneOrientationDetector.verticalHilt) return // check phone inclination horizontal
            }
            ExerciseType.PUSH_UP -> {
                if (pose.getPoseLandmark(14) == null || pose.getPoseLandmark(30) == null) return
                if (pose.getPoseLandmark(14)!!.inFrameLikelihood < 0.92 || pose.getPoseLandmark(30)!!.inFrameLikelihood < 0.92) return
                if (phoneOrientationDetector.verticalHilt) return // check phone inclination vertical
            }
            else -> {
                return
            }
        }

        log("현재 검출하려하는 포즈는 ${exerciseType.label} 입니다.")

        onPoseDetected(pose, exerciseType)
    }
}