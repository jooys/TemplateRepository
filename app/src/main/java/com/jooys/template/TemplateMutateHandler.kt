package com.jooys.template

import com.jooys.template.home.GetIntelligenceUseCase
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class TemplateMutateHandler @Inject constructor(
    private val getIntelligenceUseCase: GetIntelligenceUseCase
) {

    fun mutate(action: TemplateAction) = flow {
        when (action) {
            TemplateAction.OnViewCreated -> {
                emit(TemplateMutate.Reduce.UpdateTitle("테스트"))
                getIntelligenceUseCase.invoke("10.0.0")
                    .onStart {  }
                    .onCompletion {  }
                    .collect {
                        it.onSuccess {
                            emit(TemplateMutate.Reduce.UpdateTitle(it.version))
                        }.onFailure {
                            it.printStackTrace()
                            emit(TemplateMutate.SideEffect.ShowSnackBar(it.message.toString()))
                        }
                    }
            }

            is TemplateAction.OnClickButton -> {
                emit(TemplateMutate.SideEffect.ShowSnackBar(""))
            }
        }
    }
}
