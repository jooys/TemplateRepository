package com.jooys.template.shared.model.home

sealed class IntelligenceEntity {
    data class Response(
        val type: Type,
        val attributes: Attribute? = null,
        val purpose: String? = null
    ): IntelligenceEntity() {
        data class Attribute(
            val deepLink: String,
            val description: String,
            val title: String,
            val imageUrl: String,
            val buttonTitle: String?
        )
    }

    enum class Type{
        SIMPLE_IMAGE, NO_INTELLIGENCE, ONE_BUTTON_CARD
    }
}
