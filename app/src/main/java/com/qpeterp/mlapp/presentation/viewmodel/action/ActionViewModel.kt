package com.qpeterp.mlapp.presentation.viewmodel.action

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.qpeterp.mlapp.domain.model.action.ExerciseType
import com.qpeterp.mlapp.domain.model.action.PoseType

class ActionViewModel : ViewModel() {
    private val _count = MutableLiveData<Int>(0)
    private val _squatState = MutableLiveData<PoseType>(PoseType.DOWN)
    private val _exerciseType = MutableLiveData<ExerciseType>(ExerciseType.SQUAT)

    val count: LiveData<Int>
        get() = _count

    val squatState: LiveData<PoseType>
        get() = _squatState

    val exerciseType: LiveData<ExerciseType>
        get() = _exerciseType

    fun addCount() {
        _count.value = _count.value!! + 1
    }

    fun setSquatState(poseType: PoseType) {
        _squatState.value = poseType
    }

    fun setExerciseType(exerciseType: ExerciseType) {
        _exerciseType.value = exerciseType
    }
}