package com.jooys.template.feature.detail

import android.content.Context
import android.content.Intent
import com.jooys.template.feature.search.navigation.SearchNavigation
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Inject

class SearchNavigationImpl @Inject constructor() : SearchNavigation {
    override fun getSearchIntent(context: Context): Intent {
        return Intent(context, SearchActivity::class.java)
    }
}

@Module
@InstallIn(ActivityComponent::class)
interface NavigationModule {
    @Binds
    fun bindsNavigation(
        searchNavigationImpl: SearchNavigationImpl
    ): SearchNavigation
}
