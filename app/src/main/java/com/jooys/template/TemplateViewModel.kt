package com.jooys.template

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TemplateViewModel @Inject constructor(
    private val mutateHandler: TemplateMutateHandler
) : ViewModel() {
    private val _state = MutableStateFlow(TemplateMutate.State())
    val state: StateFlow<TemplateMutate.State> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<TemplateMutate.SideEffect>()
    val sideEffect: SharedFlow<TemplateMutate.SideEffect> = _sideEffect.asSharedFlow()

    fun onAction(action: TemplateAction) {
        mutateHandler.mutate(action).onEach { mutate ->
            when (mutate) {
                is TemplateMutate.Reduce -> {
                    when (mutate) {
                        is TemplateMutate.Reduce.UpdateTitle -> {
                            _state.update { it.copy(title = mutate.title) }
                        }
                    }
                }

                is TemplateMutate.SideEffect -> {
                    _sideEffect.emit(mutate)
                }
            }
        }.launchIn(viewModelScope)
    }
}
