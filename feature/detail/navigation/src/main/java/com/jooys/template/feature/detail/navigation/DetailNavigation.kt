package com.jooys.template.feature.detail.navigation

import android.content.Context
import android.content.Intent

interface DetailNavigation {
    fun getDetailIntent(context: Context, id: String): Intent
}
