package com.jooys.template.feature.detail

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
class DetailViewModel @Inject constructor(
    private val mutateHandler: DetailMutateHandler,
) : ViewModel() {
    private val _state = MutableStateFlow(DetailMutate.State())
    val state: StateFlow<DetailMutate.State> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<DetailMutate.SideEffect>()
    val sideEffect: SharedFlow<DetailMutate.SideEffect> = _sideEffect.asSharedFlow()

    fun onAction(action: DetailAction) {
        mutateHandler.mutate(action).onEach { mutate ->
            when (mutate) {
                is DetailMutate.Reduce -> {
                    when (mutate) {
                        is DetailMutate.Reduce.UpdateLoading -> {
                            _state.update { it.copy(isLoading = mutate.isLoading) }
                        }

                        is DetailMutate.Reduce.UpdateViewState -> {
                            _state.update { it.copy(viewState = mutate.viewState) }
                        }

                        is DetailMutate.Reduce.UpdateImageData -> {
                            _state.update { it.copy(imageData = mutate.imageData) }
                        }
                        is DetailMutate.Reduce.UpdateIsBookmark -> {
                            _state.update { it.copy(imageData = it.imageData.copy(isBookmark = mutate.isBookmark)) }
                        }
                    }
                }

                is DetailMutate.SideEffect -> {
                    _sideEffect.emit(mutate)
                }
            }
        }.launchIn(viewModelScope)
    }
}
