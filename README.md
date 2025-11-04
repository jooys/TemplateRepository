# 안드로이드 템플릿 프로젝트

각 과제들을 수행 할 템플릿 프로젝트

## 📌 개요

`TemplateRepository`는 Android 애플리케이션 개발을 위한 멀티 모듈 구조의 템플릿 프로젝트입니다.<br>
`Clean Architecture`와 `Gradle Build Logic`을 기반으로, 재사용성과 확장성을 고려하여 설계되었습니다.

## 개발 환경

Android Studio Narwhal Feature Drop | 2025.1.2<br>
MAC OS : macOS sequoia 15.6.1

## 어플리케이션 버전

`Android Gradle Platform` : 8.12.1 <br>
`kotlin` : 2.2.10 <br>
`minSdkVersion` : API 28 (Android 9.0) <br>
`targetSdkVersion` : API 36 (Android 16) <br>
`compileSdkVersion` : API 36 (Android 16) <br>

## 🧩 기술 스택

- **언어:** Kotlin
- **UI:** Jetpack Compose, Material3
- **DI:** Hilt
- **비즈니스 계층:** Clean Architecture (domain / data / app)
- **빌드:** Gradle Kotlin DSL + Custom build-logic

------------------------------------------------------------------------

## 🏗️ 프로젝트 구조

    TemplateRepository
     ┣ app/                # 실제 실행되는 Application 모듈
     ┣ core-design/        # 디자인 시스템 (UI 컴포넌트, 테마)
     ┣ data/               # 데이터 계층 (API, DB, Repository 구현체)
     ┣ domain/             # 도메인 계층 (UseCase, Repository 인터페이스, 비즈니스 로직)
     ┣ build-logic/        # Gradle 빌드 로직 모듈 (플러그인, convention 설정)
     ┣ model/             # API 모델 구성 모듈 (Entity 정의)
     ┣ gradle/             # Gradle wrapper 설정
     ┣ build.gradle.kts    # 프로젝트 전체 빌드 스크립트
     ┣ settings.gradle.kts # 모듈 포함 및 Gradle 세팅
     ┗ gradle.properties   # 공통 Gradle 속성

------------------------------------------------------------------------

## 📦 모듈 설명

### 1. `app`

- 실제 실행되는 Android Application 모듈
- `Hilt` 기반 의존성 주입, `Compose` 기반 UI를 포함
- `data`, `domain`, `core-design` 모듈을 의존하여 최종 앱을 구성

------------------------------------------------------------------------

### 2. `core-design`

- **디자인 시스템 모듈**
- 프로젝트에서 공통으로 사용하는 UI 컴포넌트, 스타일, 테마 정의
- Material3 + Custom Theme 적용
- 공용 Button, TextField, Toolbar 등 재사용 가능한 Compose 컴포넌트 확장 가능

------------------------------------------------------------------------

### 3. `domain`

- **비즈니스 로직 계층**
- `UseCase`, `Repository 인터페이스` 정의
- UI(`app`) ↔ Data(`data`) 사이에서 중간 계층 역할 수행
- Android 프레임워크에 의존하지 않음 (순수 Kotlin 모듈)

------------------------------------------------------------------------

### 4. `data`

- **데이터 계층**
- `Repository` 구현체 제공
- `Remote API`, `Local DB(Room)` 등의 실제 데이터 소스 관리
- `domain` 모듈의 인터페이스를 구현하여 의존성 역전 원칙(DIP) 준수

------------------------------------------------------------------------

### 5. `build-logic`

- Gradle Convention Plugin 모듈
- 공통된 `build.gradle.kts` 설정을 모듈별로 일일이 작성하지 않고,
  `build-logic`에서 관리하여 일관성 유지
- 예: Kotlin 설정, Android 설정, Lint/Code Style 등

------------------------------------------------------------------------

### 6. `model`

- `Model Entity` 정의 모듈
- API에서 사용하는 Model 정의

------------------------------------------------------------------------
