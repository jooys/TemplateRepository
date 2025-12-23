package com.jooys.template.core.util

class ComposeSingleClickListener {
    private var lastClickedTime: Long = 0L

    private fun checkSingleClick(): Boolean {
        return System.currentTimeMillis() - lastClickedTime > INTERVAL
    }

    fun onSingleClick(onClick: () -> Unit) {
        if (checkSingleClick()) {
            onClick()
        }
        lastClickedTime = System.currentTimeMillis()
    }

    companion object {
        private const val INTERVAL = 500L
    }
}
