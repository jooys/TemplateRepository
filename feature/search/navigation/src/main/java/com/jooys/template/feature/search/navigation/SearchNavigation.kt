package com.jooys.template.feature.search.navigation

import android.content.Context
import android.content.Intent

interface SearchNavigation {
    fun getSearchIntent(context: Context): Intent
}
