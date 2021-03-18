package com.hus


sealed class ButtonState(selectARepository: Int) {
    object Clicked : ButtonState(R.string.select_a_repository)
    object Loading : ButtonState(R.string.loading_in_progress)
    object Completed : ButtonState(R.string.loading_completed)
    object Inititial : ButtonState(R.string.select_a_repository)
}