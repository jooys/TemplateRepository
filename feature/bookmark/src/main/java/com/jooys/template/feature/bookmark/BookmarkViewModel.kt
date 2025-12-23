package com.jooys.template.feature.bookmark

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
class BookmarkViewModel @Inject constructor(
    private val mutateHandler: BookmarkMutateHandler,
) : ViewModel() {
    private val _state = MutableStateFlow(BookmarkMutate.State())
    val state: StateFlow<BookmarkMutate.State> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<BookmarkMutate.SideEffect>()
    val sideEffect: SharedFlow<BookmarkMutate.SideEffect> = _sideEffect.asSharedFlow()

    fun onAction(action: BookmarkAction) {
        mutateHandler.mutate(action).onEach { mutate ->
            when (mutate) {
                is BookmarkMutate.Reduce -> {
                    when (mutate) {
                        is BookmarkMutate.Reduce.UpdateViewState -> {
                            _state.update { it.copy(viewState = mutate.viewState) }
                        }

                        is BookmarkMutate.Reduce.UpdateImageList -> {
                            _state.update { it.copy(imageList = mutate.imageList) }
                        }

                        is BookmarkMutate.Reduce.UpdateLoading -> {
                            _state.update { it.copy(isLoading = mutate.isLoading) }
                        }
                    }
                }

                is BookmarkMutate.SideEffect -> {
                    _sideEffect.emit(mutate)
                }
            }
        }.launchIn(viewModelScope)
    }
}
