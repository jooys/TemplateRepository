package com.jooys.template.core_design.component.compose

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class SnackBarHandler @Inject constructor() {

    private var job: Job? = null

    private val _snackBarState = MutableStateFlow(SnackBarState())
    val snackBarState = _snackBarState.asStateFlow()

    fun show(message: String) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            _snackBarState.update {
                it.copy(
                    message = message,
                    visible = true
                )
            }
            delay(1_500L)
            _snackBarState.update { it.copy(visible = false) }
        }
    }

    fun dismiss() {
        job?.cancel()
        _snackBarState.update { it.copy(visible = false) }
    }

    fun showForever(message: String) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            _snackBarState.update {
                it.copy(
                    message = message,
                    visible = true
                )
            }
        }
    }
}

data class SnackBarState(
    val message: String = "",
    val visible: Boolean = false
)
