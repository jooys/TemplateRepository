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
class SearchViewModel @Inject constructor(
    private val mutateHandler: SearchMutateHandler,
) : ViewModel() {
    private val _state = MutableStateFlow(SearchMutate.State())
    val state: StateFlow<SearchMutate.State> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SearchMutate.SideEffect>()
    val sideEffect: SharedFlow<SearchMutate.SideEffect> = _sideEffect.asSharedFlow()

    fun onAction(action: SearchAction) {
        mutateHandler.mutate(action, _state.value).onEach { mutate ->
            when (mutate) {
                is SearchMutate.Reduce -> {
                    when (mutate) {
                        is SearchMutate.Reduce.UpdateViewState -> {
                            _state.update { it.copy(viewState = mutate.viewState) }
                        }

                        is SearchMutate.Reduce.UpdateImageList -> {
                            _state.update { it.copy(imageList = mutate.imageList) }
                        }

                        is SearchMutate.Reduce.UpdateLoading -> {
                            _state.update { it.copy(isLoading = mutate.isLoading) }
                        }

                        is SearchMutate.Reduce.UpdatePage -> {
                            _state.update { it.copy(page = mutate.page) }
                        }

                        is SearchMutate.Reduce.UpdateHasMore -> {
                            _state.update { it.copy(hasMore = mutate.hasMore) }
                        }

                        is SearchMutate.Reduce.UpdateSearchText -> {
                            _state.update { it.copy(searchText = mutate.searchText) }
                        }
                    }
                }

                is SearchMutate.SideEffect -> {
                    _sideEffect.emit(mutate)
                }
            }
        }.launchIn(viewModelScope)
    }
}
