package com.qpeterp.mlapp.utils

import android.util.Log
import com.qpeterp.mlapp.common.Constant

fun log(message: String) {
    Log.d(Constant.TAG, message)
}

fun logE(message: String) {
    Log.e(Constant.TAG, message)
}

fun logI(message: String) {
    Log.i(Constant.TAG, message)
}
