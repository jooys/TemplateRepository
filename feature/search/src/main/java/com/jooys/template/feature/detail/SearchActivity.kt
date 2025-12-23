package com.jooys.template.feature.detail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jooys.template.core.theme.CustomTheme
import com.jooys.template.feature.bookmark.navigation.BookmarkNavigation
import com.jooys.template.feature.detail.navigation.DetailNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity : ComponentActivity() {

    @Inject
    lateinit var detailNavigation: DetailNavigation

    @Inject
    lateinit var bookmarkNavigation: BookmarkNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomTheme {
                SearchRoute(
                    detailNavigation = detailNavigation,
                    bookmarkNavigation = bookmarkNavigation
                )
            }
        }
    }
}
