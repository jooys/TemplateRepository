package com.jooys.template

sealed interface TemplateMutate {
    data class State(
        val title: String = ""
    )

    sealed interface Reduce : TemplateMutate {
        data class UpdateTitle(val title: String) : Reduce
    }

    sealed interface SideEffect : TemplateMutate {
        data class ShowSnackBar(val message: String) : SideEffect
        data object Finish : SideEffect
    }
}
