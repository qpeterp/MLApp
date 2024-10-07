package com.qpeterp.mlapp.domain.model.action

enum class PoseType(
    val message: String
) {
    UP("올라가"),
    DOWN("내려가"),
    MAINTAIN("유지해"),
    DISHEVELED("다시앉아");
}