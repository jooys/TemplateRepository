package com.jooys.template.feature.bookmark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jooys.template.core.theme.CustomTheme
import com.jooys.template.feature.detail.navigation.DetailNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BookmarkActivity : ComponentActivity() {

    @Inject
    lateinit var detailNavigation: DetailNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomTheme {
                BookmarkRoute(
                    detailNavigation = detailNavigation
                )
            }
        }
    }
}
