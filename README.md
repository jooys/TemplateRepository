# Android Modular Architecture Template

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.21-blue.svg)](https://kotlinlang.org)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26-green.svg)](https://developer.android.com/about/versions/oreo)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-35-green.svg)](https://developer.android.com)
[![Compose](https://img.shields.io/badge/Compose-2025.11.00-blue.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-Apache%202.0-orange.svg)](LICENSE)

엔터프라이즈급 Android 애플리케이션을 위한 **모듈형 클린 아키텍처 템플릿**입니다. 이 프로젝트는 Unsplash API를 활용한 사진 검색 앱을 예제로, 확장 가능하고 유지보수하기 쉬운 현대적인 Android 앱 개발 방법론을 제시합니다.

## 📋 목차

- [주요 특징](#-주요-특징)
- [아키텍처 개요](#-아키텍처-개요)
- [모듈 구조](#-모듈-구조)
- [기술 스택](#-기술-스택)
- [시작하기](#-시작하기)
- [프로젝트 구조 상세](#-프로젝트-구조-상세)
- [MVI 패턴 가이드](#-mvi-패턴-가이드)
- [새 기능 추가하기](#-새-기능-추가하기)
- [테스트 작성하기](#-테스트-작성하기)
- [Convention Plugins](#-convention-plugins)
- [의존성 규칙](#-의존성-규칙)
- [빌드 명령어](#-빌드-명령어)
- [참고 자료](#-참고-자료)

---

## 🎯 주요 특징

### 1. **모듈형 클린 아키텍처 (Modular Clean Architecture)**
- 16개의 독립적인 모듈로 구성
- 명확한 레이어 분리 (Presentation → Domain → Data)
- 엄격한 의존성 규칙 강제 (`assertModuleGraph` 플러그인)
- 순수 Kotlin 비즈니스 로직 (Android 의존성 없음)

### 2. **MVI (Mutate-Reduce) 패턴**
- 일방향 데이터 흐름
- 예측 가능한 상태 관리
- Reduce (상태 변경)와 SideEffect (이벤트) 명확히 분리
- 모든 feature에서 일관된 구조

### 3. **Convention Plugins**
- 커스텀 Gradle 플러그인으로 빌드 설정 표준화
- 보일러플레이트 코드 제거
- 새 모듈 추가 시 일관성 자동 보장

### 4. **현대적인 Android 개발**
- Jetpack Compose (UI)
- Kotlin Coroutines & Flow (비동기 처리)
- Hilt (의존성 주입)
- Room (로컬 DB)
- Retrofit (네트워크)

### 5. **종합적인 테스트**
- Kotest BehaviorSpec (BDD 스타일)
- MockK (모킹)
- Turbine (Flow 테스트)
- MutateHandler 중심 테스트 (순수 비즈니스 로직)

### 6. **Production-Ready**
- 에러 처리 패턴
- 로깅 시스템
- 네트워크 인터셉터
- 이미지 로딩 최적화 (Coil)

---

## 🏗️ 아키텍처 개요

### 레이어 구조

```
┌─────────────────────────────────────────┐
│           Presentation Layer            │  ← Feature Modules (UI)
│  (Compose, ViewModel, MutateHandler)    │
└─────────────────────────────────────────┘
                    ↓ ↑
┌─────────────────────────────────────────┐
│            Domain Layer                 │  ← 순수 Kotlin
│   (UseCases, Repository Interfaces)     │
└─────────────────────────────────────────┘
                    ↓ ↑
┌─────────────────────────────────────────┐
│             Data Layer                  │  ← Repository 구현
│  (RepositoryImpl, DataSource Abstractions)│
└─────────────────────────────────────────┘
          ↓ ↑                    ↓ ↑
┌──────────────────┐    ┌──────────────────┐
│  Remote (API)    │    │  Local (Room)    │
└──────────────────┘    └──────────────────┘
```

### MVI (Mutate-Reduce) 데이터 흐름

```
User Interaction
       │
       ↓
Action (Sealed Interface)
       │
       ↓
ViewModel.onAction()
       │
       ↓
MutateHandler.mutate()  ← 비즈니스 로직 (UseCase 호출)
       │
       ↓
Flow<Mutate>
  ├─ Reduce (상태 변경)
  └─ SideEffect (일회성 이벤트)
       │
       ↓
ViewModel
  ├─ StateFlow (UI 상태)
  └─ SharedFlow (이벤트)
       │
       ↓
Composable (UI Rendering)
```

---

## 📦 모듈 구조

### Core Layers (핵심 레이어)

| 모듈 | 역할 | 의존성 | 타입 |
|------|------|--------|------|
| **app** | 애플리케이션 진입점 | 모든 모듈 | Android App |
| **domain** | 비즈니스 로직 (UseCases, Repository 인터페이스) | `model` | 순수 Kotlin |
| **model** | 데이터 모델 (Kotlin Serialization) | - | 순수 Kotlin |
| **data** | Repository 구현 | `domain`, `model` | Android Library |
| **remote** | 네트워크 구현 (Retrofit, Unsplash API) | `data`, `model` | Android Library |
| **local** | 데이터베이스 (Room) | `data`, `model` | Android Library |
| **core** | 공유 UI 컴포넌트 및 테마 | `model` | Android Library (Compose) |

### Feature Modules (기능 모듈)

각 기능은 **메인 모듈**과 **navigation 모듈**로 구성됩니다:

| 기능 | 모듈 | 역할 |
|------|------|------|
| **검색** | `feature:search` | 사진 검색 화면 (Unsplash API) |
|  | `feature:search:navigation` | 검색 화면 Navigation 인터페이스 |
| **상세** | `feature:detail` | 사진 상세 정보 화면 |
|  | `feature:detail:navigation` | 상세 화면 Navigation 인터페이스 |
| **북마크** | `feature:bookmark` | 북마크된 사진 목록 (로컬 DB) |
|  | `feature:bookmark:navigation` | 북마크 화면 Navigation 인터페이스 |

### Build Logic (빌드 로직)

| 모듈 | 역할 |
|------|------|
| **build-logic/convention** | 커스텀 Gradle Convention Plugins |

---

## 🛠️ 기술 스택

### Android & Kotlin

- **Kotlin**: 2.2.21
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 35
- **Compile SDK**: 36
- **AGP**: 8.12.3

### UI

- **Jetpack Compose**: BOM 2025.11.00
- **Material 3**: 1.7.8
- **Coil**: 3.3.0 (이미지 로딩)

### Architecture Components

- **Lifecycle**: 2.9.4
- **ViewModel**: 2.9.4
- **Room**: 2.8.3 (로컬 DB)

### Dependency Injection

- **Hilt**: 2.57.2
- **KSP**: 2.0.21-1.0.29

### Networking

- **Retrofit**: 2.11.0
- **OkHttp**: 5.1.0
- **Kotlin Serialization**: 1.9.0

### Asynchronous

- **Kotlin Coroutines**: 1.10.2

### Testing

- **Kotest**: 5.9.1 (BehaviorSpec)
- **MockK**: 1.13.17
- **Turbine**: 1.2.0 (Flow 테스트)

### Other

- **Logger**: 2.2.0
- **Module Graph Assertion**: 2.9.0 (의존성 규칙 검증)

---

## 🚀 시작하기

### 사전 요구사항

- **JDK**: 17 이상
- **Android Studio**: Ladybug (2024.2.1) 이상
- **Gradle**: 8.12.3 (Wrapper 사용)
- **Unsplash API Key**: [Unsplash Developers](https://unsplash.com/developers)에서 발급

### 1. 프로젝트 클론

```bash
git clone https://github.com/yourusername/template-repository.git
cd template-repository
```

### 2. API Key 설정

`local.properties` 파일에 Unsplash API Key를 추가하세요:

```properties
sdk.dir=/path/to/android/sdk
api_key=YOUR_UNSPLASH_API_KEY_HERE
```

> **참고**: `local.properties`는 `.gitignore`에 포함되어 있어 커밋되지 않습니다.

### 3. 프로젝트 빌드

```bash
./gradlew build
```

### 4. 앱 실행

Android Studio에서 `app` 모듈을 실행하거나:

```bash
./gradlew installDebug
```

---

## 📂 프로젝트 구조 상세

프로젝트는 다음과 같은 디렉토리 구조를 가집니다:

```
TemplateRepository/
├── app/                    # 애플리케이션 진입점
├── domain/                 # 순수 Kotlin 비즈니스 로직
├── model/                  # 데이터 모델
├── data/                   # Repository 구현
├── remote/                 # 네트워크 레이어
├── local/                  # 로컬 데이터베이스
├── core/                   # 공유 UI 컴포넌트
├── feature/
│   ├── search/            # 검색 기능
│   ├── detail/            # 상세 기능
│   └── bookmark/          # 북마크 기능
├── build-logic/           # Convention Plugins
└── gradle/                # 버전 카탈로그
```

자세한 파일 구조는 `CLAUDE.md` 파일을 참고하세요.

---

## 🔄 MVI 패턴 가이드

이 프로젝트는 **Mutate-Reduce** 변형의 MVI 패턴을 사용합니다.

### 핵심 구성요소

1. **Action**: 사용자 이벤트를 `sealed interface`로 정의
2. **State**: 화면의 모든 상태를 하나의 `data class`로 관리
3. **Mutate**: `Reduce` (상태 변경)와 `SideEffect` (이벤트)로 분리
4. **MutateHandler**: UseCase를 호출하고 `Flow<Mutate>` 반환
5. **ViewModel**: StateFlow/SharedFlow 관리
6. **Composable**: UI 렌더링

### 예제: Search 기능

```kotlin
// 1. Action 정의
sealed interface SearchAction {
    data object OnViewCreated : SearchAction
    data class OnSearch(val text: String) : SearchAction
    data class OnClickItem(val id: String) : SearchAction
}

// 2. State 정의
data class State(
    val imageList: List<ImageItem> = emptyList(),
    val isLoading: Boolean = false,
    val searchText: String = ""
)

// 3. Mutate 정의
sealed interface SearchMutate {
    sealed interface Reduce : SearchMutate {
        data class UpdateImageList(val list: List<ImageItem>) : Reduce
        data class UpdateLoading(val isLoading: Boolean) : Reduce
    }
    sealed interface SideEffect : SearchMutate {
        data class NaviToDetail(val id: String) : SideEffect
        data class ShowSnackBar(val message: String) : SideEffect
    }
}

// 4. MutateHandler
class SearchMutateHandler @Inject constructor(
    private val getSearchPhotoUseCase: GetSearchPhotoUseCase
) {
    fun mutate(action: SearchAction, state: State): Flow<SearchMutate> = flow {
        when (action) {
            is SearchAction.OnSearch -> {
                getSearchPhotoUseCase(action.text, 1)
                    .onStart { emit(Reduce.UpdateLoading(true)) }
                    .onCompletion { emit(Reduce.UpdateLoading(false)) }
                    .collect { result ->
                        result.onSuccess { data ->
                            emit(Reduce.UpdateImageList(data.results))
                        }.onFailure { error ->
                            emit(SideEffect.ShowSnackBar(error.message ?: "Error"))
                        }
                    }
            }
            is SearchAction.OnClickItem -> {
                emit(SideEffect.NaviToDetail(action.id))
            }
        }
    }
}

// 5. ViewModel
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val mutateHandler: SearchMutateHandler
) : ViewModel() {
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SearchMutate.SideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    fun onAction(action: SearchAction) {
        viewModelScope.launch {
            mutateHandler.mutate(action, _state.value).collect { mutate ->
                when (mutate) {
                    is SearchMutate.Reduce -> reduce(mutate)
                    is SearchMutate.SideEffect -> _sideEffect.emit(mutate)
                }
            }
        }
    }

    private fun reduce(reduce: SearchMutate.Reduce) {
        _state.update { state ->
            when (reduce) {
                is SearchMutate.Reduce.UpdateImageList -> 
                    state.copy(imageList = reduce.list)
                is SearchMutate.Reduce.UpdateLoading -> 
                    state.copy(isLoading = reduce.isLoading)
            }
        }
    }
}

// 6. Composable
@Composable
fun SearchRoute(
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onAction(SearchAction.OnViewCreated)
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is SearchMutate.SideEffect.ShowSnackBar -> {
                    // 스낵바 표시
                }
                is SearchMutate.SideEffect.NaviToDetail -> {
                    // 상세 화면으로 이동
                }
            }
        }
    }

    SearchScreen(state = state, onAction = viewModel::onAction)
}
```

---

## ➕ 새 기능 추가하기

### 단계별 가이드

1. **모듈 생성**: `settings.gradle.kts`에 모듈 추가
2. **build.gradle.kts 작성**: Convention Plugin 사용
3. **Navigation 인터페이스**: feature/navigation 모듈에 작성
4. **MVI 구현**: Action, State, Mutate, MutateHandler, ViewModel 작성
5. **UI 작성**: Composable 함수 작성
6. **Navigation 구현**: NavigationImpl 작성
7. **테스트 작성**: MutateHandler 테스트

자세한 가이드는 `CLAUDE.md`의 "새 기능 추가하기" 섹션을 참고하세요.

---

## 🧪 테스트 작성하기

이 프로젝트는 **MutateHandler만 테스트**합니다.

### Kotest BehaviorSpec 예제

```kotlin
class SearchMutateHandlerTest : BehaviorSpec() {
    init {
        val mockUseCase: GetSearchPhotoUseCase = mockk()
        val handler = SearchMutateHandler(mockUseCase)

        Given("검색 화면에") {
            val state = SearchMutate.State()

            When("검색을 수행하면") {
                val action = SearchAction.OnSearch("test")

                coEvery { mockUseCase.invoke(any(), any()) } returns flow {
                    emit(Result.success(mockData))
                }

                handler.mutate(action, state).test {
                    Then("로딩 상태를 시작한다") {
                        awaitItem() shouldBe UpdateLoading(true)
                    }

                    Then("데이터를 업데이트한다") {
                        val item = awaitItem()
                        item.shouldBeInstanceOf<UpdateImageList>()
                    }

                    Then("로딩 상태를 종료한다") {
                        awaitItem() shouldBe UpdateLoading(false)
                    }

                    awaitComplete()
                }
            }
        }
    }
}
```

### 테스트 실행

```bash
./gradlew test
./gradlew :feature:search:testDebugUnitTest
```

---

## 🔌 Convention Plugins

빌드 설정을 표준화하는 커스텀 Gradle 플러그인입니다.

### 사용 가능한 Plugins

| Plugin | 용도 |
|--------|------|
| `custom.application` | App 모듈 설정 |
| `custom.android.library` | Android Library |
| `custom.android.library.compose` | Compose 지원 Library |
| `custom.android.hilt` | Hilt DI 설정 |
| `custom.jvm.library` | 순수 Kotlin 모듈 |
| `custom.feature.library` | Feature 모듈 (자동으로 domain, model, core 의존성 추가) |
| `custom.navigation.library` | Navigation 모듈 |

### 사용 예시

```kotlin
// feature/myfeature/build.gradle.kts
plugins {
    id("custom.feature.library")
    id("custom.android.hilt")
    id("custom.android.library.compose")
}

android {
    namespace = "com.jooys.template.feature.myfeature"
}
```

---

## 🚫 의존성 규칙

### 엄격한 레이어링

```
Features → domain + model + core (✅)
Features → data/remote/local (❌ 절대 안 됨)

domain → model (✅)
domain → Android 의존성 (❌ 순수 Kotlin만)

data → domain + model (✅)
remote/local → data + model (✅)

app → 모든 모듈 (✅)
```

### 규칙 검증

```bash
./gradlew assertModuleGraph
```

**규칙 위반 시 빌드 실패:**

```
Violation: :feature:search -> :data (FORBIDDEN)
Features must not depend on data layer directly!
```

---

## 🏃 빌드 명령어

### 빌드

```bash
./gradlew build                # 전체 빌드
./gradlew assembleDebug        # 디버그 APK
./gradlew assembleRelease      # 릴리즈 APK
./gradlew clean                # 정리
```

### 테스트

```bash
./gradlew test                          # 전체 테스트
./gradlew testDebugUnitTest             # 디버그 테스트
./gradlew :feature:search:test          # 특정 모듈 테스트
```

### 검사

```bash
./gradlew lint                 # Lint 검사
./gradlew check                # 모든 검사 (테스트 + Lint)
./gradlew assertModuleGraph    # 모듈 의존성 규칙 검증
```

---

## 📚 참고 자료

### 공식 문서

- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Retrofit](https://square.github.io/retrofit/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

### 아키텍처

- [Guide to app architecture](https://developer.android.com/topic/architecture)
- [Android Clean Architecture](https://github.com/android10/Android-CleanArchitecture)
- [MVI Pattern](https://hannesdorfmann.com/android/model-view-intent/)

### 테스트

- [Kotest](https://kotest.io/)
- [MockK](https://mockk.io/)
- [Turbine](https://github.com/cashapp/turbine)

### Convention Plugins

- [Gradle Convention Plugins](https://docs.gradle.org/current/samples/sample_convention_plugins.html)
- [Now in Android - Convention Plugins](https://github.com/android/nowinandroid/tree/main/build-logic)

---

## 📄 라이선스

```
Copyright 2025 Jooys Template

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

### 코드 스타일

- Kotlin 공식 코딩 컨벤션을 따릅니다
- Android Studio의 기본 포매터 사용
- 모든 public API에 KDoc 주석 작성

---
