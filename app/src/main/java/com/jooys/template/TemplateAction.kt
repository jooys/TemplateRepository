package com.jooys.template

sealed interface TemplateAction {
    data object OnViewCreated: TemplateAction
    data object OnClickButton: TemplateAction
}
