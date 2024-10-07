package com.qpeterp.mlapp.presentation.viewmodel.action

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.qpeterp.mlapp.domain.model.action.PoseType

class ActionViewModel : ViewModel() {
    private val _count = MutableLiveData<Int>(0)
    private val _squatState = MutableLiveData<PoseType>(PoseType.DOWN)

    val count: LiveData<Int>
        get() = _count

    val squatState: LiveData<PoseType>
        get() = _squatState

    fun addCount() {
        _count.value = _count.value!! + 1
    }

    fun setSquatState(poseType: PoseType) {
        _squatState.value = poseType
    }
}