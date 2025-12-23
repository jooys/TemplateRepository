package com.jooys.template

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.jooys.template.feature.search.navigation.SearchNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var searchNavigation: SearchNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(searchNavigation.getSearchIntent(this))
        finish()
    }
}
