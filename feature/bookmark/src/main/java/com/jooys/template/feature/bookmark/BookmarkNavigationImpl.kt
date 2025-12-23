package com.jooys.template.feature.bookmark

import android.content.Context
import android.content.Intent
import com.jooys.template.feature.bookmark.navigation.BookmarkNavigation
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Inject

class BookmarkNavigationImpl @Inject constructor() : BookmarkNavigation {
    override fun getBookmarkIntent(context: Context): Intent {
        return Intent(context, BookmarkActivity::class.java)
    }
}

@Module
@InstallIn(ActivityComponent::class)
interface BookmarkNavigationModule {
    @Binds
    fun bindsBookmarkNavigation(
        bookmarkNavigationImpl: BookmarkNavigationImpl,
    ): BookmarkNavigation
}
