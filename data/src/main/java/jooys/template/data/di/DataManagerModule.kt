package jooys.template.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jooys.template.data.preference.SharedPreference
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataManagerModule {

    @Provides
    @Singleton
    fun provideSharePreference(
        @ApplicationContext context: Context
    ) : SharedPreference {
        return SharedPreference(context, context.packageName)
    }
}
