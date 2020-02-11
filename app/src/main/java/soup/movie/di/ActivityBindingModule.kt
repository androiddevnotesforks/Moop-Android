package soup.movie.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import soup.movie.di.scope.ActivityScope
import soup.movie.di.ui.*
import soup.movie.system.di.SystemAssistedInjectModule
import soup.movie.ui.main.MainActivity

@Module
interface ActivityBindingModule {

    @ActivityScope
    @ContributesAndroidInjector(
        modules = [
            MainUiModule::class,
            HomeUiModule::class,
            SearchUiModule::class,
            SettingsUiModule::class,
            ThemeSettingUiModule::class,
            TheaterMapUiModule::class,
            TheaterEditUiModule::class,
            SystemAssistedInjectModule::class
        ]
    )
    fun bindMainActivity(): MainActivity
}
