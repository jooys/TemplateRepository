# CLAUDE.md

이 파일은 Claude Code (claude.ai/code)가 이 저장소에서 작업할 때 참고할 가이드를 제공합니다.

## 자주 사용하는 명령어

### 빌드
```bash
./gradlew build              # 전체 프로젝트 빌드
./gradlew assembleDebug      # 디버그 APK만 빌드
./gradlew assembleRelease    # 릴리즈 APK 빌드
```

### 테스트
```bash
./gradlew test                                  # 모든 유닛 테스트 실행
./gradlew testDebugUnitTest                     # 디버그 유닛 테스트만 실행
./gradlew :feature:search:test                  # 특정 모듈의 테스트 실행
./gradlew :feature:detail:testDebugUnitTest     # 단일 모듈의 디버그 테스트 실행
```

### 기타 작업
```bash
./gradlew clean                    # 빌드 산출물 정리
./gradlew lint                     # Lint 검사 실행
./gradlew check                    # 모든 검사 실행 (테스트 + Lint)
./gradlew assertModuleGraph        # 모듈 의존성 규칙 검증
./generateModuleGraphs.sh          # SVG 의존성 그래프 생성
```

## 아키텍처 개요

### 모듈 구조

레이어로 구성된 16개의 모듈을 가진 **모듈형 클린 아키텍처 안드로이드 앱**입니다:

**핵심 레이어:**
- **app** - 애플리케이션 진입점, 모든 모듈에 의존
- **domain** - 순수 Kotlin 비즈니스 로직 레이어 (유스케이스, 리포지토리 인터페이스)
- **data** - 리포지토리 구현체 및 데이터 소스 추상화
- **model** - 공유 데이터 모델 (순수 Kotlin, Kotlin Serialization 사용)
- **remote** - 네트워크 구현 (Retrofit, Unsplash API)
- **local** - 데이터베이스 구현 (Room)
- **core** - 공유 UI 컴포넌트 및 테마

**기능 모듈:**
- **feature:search** + **feature:search:navigation** - 사진 검색 화면
- **feature:detail** + **feature:detail:navigation** - 사진 상세 화면
- **feature:bookmark** + **feature:bookmark:navigation** - 북마크 화면

### 의존성 규칙

**`assertModuleGraph` 플러그인으로 강제되는 엄격한 레이어링:**

```
Features → domain + model + core (절대 data/remote/local 의존 불가)
domain → model만 의존 (순수 Kotlin, Android 의존성 없음)
data → domain + model
remote/local → data + model
```

**내비게이션 패턴:** 각 기능은 인터페이스 정의만 포함하는 별도의 `navigation` 모듈을 가집니다. 이는 순환 의존성을 방지하면서 기능 간 내비게이션을 허용합니다.

### MVI 아키텍처 패턴

모든 기능은 **"Mutate-Reduce" MVI 패턴**을 따릅니다:

**기능별 핵심 컴포넌트:**
1. **Action** - 사용자 이벤트 (sealed interface)
2. **State** - UI 상태 (data class)
3. **Mutate** - 다음을 포함:
   - **Reduce** - 상태 변경
   - **SideEffect** - 일회성 이벤트 (내비게이션, 스낵바)
4. **MutateHandler** - 비즈니스 로직 처리기 (`Flow<Mutate>` 반환)
5. **ViewModel** - 상태 홀더 (Reduce/SideEffect 스트림 분리)

**데이터 흐름:**
```
User Action → ViewModel.onAction()
→ MutateHandler.mutate(action, currentState)
→ Flow<Mutate> (Reduce | SideEffect)
→ ViewModel이 StateFlow/SharedFlow 업데이트
→ UI가 관찰하고 반응
```

**기능별 파일 패턴:**
```
feature/[name]/
├── [Name]Activity.kt         # Compose Activity
├── [Name]Route.kt            # 메인 composable
├── [Name]ViewModel.kt        # MVI 상태 홀더
├── [Name]Mutate.kt           # Reduce + SideEffect 정의
├── [Name]MutateHandler.kt    # 비즈니스 로직
├── [Name]Action.kt           # 사용자 액션
└── [Name]NavigationImpl.kt   # 내비게이션 구현
```

### 의존성 주입 (Hilt)

**모듈 구성:**
- **SingletonComponent** - Repositories, UseCases, Network (Retrofit/OkHttp), Database (Room)
- **ActivityComponent** - Navigation 구현체

**바인딩 전략:**
- 인터페이스 → 구현체는 `@Binds` 사용
- 서드파티 객체(Retrofit, Room, OkHttp)는 `@Provides` 사용
- 각 레이어는 자체 Hilt 모듈을 가짐

### 에러 처리 패턴

유스케이스는 리포지토리 결과를 `Result<T>`로 래핑합니다:
```kotlin
fun invoke(...) = flow {
    val result = repository.doSomething(...)
    emit(Result.success(result))
}.catch {
    emit(Result.failure(it))
}
```

MutateHandler는 성공/실패를 모두 처리하고 적절한 Mutate 이벤트를 방출합니다:
```kotlin
.onStart { emit(UpdateLoading(true)) }
.collect { result ->
    result.onSuccess { data ->
        emit(UpdateData(data))  // Reduce
    }.onFailure { error ->
        emit(ShowSnackBar(error.message))  // SideEffect
    }
}
.onCompletion { emit(UpdateLoading(false)) }
```

### 테스트 접근법

**프레임워크:** Kotest BehaviorSpec (BDD 스타일) + MockK + Turbine

**테스트 대상:**
- **MutateHandler만** 테스트 (순수 비즈니스 로직)
- ViewModel은 단순 위임자 (테스트하지 않음)
- 테스트는 상태 전환, 부수 효과, 로딩 상태, 에러 처리를 검증

**테스트 구조 패턴:**
```kotlin
class FeatureMutateHandlerTest : BehaviorSpec() {
    init {
        Given("컨텍스트") {
            val handler = FeatureMutateHandler(mockUseCase)

            When("액션 발생") {
                handler.mutate(action, state).test {
                    Then("기대 결과") {
                        awaitItem().shouldBe(expectedMutate)
                        awaitComplete()
                    }
                }
            }
        }
    }
}
```

**참고:** 테스트는 비즈니스 요구사항에 맞춰 한국어로 작성됩니다.

## Convention 플러그인

`build-logic/convention/`의 커스텀 Gradle convention 플러그인이 일관된 설정을 제공합니다:

- **custom.feature.library** - 기능 모듈 템플릿 (domain/model/core 자동 의존, Kotest 설정)
- **custom.navigation.library** - 내비게이션 모듈 템플릿
- **custom.android.library** - 기본 안드로이드 라이브러리
- **custom.android.library.compose** - Compose 지원 라이브러리
- **custom.android.hilt** - Hilt DI 설정
- **custom.jvm.library** - 순수 Kotlin 모듈 (domain, model)
- **custom.application** - 앱 모듈 설정

이 플러그인들은 일관된 구조를 강제하고 build.gradle.kts 중복을 제거합니다.

## 새 기능 추가하기

1. 모듈 생성:
   ```bash
   mkdir -p feature/newfeature/navigation/src/main/java/com/jooys/template/feature/newfeature/navigation
   mkdir -p feature/newfeature/src/main/java/com/jooys/template/feature/newfeature
   mkdir -p feature/newfeature/src/test/java/com/jooys/template/feature/newfeature
   ```

2. `settings.gradle.kts`에 추가:
   ```kotlin
   include(":feature:newfeature")
   include(":feature:newfeature:navigation")
   ```

3. `feature/newfeature/build.gradle.kts` 생성:
   ```kotlin
   plugins {
       id("custom.feature.library")
   }

   android {
       namespace = "com.jooys.template.feature.newfeature"
   }

   dependencies {
       implementation(projects.feature.newfeature.navigation)
       // 필요한 다른 기능 navigation 의존성 추가
   }
   ```

4. `feature/newfeature/navigation/build.gradle.kts` 생성:
   ```kotlin
   plugins {
       id("custom.navigation.library")
   }

   android {
       namespace = "com.jooys.template.feature.newfeature.navigation"
   }
   ```

5. MVI 패턴에 따라 구현 (기존 기능 참조)

6. `app/build.gradle.kts`에 새 기능 의존성 추가

## 주요 기술

- **언어:** Kotlin 2.2.21
- **UI:** Jetpack Compose (BOM 2025.11.00) + Material 3
- **DI:** Hilt 2.57.2
- **데이터베이스:** Room 2.8.3
- **네트워크:** Retrofit 2.11.0 + OkHttp 5.1.0
- **직렬화:** Kotlin Serialization
- **이미지 로딩:** Coil 3.3.0
- **테스팅:** Kotest 5.9.1 + MockK 1.13.17 + Turbine 1.2.0
- **최소 SDK:** 26 (Android 8.0)

## 중요 사항

- **domain과 model 모듈은 순수 Kotlin** - Android 의존성 불허
- **기능 모듈은 data/remote/local에 의존하면 안 됨** - domain/model/core만 허용
- **내비게이션은 Activity 기반 접근법 사용** - Jetpack Compose Navigation 미사용
- **테스트는 한국어로 작성** - 비즈니스 요구사항이 한국어
- **각 기능은 동일한 구조를 따름** - convention 플러그인으로 일관성 강제
- **모듈 의존성 규칙은 검증됨** - `./gradlew assertModuleGraph`로 확인

---

## 🔄 다른 프로젝트를 이 아키텍처로 리팩토링하기

사용자가 다른 프로젝트를 이 아키텍처 패턴으로 리팩토링하고 싶다면, 다음 단계를 따라 진행하세요.

### 리팩토링 프로세스

#### 1단계: 현재 프로젝트 분석
먼저 사용자의 프로젝트를 분석하여:
- 현재 아키텍처 패턴 파악 (MVC, MVP, MVVM 등)
- 모듈 구조 확인
- 데이터 레이어 구조 파악
- 의존성 주입 방식 확인
- 테스트 커버리지 확인

#### 2단계: 마이그레이션 플랜 수립
**자동으로 TodoWrite를 사용하여 마이그레이션 작업 목록을 생성하세요:**

```
1. Convention Plugins 설정
2. 모듈 구조 재구성
3. Domain Layer 분리
4. Data Layer 리팩토링
5. Feature를 MVI 패턴으로 변환
6. Hilt DI 마이그레이션
7. 테스트 작성
```

#### 3단계: Convention Plugins 생성

`build-logic/convention/` 디렉토리를 이 프로젝트에서 복사하여 사용자 프로젝트에 적용:

**필수 파일들:**
- `CustomAndroidFeatureLibraryPlugin.kt` - Feature 모듈 템플릿
- `CustomNavigationLibraryPlugin.kt` - Navigation 모듈 템플릿
- `CustomAndroidLibraryPlugin.kt` - 기본 라이브러리
- `CustomJvmLibraryPlugin.kt` - 순수 Kotlin 모듈
- `CustomAndroidHiltPlugin.kt` - Hilt DI 설정

패키지명을 사용자 프로젝트에 맞게 변경하세요.

#### 4단계: 모듈 재구성

**기존 단일 모듈을 다음과 같이 분리:**

```bash
# Core Layers
mkdir -p domain/src/main/java
mkdir -p model/src/main/java
mkdir -p data/src/main/java
mkdir -p remote/src/main/java
mkdir -p local/src/main/java
mkdir -p core/src/main/java

# Feature Modules (기존 화면들을 각각 feature로)
mkdir -p feature/[screen-name]/src/main/java
mkdir -p feature/[screen-name]/navigation/src/main/java
mkdir -p feature/[screen-name]/src/test/java
```

#### 5단계: Domain Layer 분리

**기존 코드에서 다음을 domain으로 이동:**

1. **Repository 인터페이스 추출**
   ```kotlin
   // 기존 RepositoryImpl에서 인터페이스만 추출
   // data/XxxRepositoryImpl.kt -> domain/repository/XxxRepository.kt
   ```

2. **UseCase 생성**
   ```kotlin
   // 기존 ViewModel의 비즈니스 로직을 UseCase로 분리
   // viewModel에 있던 repository 호출 로직 -> domain/usecase/
   ```

**중요:** domain과 model은 순수 Kotlin이어야 하며, Android 의존성이 없어야 합니다.

#### 6단계: Feature를 MVI 패턴으로 변환

**각 화면(Activity/Fragment)을 다음과 같이 변환:**

1. **Action 정의**
   ```kotlin
   sealed interface [Screen]Action {
       data object OnViewCreated : [Screen]Action
       data class OnItemClick(val id: String) : [Screen]Action
       // 기존 사용자 이벤트를 Action으로 정의
   }
   ```

2. **Mutate 정의**
   ```kotlin
   sealed interface [Screen]Mutate {
       sealed interface Reduce : [Screen]Mutate {
           // 상태 변경 (기존 LiveData/StateFlow 업데이트를 Reduce로)
           data class UpdateData(...) : Reduce
       }

       sealed interface SideEffect : [Screen]Mutate {
           // 일회성 이벤트 (Toast, Navigation, Dialog 등)
           data class ShowToast(val message: String) : SideEffect
       }

       data class State(
           // 기존 ViewModel의 상태를 하나의 State로 통합
       )
   }
   ```

3. **MutateHandler 생성**
   ```kotlin
   class [Screen]MutateHandler @Inject constructor(
       // 기존 ViewModel의 UseCase 의존성
   ) {
       fun mutate(action: [Screen]Action, state: [Screen]Mutate.State): Flow<[Screen]Mutate> = flow {
           when (action) {
               is [Screen]Action.OnViewCreated -> {
                   // 기존 init {} 또는 onCreate 로직
                   useCase.invoke()
                       .onStart { emit(Reduce.UpdateLoading(true)) }
                       .onCompletion { emit(Reduce.UpdateLoading(false)) }
                       .collect { result ->
                           result.onSuccess { data ->
                               emit(Reduce.UpdateData(data))
                           }.onFailure { error ->
                               emit(SideEffect.ShowError(error.message))
                           }
                       }
               }
               // 다른 액션들...
           }
       }
   }
   ```

4. **ViewModel 간소화**
   ```kotlin
   @HiltViewModel
   class [Screen]ViewModel @Inject constructor(
       private val mutateHandler: [Screen]MutateHandler
   ) : ViewModel() {
       private val _state = MutableStateFlow([Screen]Mutate.State())
       val state: StateFlow<[Screen]Mutate.State> = _state.asStateFlow()

       private val _sideEffect = MutableSharedFlow<[Screen]Mutate.SideEffect>()
       val sideEffect: SharedFlow<[Screen]Mutate.SideEffect> = _sideEffect.asSharedFlow()

       fun onAction(action: [Screen]Action) {
           viewModelScope.launch {
               mutateHandler.mutate(action, _state.value).collect { mutate ->
                   when (mutate) {
                       is [Screen]Mutate.Reduce -> reduce(mutate)
                       is [Screen]Mutate.SideEffect -> _sideEffect.emit(mutate)
                   }
               }
           }
       }

       private fun reduce(reduce: [Screen]Mutate.Reduce) {
           _state.update { state ->
               when (reduce) {
                   // State 업데이트 로직
               }
           }
       }
   }
   ```

#### 7단계: UI를 Compose로 변환 (필요시)

**기존 XML을 Composable로 변환:**

1. **Route Composable 생성**
   ```kotlin
   @Composable
   fun [Screen]Route(
       viewModel: [Screen]ViewModel = hiltViewModel()
   ) {
       val state by viewModel.state.collectAsStateWithLifecycle()

       LaunchedEffect(Unit) {
           viewModel.onAction([Screen]Action.OnViewCreated)
       }

       LaunchedEffect(Unit) {
           viewModel.sideEffect.collect { sideEffect ->
               when (sideEffect) {
                   // SideEffect 처리
               }
           }
       }

       [Screen]Screen(state = state, onAction = viewModel::onAction)
   }
   ```

2. **Activity 간소화**
   ```kotlin
   @AndroidEntryPoint
   class [Screen]Activity : ComponentActivity() {
       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           setContent {
               AppTheme {
                   [Screen]Route()
               }
           }
       }
   }
   ```

#### 8단계: 테스트 작성

**각 MutateHandler에 대한 테스트 작성:**

```kotlin
class [Screen]MutateHandlerTest : BehaviorSpec() {
    init {
        val mockUseCase: [UseCase] = mockk()
        val handler = [Screen]MutateHandler(mockUseCase)

        Given("[화면]에") {
            val state = [Screen]Mutate.State()

            When("[액션 발생]") {
                val action = [Screen]Action.SomeAction

                coEvery { mockUseCase.invoke() } returns flow {
                    emit(Result.success(mockData))
                }

                handler.mutate(action, state).test {
                    Then("[기대 결과]") {
                        awaitItem() shouldBe [Screen]Mutate.Reduce.UpdateLoading(true)
                        awaitItem() shouldBe [Screen]Mutate.Reduce.UpdateData(expectedData)
                        awaitItem() shouldBe [Screen]Mutate.Reduce.UpdateLoading(false)
                        awaitComplete()
                    }
                }
            }
        }
    }
}
```

### 리팩토링 체크리스트

**사용자가 리팩토링을 요청하면 다음 순서로 진행:**

- [ ] 1. 프로젝트 분석 완료
- [ ] 2. TodoWrite로 마이그레이션 플랜 생성
- [ ] 3. build-logic 및 Convention Plugins 설정
- [ ] 4. Version Catalog 설정
- [ ] 5. domain 모듈 생성 및 인터페이스 분리
- [ ] 6. model 모듈 생성 및 데이터 모델 이동
- [ ] 7. data 모듈 생성 및 Repository 구현 이동
- [ ] 8. remote/local 모듈 생성
- [ ] 9. 첫 번째 Feature를 MVI 패턴으로 변환
- [ ] 10. MutateHandler 테스트 작성
- [ ] 11. 빌드 및 테스트 확인
- [ ] 12. 나머지 Feature들 순차적으로 변환
- [ ] 13. Navigation 모듈 분리
- [ ] 14. 최종 빌드 및 전체 테스트

### 리팩토링 시 주의사항

1. **점진적 마이그레이션**
   - 한 번에 모든 것을 변경하지 말고, Feature 단위로 순차적으로 마이그레이션
   - 각 단계마다 빌드 및 테스트 확인

2. **의존성 규칙 준수**
   - Feature → Domain (O)
   - Feature → Data (X) - 절대 안 됨
   - Domain은 순수 Kotlin (Android 의존성 X)

3. **기존 코드 보존**
   - 리팩토링 중에는 기존 코드를 주석 처리하고 새 코드 추가
   - 모든 테스트가 통과하면 기존 코드 삭제

4. **테스트 우선**
   - 리팩토링 전에 기존 기능에 대한 테스트 작성
   - 리팩토링 후 동일한 테스트가 통과하는지 확인

### 자동 리팩토링 지침

**사용자가 "이 프로젝트를 Mutate-Reduce MVI 패턴으로 리팩토링해줘"라고 요청하면:**

1. **EnterPlanMode 사용**
   - 프로젝트 구조 분석
   - 마이그레이션 플랜 작성
   - 사용자 승인 받기

2. **TodoWrite 활용**
   - 모든 마이그레이션 작업을 Todo로 생성
   - 각 단계별로 진행 상황 업데이트

3. **단계별 실행**
   - Convention Plugins부터 시작
   - Core Layer 모듈 생성
   - Feature 하나씩 변환
   - 각 단계마다 빌드 확인

4. **테스트 작성**
   - MutateHandler부터 테스트
   - 기존 기능 회귀 테스트

5. **문서화**
   - 변경된 아키텍처 설명
   - 마이그레이션 과정 기록

### 참고 자료

리팩토링에 대한 자세한 가이드는 `ARCHITECTURE_GUIDE.md` 파일을 참조하세요.
