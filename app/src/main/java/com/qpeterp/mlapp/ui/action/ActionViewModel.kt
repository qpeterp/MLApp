package com.qpeterp.mlapp.ui.action

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions

class ActionViewModel : ViewModel() {
    // 영상 포즈 감지
    private val options = PoseDetectorOptions.Builder()
        .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
        .build()

    val poseDetector = PoseDetection.getClient(options)

    private val _count = MutableLiveData<Int>(0)

    val count: LiveData<Int>
        get() = _count

    fun addCount() {
        _count.value = _count.value!! + 1
    }
}