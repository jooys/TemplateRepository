package com.jooys.template.feature.detail

import android.content.Context
import android.content.Intent
import com.jooys.template.feature.detail.navigation.DetailNavigation
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Inject

class DetailNavigationImpl @Inject constructor() : DetailNavigation {
    override fun getDetailIntent(context: Context, id: String): Intent {
        return Intent(context, DetailActivity::class.java).apply {
            putExtra("id", id)
        }
    }
}

@Module
@InstallIn(ActivityComponent::class)
interface DetailNavigationModule {
    @Binds
    fun bindsDetailNavigation(
        searchNavigationImpl: DetailNavigationImpl,
    ): DetailNavigation
}
