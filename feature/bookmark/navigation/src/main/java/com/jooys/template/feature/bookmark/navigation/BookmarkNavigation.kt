package com.jooys.template.feature.bookmark.navigation

import android.content.Context
import android.content.Intent

interface BookmarkNavigation {
    fun getBookmarkIntent(context: Context): Intent
}
