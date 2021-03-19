package com.hus

import androidx.annotation.StringRes


sealed class ButtonState(@StringRes internal val selectARepository: Int) {
    object Clicked : ButtonState(R.string.select_a_repository)
    object Loading : ButtonState(R.string.loading_in_progress)
    object Completed : ButtonState(R.string.loading_completed)
    object Inititial : ButtonState(R.string.select_a_repository)

    object Failure : ButtonState(R.string.failure)
}