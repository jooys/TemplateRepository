# 빠른 리팩토링 가이드

다른 프로젝트를 Mutate-Reduce MVI 아키텍처로 변환하기 위한 빠른 참조 가이드입니다.

---

## 📝 체크리스트

```
[ ] 1. Convention Plugins 복사
[ ] 2. Version Catalog 설정
[ ] 3. 모듈 생성 (domain, model, data, remote, local, core)
[ ] 4. Repository 인터페이스를 domain으로 분리
[ ] 5. UseCase 생성
[ ] 6. 첫 Feature를 MVI로 변환
[ ] 7. 테스트 작성
[ ] 8. 나머지 Feature 변환
```

---

## 🚀 빠른 시작 (30분)

### 1단계: Convention Plugins 복사 (5분)

```bash
# 이 프로젝트의 build-logic를 복사
cp -r build-logic [대상프로젝트]/

# 패키지명 변경
# com.jooys.template -> com.yourcompany.yourproject
```

### 2단계: Version Catalog 설정 (5분)

`gradle/libs.versions.toml` 생성:

```toml
[versions]
kotlin = "2.2.21"
compose-bom = "2025.11.00"
hilt = "2.57.2"

[plugins]
android-application = { id = "com.android.application", version = "8.7.3" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
```

### 3단계: 모듈 생성 (10분)

```bash
# Core Layers
mkdir -p domain/src/main/java/com/yourcompany/domain
mkdir -p model/src/main/java/com/yourcompany/model
mkdir -p data/src/main/java/com/yourcompany/data
mkdir -p core/src/main/java/com/yourcompany/core

# Feature
mkdir -p feature/home/src/main/java/com/yourcompany/feature/home
mkdir -p feature/home/navigation/src/main/java/com/yourcompany/feature/home/navigation
mkdir -p feature/home/src/test/java/com/yourcompany/feature/home
```

각 모듈의 `build.gradle.kts`:

```kotlin
// domain/build.gradle.kts
plugins { id("custom.jvm.library") }
dependencies {
    implementation(project(":model"))
}

// feature/home/build.gradle.kts
plugins { id("custom.feature.library") }
android { namespace = "com.yourcompany.feature.home" }
dependencies {
    implementation(projects.feature.home.navigation)
}
```

### 4단계: 첫 Feature를 MVI로 변환 (10분)

기존 ViewModel을 다음 파일들로 분리:

1. **HomeAction.kt**
```kotlin
sealed interface HomeAction {
    data object OnViewCreated : HomeAction
    data class OnItemClick(val id: String) : HomeAction
}
```

2. **HomeMutate.kt**
```kotlin
sealed interface HomeMutate {
    sealed interface Reduce : HomeMutate {
        data class UpdateData(val data: List<Item>) : Reduce
        data class UpdateLoading(val isLoading: Boolean) : Reduce
    }

    sealed interface SideEffect : HomeMutate {
        data class ShowError(val message: String) : SideEffect
    }

    data class State(
        val data: List<Item> = emptyList(),
        val isLoading: Boolean = false
    )

    data class Item(val id: String, val title: String)
}
```

3. **HomeMutateHandler.kt**
```kotlin
class HomeMutateHandler @Inject constructor(
    private val useCase: GetDataUseCase
) {
    fun mutate(action: HomeAction, state: HomeMutate.State): Flow<HomeMutate> = flow {
        when (action) {
            is HomeAction.OnViewCreated -> {
                useCase.invoke()
                    .onStart { emit(HomeMutate.Reduce.UpdateLoading(true)) }
                    .onCompletion { emit(HomeMutate.Reduce.UpdateLoading(false)) }
                    .collect { result ->
                        result.onSuccess { data ->
                            emit(HomeMutate.Reduce.UpdateData(data))
                        }.onFailure { error ->
                            emit(HomeMutate.SideEffect.ShowError(error.message ?: "Error"))
                        }
                    }
            }
            is HomeAction.OnItemClick -> {
                // Handle click
            }
        }
    }
}
```

4. **HomeViewModel.kt** (간소화)
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mutateHandler: HomeMutateHandler
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
                is HomeMutate.Reduce.UpdateData -> state.copy(data = reduce.data)
                is HomeMutate.Reduce.UpdateLoading -> state.copy(isLoading = reduce.isLoading)
            }
        }
    }
}
```

---

## 🎯 핵심 변환 규칙

### MVVM → MVI 변환표

| 기존 MVVM | 새로운 MVI |
|-----------|-----------|
| `fun loadData()` | `HomeAction.OnLoadData` |
| `val items: LiveData<List<Item>>` | `HomeMutate.State.items` |
| `val isLoading: LiveData<Boolean>` | `HomeMutate.State.isLoading` |
| `_showToast.value = "Error"` | `emit(HomeMutate.SideEffect.ShowToast("Error"))` |
| `_items.value = newList` | `emit(HomeMutate.Reduce.UpdateItems(newList))` |

### 기존 코드 → 새 코드 매핑

#### ViewModel의 비즈니스 로직 → MutateHandler

**기존:**
```kotlin
class HomeViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>> = _items

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getData()
                _items.value = result
            } catch (e: Exception) {
                _showError.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
```

**새로운:**
```kotlin
class HomeMutateHandler @Inject constructor(
    private val useCase: GetDataUseCase
) {
    fun mutate(action: HomeAction, state: HomeMutate.State): Flow<HomeMutate> = flow {
        when (action) {
            is HomeAction.LoadData -> {
                useCase.invoke()
                    .onStart { emit(HomeMutate.Reduce.UpdateLoading(true)) }
                    .onCompletion { emit(HomeMutate.Reduce.UpdateLoading(false)) }
                    .collect { result ->
                        result.onSuccess { data ->
                            emit(HomeMutate.Reduce.UpdateItems(data))
                        }.onFailure { error ->
                            emit(HomeMutate.SideEffect.ShowError(error.message ?: "Error"))
                        }
                    }
            }
        }
    }
}
```

---

## 🧪 테스트 템플릿

```kotlin
class HomeMutateHandlerTest : BehaviorSpec() {
    init {
        val mockUseCase: GetDataUseCase = mockk()
        val handler = HomeMutateHandler(mockUseCase)

        Given("홈 화면에") {
            val state = HomeMutate.State()

            When("데이터를 로드하면") {
                val action = HomeAction.LoadData

                coEvery { mockUseCase.invoke() } returns flow {
                    emit(Result.success(listOf(mockItem)))
                }

                handler.mutate(action, state).test {
                    Then("로딩을 시작한다") {
                        awaitItem() shouldBe HomeMutate.Reduce.UpdateLoading(true)
                    }

                    Then("데이터를 업데이트한다") {
                        val item = awaitItem() as HomeMutate.Reduce.UpdateItems
                        item.items.size shouldBe 1
                    }

                    Then("로딩을 종료한다") {
                        awaitItem() shouldBe HomeMutate.Reduce.UpdateLoading(false)
                        awaitComplete()
                    }
                }
            }

            When("에러가 발생하면") {
                val action = HomeAction.LoadData

                coEvery { mockUseCase.invoke() } returns flow {
                    emit(Result.failure(Exception("Network Error")))
                }

                handler.mutate(action, state).test {
                    awaitItem() // UpdateLoading(true)

                    Then("에러를 표시한다") {
                        val error = awaitItem() as HomeMutate.SideEffect.ShowError
                        error.message shouldBe "Network Error"
                    }

                    awaitItem() // UpdateLoading(false)
                    awaitComplete()
                }
            }
        }
    }
}
```

---

## 📦 모듈별 역할

```
domain/          → Repository 인터페이스, UseCase (순수 Kotlin)
model/           → 데이터 모델 (순수 Kotlin)
data/            → Repository 구현, DataSource 인터페이스
remote/          → Retrofit, API Service
local/           → Room Database, DAO
core/            → 공통 UI 컴포넌트, Theme
feature/[name]/  → MVI 구현 (Action, Mutate, MutateHandler, ViewModel, UI)
```

---

## ⚠️ 주의사항

1. **절대 하지 말 것**
   - ❌ Feature에서 data/remote/local 직접 의존
   - ❌ domain에 Android 의존성 추가
   - ❌ MutateHandler 밖에서 비즈니스 로직 작성

2. **반드시 할 것**
   - ✅ Feature는 domain만 의존
   - ✅ UseCase는 Result<T> 반환
   - ✅ 모든 상태 변경은 Reduce로
   - ✅ 모든 일회성 이벤트는 SideEffect로
   - ✅ MutateHandler 테스트 작성

---

## 🔍 디버깅 팁

### 빌드 에러 해결

**"Unresolved reference: projects"**
→ `settings.gradle.kts`에 `enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")` 추가

**"Cannot access class ... Check your module classpath"**
→ 의존성 순서 확인 (domain → data → feature)

**"No such property: libs"**
→ Version Catalog 설정 확인

### 테스트 에러 해결

**"No tests found"**
→ `build.gradle.kts`에 `testOptions { unitTests.all { it.useJUnitPlatform() } }` 추가

**"MockK could not find mocked calls"**
→ `coEvery` vs `every` 확인 (suspend function은 coEvery)

---

## 📚 더 알아보기

- `ARCHITECTURE_GUIDE.md` - 상세 아키텍처 가이드
- `CLAUDE.md` - Claude Code용 프로젝트 가이드
- 기존 feature 모듈들 - 실제 구현 예시

---

## 💡 성공 사례

이 패턴을 적용하면:

✅ **테스트 가능성 향상** - MutateHandler는 순수 함수
✅ **명확한 데이터 흐름** - Action → Mutate → State
✅ **비즈니스 로직 분리** - ViewModel은 단순 위임자
✅ **확장 가능한 구조** - Feature 단위 모듈화
✅ **의존성 규칙 강제** - Convention Plugins로 자동화

---

**Happy Refactoring! 🚀**
