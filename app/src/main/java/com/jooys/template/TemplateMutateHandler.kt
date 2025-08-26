package com.jooys.template

import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TemplateMutateHandler @Inject constructor() {

    fun mutate(action: TemplateAction) = flow {
        when (action) {
            TemplateAction.OnViewCreated -> {
                emit(TemplateMutate.Reduce.UpdateTitle("테스트"))
            }

            is TemplateAction.OnClickButton -> {
                emit(TemplateMutate.SideEffect.ShowSnackBar(""))
            }
        }
    }
}
