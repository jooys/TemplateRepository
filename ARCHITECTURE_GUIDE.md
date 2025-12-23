# 아키텍처 적용 가이드

이 문서는 **Mutate-Reduce MVI 패턴**과 **모듈형 Clean Architecture**를 다른 Android 프로젝트에 적용하는 방법을 단계별로 설명합니다.

---

## 📋 목차

1. [프로젝트 초기 설정](#1-프로젝트-초기-설정)
2. [Convention Plugins 구성](#2-convention-plugins-구성)
3. [모듈 구조 생성](#3-모듈-구조-생성)
4. [MVI 패턴 구현](#4-mvi-패턴-구현)
5. [Hilt DI 설정](#5-hilt-di-설정)
6. [테스트 구조 구축](#6-테스트-구조-구축)
7. [체크리스트](#7-체크리스트)

---

## 1. 프로젝트 초기 설정

### 1.1 Version Catalog 설정

`gradle/libs.versions.toml` 생성:

```toml
[versions]
kotlin = "2.2.21"
compose-bom = "2025.11.00"
hilt = "2.57.2"
room = "2.8.3"
retrofit = "2.11.0"
kotest = "5.9.1"
mockk = "1.13.17"
turbine = "1.2.0"

compileSdk = "35"
minSdk = "26"
targetSdk = "35"

[libraries]
# Kotlin
kotlin-stdlib = { group = "org.jetbrains.kotlin", name = "kotlin-stdlib", version.ref = "kotlin" }
kotlinx-coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = "1.10.1" }
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = "1.8.0" }

# Compose
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-material3 = { group = "androidx.compose.material3", name = "material3" }
compose-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
compose-activity = { group = "androidx.activity", name = "activity-compose", version = "1.10.0" }

# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }

# Room
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }

# Retrofit
retrofit-core = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-serialization = { group = "com.squareup.retrofit2", name = "converter-kotlinx-serialization", version.ref = "retrofit" }
okhttp-logging = { group = "com.squareup.okhttp3", name = "logging-interceptor", version = "5.1.0" }

# Testing
kotest-runner = { group = "io.kotest", name = "kotest-runner-junit5", version.ref = "kotest" }
kotest-assertions = { group = "io.kotest", name = "kotest-assertions-core", version.ref = "kotest" }
mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
turbine = { group = "app.cash.turbine", name = "turbine", version.ref = "turbine" }
coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version = "1.10.1" }

[plugins]
android-application = { id = "com.android.application", version = "8.7.3" }
android-library = { id = "com.android.library", version = "8.7.3" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
ksp = { id = "com.google.devtools.ksp", version = "2.2.21-1.0.31" }
```

### 1.2 프로젝트 Gradle 설정

`settings.gradle.kts`:

```kotlin
pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "YourProject"

// Core modules
include(":app")
include(":domain")
include(":data")
include(":model")
include(":remote")
include(":local")
include(":core")

// Feature modules (예시)
include(":feature")
include(":feature:home")
include(":feature:home:navigation")
```

`gradle.properties`:

```properties
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.jvmargs=-Xmx4608m -XX:+UseParallelGC
org.gradle.configuration-cache=true

android.useAndroidX=true
android.enableJetifier=false

kotlin.code.style=official
```

---

## 2. Convention Plugins 구성

### 2.1 build-logic 모듈 생성

```bash
mkdir -p build-logic/convention/src/main/java/com/yourcompany/convention
```

### 2.2 build-logic/convention/build.gradle.kts

```kotlin
plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "custom.application"
            implementationClass = "com.yourcompany.convention.CustomApplicationPlugin"
        }
        register("androidLibrary") {
            id = "custom.android.library"
            implementationClass = "com.yourcompany.convention.CustomAndroidLibraryPlugin"
        }
        register("androidFeature") {
            id = "custom.feature.library"
            implementationClass = "com.yourcompany.convention.CustomAndroidFeatureLibraryPlugin"
        }
        register("androidNavigation") {
            id = "custom.navigation.library"
            implementationClass = "com.yourcompany.convention.CustomNavigationLibraryPlugin"
        }
        register("androidHilt") {
            id = "custom.android.hilt"
            implementationClass = "com.yourcompany.convention.CustomAndroidHiltPlugin"
        }
        register("jvmLibrary") {
            id = "custom.jvm.library"
            implementationClass = "com.yourcompany.convention.CustomJvmLibraryPlugin"
        }
    }
}
```

### 2.3 Feature Library Plugin (핵심)

`CustomAndroidFeatureLibraryPlugin.kt`:

```kotlin
package com.yourcompany.convention

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class CustomAndroidFeatureLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // 기본 플러그인 적용
            pluginManager.apply("com.android.library")
            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            pluginManager.apply("custom.android.hilt")

            // Android 설정
            extensions.configure<LibraryExtension> {
                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                buildFeatures {
                    compose = true
                }

                // Kotest 설정
                testOptions {
                    unitTests.all {
                        it.useJUnitPlatform()
                    }
                }
            }

            // 공통 의존성
            dependencies {
                // Feature는 항상 domain, model, core에 의존
                add("implementation", project(":domain"))
                add("implementation", project(":model"))
                add("implementation", project(":core"))

                // Compose
                val composeBom = project.dependencies.platform(libs.findLibrary("compose-bom").get())
                add("implementation", composeBom)
                add("implementation", libs.findLibrary("compose-ui").get())
                add("implementation", libs.findLibrary("compose-material3").get())
                add("implementation", libs.findLibrary("compose-preview").get())
                add("implementation", libs.findLibrary("compose-activity").get())

                // ViewModel
                add("implementation", libs.findLibrary("lifecycle-viewmodel").get())
                add("implementation", libs.findLibrary("lifecycle-runtime-compose").get())

                // Testing
                add("testImplementation", libs.findLibrary("kotest-runner").get())
                add("testImplementation", libs.findLibrary("kotest-assertions").get())
                add("testImplementation", libs.findLibrary("mockk").get())
                add("testImplementation", libs.findLibrary("turbine").get())
                add("testImplementation", libs.findLibrary("coroutines-test").get())
            }
        }
    }
}
```

### 2.4 나머지 Convention Plugins

각각 `CustomAndroidLibraryPlugin.kt`, `CustomJvmLibraryPlugin.kt`, `CustomAndroidHiltPlugin.kt` 등을 생성하세요.

---

## 3. 모듈 구조 생성

### 3.1 Core Layer 모듈 생성

#### domain (순수 Kotlin)

`domain/build.gradle.kts`:

```kotlin
plugins {
    id("custom.jvm.library")
}

dependencies {
    implementation(project(":model"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
```

구조:
```
domain/
└── src/main/java/
    └── com/yourcompany/domain/
        ├── repository/          # Repository 인터페이스
        │   └── YourRepository.kt
        └── usecase/             # Use Cases
            └── GetDataUseCase.kt
```

#### model (순수 Kotlin)

`model/build.gradle.kts`:

```kotlin
plugins {
    id("custom.jvm.library")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}
```

#### data

`data/build.gradle.kts`:

```kotlin
plugins {
    id("custom.android.library")
    id("custom.android.hilt")
}

dependencies {
    implementation(project(":model"))
    implementation(project(":domain"))
}
```

구조:
```
data/
└── src/main/java/
    └── com/yourcompany/data/
        ├── repository/          # Repository 구현
        │   └── YourRepositoryImpl.kt
        └── datasource/          # DataSource 인터페이스
            ├── RemoteDataSource.kt
            └── LocalDataSource.kt
```

#### remote

`remote/build.gradle.kts`:

```kotlin
plugins {
    id("custom.android.library")
    id("custom.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(project(":model"))
    implementation(project(":data"))

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.serialization)
    implementation(libs.okhttp.logging)
}
```

#### local

`local/build.gradle.kts`:

```kotlin
plugins {
    id("custom.android.library")
    id("custom.android.hilt")
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(project(":model"))
    implementation(project(":data"))

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
}
```

#### core (UI 공통)

`core/build.gradle.kts`:

```kotlin
plugins {
    id("custom.android.library")
    alias(libs.plugins.kotlin.compose)
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    api(composeBom)
    api(libs.compose.ui)
    api(libs.compose.material3)
    api(libs.compose.preview)
}
```

### 3.2 Feature 모듈 생성

각 기능마다:

```bash
mkdir -p feature/home/navigation/src/main/java/com/yourcompany/feature/home/navigation
mkdir -p feature/home/src/main/java/com/yourcompany/feature/home
mkdir -p feature/home/src/test/java/com/yourcompany/feature/home
```

`feature/home/build.gradle.kts`:

```kotlin
plugins {
    id("custom.feature.library")
}

android {
    namespace = "com.yourcompany.feature.home"
}

dependencies {
    implementation(projects.feature.home.navigation)
    // 다른 feature navigation 의존성
}
```

`feature/home/navigation/build.gradle.kts`:

```kotlin
plugins {
    id("custom.navigation.library")
}

android {
    namespace = "com.yourcompany.feature.home.navigation"
}
```

---

## 4. MVI 패턴 구현

### 4.1 기본 구조

각 Feature는 다음 파일들을 가집니다:

```
feature/home/
├── HomeActivity.kt
├── HomeRoute.kt
├── HomeViewModel.kt
├── HomeMutate.kt
├── HomeMutateHandler.kt
├── HomeAction.kt
└── HomeNavigationImpl.kt
```

### 4.2 Action 정의

`HomeAction.kt`:

```kotlin
package com.yourcompany.feature.home

sealed interface HomeAction {
    data object OnViewCreated : HomeAction
    data class OnItemClick(val id: String) : HomeAction
    data class OnSearch(val query: String) : HomeAction
}
```

### 4.3 Mutate 정의

`HomeMutate.kt`:

```kotlin
package com.yourcompany.feature.home

sealed interface HomeMutate {
    // 상태 변경
    sealed interface Reduce : HomeMutate {
        data class UpdateViewState(val viewState: State.ViewState) : Reduce
        data class UpdateItems(val items: List<State.Item>) : Reduce
        data class UpdateLoading(val isLoading: Boolean) : Reduce
    }

    // 일회성 이벤트
    sealed interface SideEffect : HomeMutate {
        data class ShowSnackBar(val message: String) : SideEffect
        data class NavigateToDetail(val id: String) : SideEffect
    }

    // State 정의
    data class State(
        val viewState: ViewState = ViewState.LOADING,
        val items: List<Item> = emptyList(),
        val isLoading: Boolean = false,
    ) {
        enum class ViewState {
            LOADING, SUCCESS, EMPTY, ERROR
        }

        data class Item(
            val id: String,
            val title: String,
            val imageUrl: String,
        )
    }
}
```

### 4.4 MutateHandler 구현

`HomeMutateHandler.kt`:

```kotlin
package com.yourcompany.feature.home

import com.yourcompany.domain.usecase.GetItemsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class HomeMutateHandler @Inject constructor(
    private val getItemsUseCase: GetItemsUseCase,
) {
    fun mutate(
        action: HomeAction,
        state: HomeMutate.State,
    ): Flow<HomeMutate> = flow {
        when (action) {
            is HomeAction.OnViewCreated -> {
                getItemsUseCase.invoke()
                    .onStart {
                        emit(HomeMutate.Reduce.UpdateLoading(true))
                    }
                    .onCompletion {
                        emit(HomeMutate.Reduce.UpdateLoading(false))
                    }
                    .catch { error ->
                        emit(HomeMutate.Reduce.UpdateViewState(HomeMutate.State.ViewState.ERROR))
                        emit(HomeMutate.SideEffect.ShowSnackBar(
                            error.message ?: "알 수 없는 오류가 발생했습니다"
                        ))
                    }
                    .collect { result ->
                        result.onSuccess { data ->
                            val items = data.map { item ->
                                HomeMutate.State.Item(
                                    id = item.id,
                                    title = item.title,
                                    imageUrl = item.imageUrl,
                                )
                            }

                            if (items.isEmpty()) {
                                emit(HomeMutate.Reduce.UpdateViewState(HomeMutate.State.ViewState.EMPTY))
                            } else {
                                emit(HomeMutate.Reduce.UpdateViewState(HomeMutate.State.ViewState.SUCCESS))
                                emit(HomeMutate.Reduce.UpdateItems(items))
                            }
                        }.onFailure { error ->
                            emit(HomeMutate.Reduce.UpdateViewState(HomeMutate.State.ViewState.ERROR))
                            emit(HomeMutate.SideEffect.ShowSnackBar(
                                error.message ?: "데이터를 불러오는데 실패했습니다"
                            ))
                        }
                    }
            }

            is HomeAction.OnItemClick -> {
                emit(HomeMutate.SideEffect.NavigateToDetail(action.id))
            }

            is HomeAction.OnSearch -> {
                // 검색 로직
            }
        }
    }
}
```

### 4.5 ViewModel 구현

`HomeViewModel.kt`:

```kotlin
package com.yourcompany.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mutateHandler: HomeMutateHandler,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeMutate.State())
    val state: StateFlow<HomeMutate.State> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<HomeMutate.SideEffect>()
    val sideEffect: SharedFlow<HomeMutate.SideEffect> = _sideEffect.asSharedFlow()

    fun onAction(action: HomeAction) {
        viewModelScope.launch {
            mutateHandler.mutate(action, _state.value).collect { mutate ->
                when (mutate) {
                    is HomeMutate.Reduce -> reduce(mutate)
                    is HomeMutate.SideEffect -> _sideEffect.emit(mutate)
                }
            }
        }
    }

    private fun reduce(reduce: HomeMutate.Reduce) {
        _state.update { state ->
            when (reduce) {
                is HomeMutate.Reduce.UpdateViewState -> {
                    state.copy(viewState = reduce.viewState)
                }
                is HomeMutate.Reduce.UpdateItems -> {
                    state.copy(items = reduce.items)
                }
                is HomeMutate.Reduce.UpdateLoading -> {
                    state.copy(isLoading = reduce.isLoading)
                }
            }
        }
    }
}
```

### 4.6 Compose UI

`HomeRoute.kt`:

```kotlin
package com.yourcompany.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // 초기 진입
    LaunchedEffect(Unit) {
        viewModel.onAction(HomeAction.OnViewCreated)
    }

    // SideEffect 처리
    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is HomeMutate.SideEffect.ShowSnackBar -> {
                    // Snackbar 표시
                }
                is HomeMutate.SideEffect.NavigateToDetail -> {
                    // Navigation 처리
                }
            }
        }
    }

    HomeScreen(
        state = state,
        onItemClick = { id ->
            viewModel.onAction(HomeAction.OnItemClick(id))
        },
    )
}

@Composable
private fun HomeScreen(
    state: HomeMutate.State,
    onItemClick: (String) -> Unit,
) {
    // UI 구현
    when (state.viewState) {
        HomeMutate.State.ViewState.LOADING -> {
            // Loading UI
        }
        HomeMutate.State.ViewState.SUCCESS -> {
            // Success UI with items
        }
        HomeMutate.State.ViewState.EMPTY -> {
            // Empty state UI
        }
        HomeMutate.State.ViewState.ERROR -> {
            // Error UI
        }
    }
}
```

`HomeActivity.kt`:

```kotlin
package com.yourcompany.feature.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.yourcompany.core.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                HomeRoute()
            }
        }
    }
}
```

---

## 5. Hilt DI 설정

### 5.1 Application 클래스

```kotlin
@HiltAndroidApp
class YourApplication : Application()
```

### 5.2 Repository 바인딩

`data/di/DataModule.kt`:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindYourRepository(
        impl: YourRepositoryImpl
    ): YourRepository
}
```

### 5.3 Network 모듈

`remote/di/NetworkModule.kt`:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .client(okHttpClient)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): YourApiService {
        return retrofit.create(YourApiService::class.java)
    }
}
```

### 5.4 Database 모듈

`local/di/DatabaseModule.kt`:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): YourDatabase {
        return Room.databaseBuilder(
            context,
            YourDatabase::class.java,
            "your_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideYourDao(database: YourDatabase): YourDao {
        return database.yourDao()
    }
}
```

### 5.5 Navigation 바인딩

`feature/home/di/HomeNavigationModule.kt`:

```kotlin
@Module
@InstallIn(ActivityComponent::class)
abstract class HomeNavigationModule {

    @Binds
    abstract fun bindHomeNavigation(
        impl: HomeNavigationImpl
    ): HomeNavigation
}
```

---

## 6. 테스트 구조 구축

### 6.1 MutateHandler 테스트

`HomeMutateHandlerTest.kt`:

```kotlin
package com.yourcompany.feature.home

import app.cash.turbine.test
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow

class HomeMutateHandlerTest : BehaviorSpec() {
    init {
        val mockUseCase: GetItemsUseCase = mockk()
        val handler = HomeMutateHandler(mockUseCase)

        Given("홈 화면에") {
            val initialState = HomeMutate.State()

            When("처음 진입하면") {
                val action = HomeAction.OnViewCreated

                coEvery { mockUseCase.invoke() } returns flow {
                    emit(Result.success(listOf(
                        mockk {
                            every { id } returns "1"
                            every { title } returns "Test"
                            every { imageUrl } returns "url"
                        }
                    )))
                }

                handler.mutate(action, initialState).test {
                    Then("로딩을 시작한다") {
                        awaitItem() shouldBe HomeMutate.Reduce.UpdateLoading(true)
                    }

                    Then("데이터를 표시한다") {
                        awaitItem() shouldBe HomeMutate.Reduce.UpdateViewState(
                            HomeMutate.State.ViewState.SUCCESS
                        )

                        val items = awaitItem() as HomeMutate.Reduce.UpdateItems
                        items.items.size shouldBe 1
                    }

                    Then("로딩을 종료한다") {
                        awaitItem() shouldBe HomeMutate.Reduce.UpdateLoading(false)
                        awaitComplete()
                    }
                }
            }

            When("아이템을 클릭하면") {
                val action = HomeAction.OnItemClick("1")

                handler.mutate(action, initialState).test {
                    Then("상세 화면으로 이동한다") {
                        awaitItem() shouldBe HomeMutate.SideEffect.NavigateToDetail("1")
                        awaitComplete()
                    }
                }
            }
        }
    }
}
```

---

## 7. 체크리스트

프로젝트에 아키텍처를 적용할 때 이 체크리스트를 사용하세요:

### Phase 1: 프로젝트 기반 구축
- [ ] Version Catalog 설정 완료
- [ ] build-logic 모듈 생성
- [ ] Convention Plugins 구현
- [ ] settings.gradle.kts 설정

### Phase 2: 모듈 구조
- [ ] domain 모듈 생성 (순수 Kotlin)
- [ ] model 모듈 생성 (순수 Kotlin)
- [ ] data 모듈 생성
- [ ] remote 모듈 생성
- [ ] local 모듈 생성
- [ ] core 모듈 생성

### Phase 3: 첫 Feature 구현
- [ ] feature 모듈 생성
- [ ] navigation 모듈 생성
- [ ] Action 정의
- [ ] Mutate (State, Reduce, SideEffect) 정의
- [ ] MutateHandler 구현
- [ ] ViewModel 구현
- [ ] Composable UI 구현
- [ ] Activity 구현

### Phase 4: DI 설정
- [ ] Application 클래스에 @HiltAndroidApp
- [ ] Repository 바인딩
- [ ] Network 모듈 구현
- [ ] Database 모듈 구현
- [ ] Navigation 바인딩

### Phase 5: 테스트
- [ ] MutateHandler 테스트 작성
- [ ] Repository 테스트 작성
- [ ] UseCase 테스트 작성

### Phase 6: 추가 Feature
- [ ] 두 번째 Feature 구현
- [ ] Feature 간 Navigation 구현
- [ ] 공통 코드 Core로 이동

---

## 추가 참고 자료

### 패키지 구조 예시

```
com.yourcompany/
├── app/                    # Application
├── core/                   # 공통 UI
│   ├── component/
│   ├── theme/
│   └── util/
├── data/                   # Repository 구현
│   ├── repository/
│   └── datasource/
├── domain/                 # Business Logic
│   ├── repository/
│   └── usecase/
├── feature/
│   └── home/              # Feature
│       ├── HomeActivity.kt
│       ├── HomeRoute.kt
│       ├── HomeViewModel.kt
│       ├── HomeMutate.kt
│       ├── HomeMutateHandler.kt
│       ├── HomeAction.kt
│       └── di/
├── local/                  # Room Database
│   ├── dao/
│   ├── entity/
│   └── di/
├── model/                  # Data Models
└── remote/                 # Network
    ├── api/
    ├── interceptor/
    └── di/
```

### 핵심 원칙

1. **의존성 규칙**: Feature → Domain ← Data → Implementation
2. **순수 Kotlin**: Domain과 Model은 Android 의존성 없음
3. **단방향 데이터 흐름**: Action → MutateHandler → Mutate → State
4. **명확한 분리**: Reduce (상태 변경) vs SideEffect (일회성 이벤트)
5. **테스트 가능성**: MutateHandler는 순수 함수로 테스트 용이

---

이 가이드를 따라 단계별로 구현하면 확장 가능하고 유지보수가 쉬운 Android 앱을 만들 수 있습니다.
