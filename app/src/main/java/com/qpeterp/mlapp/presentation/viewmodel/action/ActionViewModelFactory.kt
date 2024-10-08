package com.qpeterp.mlapp.presentation.viewmodel.action

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ActionViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ActionViewModel::class.java)) {
            ActionViewModel() as T
        } else {
            throw IllegalArgumentException()
        }
    }
}