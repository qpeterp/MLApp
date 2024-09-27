package com.qpeterp.mlapp.viewmodel.action

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActionViewModel : ViewModel() {
    private val _count = MutableLiveData<Int>(0)
    private val _isSquatDown = MutableLiveData<Boolean>(true)

    val count: LiveData<Int>
        get() = _count

    val isSquatDown: LiveData<Boolean>
        get() = _isSquatDown

    fun addCount() {
        _count.value = _count.value!! + 1
    }

    fun squatDownState() {
        _isSquatDown.value = _isSquatDown.value!!.not() // _isSquatDown is init true
    }
}