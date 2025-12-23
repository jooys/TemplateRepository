package com.jooys.template.feature.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.jooys.template.domain.photo.usecase.GetFavoriteItemDetailUseCase
import com.jooys.template.domain.photo.usecase.GetPhotoDetailUseCase
import com.jooys.template.domain.photo.usecase.SaveAndDeleteFavoriteItemUseCase
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf

class DetailMutateHandlerTest : BehaviorSpec() {
    val savedStateHandle = mockk<SavedStateHandle>(relaxed = true)
    val getPhotoDetailUseCase = mockk<GetPhotoDetailUseCase>(relaxed = true)
    val getFavoriteItemDetailUseCase = mockk<GetFavoriteItemDetailUseCase>(relaxed = true)
    val saveAndDeleteFavoriteItemUseCase = mockk<SaveAndDeleteFavoriteItemUseCase>(relaxed = true)


    init {
        Given("상세화면에") {
            val handler = DetailMutateHandler(
                savedStateHandle = savedStateHandle,
                getPhotoDetailUseCase = getPhotoDetailUseCase,
                getFavoriteItemDetailUseCase = getFavoriteItemDetailUseCase,
                saveAndDeleteFavoriteItemUseCase = saveAndDeleteFavoriteItemUseCase
            )
            When("처음 진입하게 되면") {
                val action = DetailAction.OnViewCreated
                When("이미지 데이터를 성공적으로 불러왔다면") {
                    every { getPhotoDetailUseCase.invoke(any()) } returns flowOf(
                        Result.success(mockk(relaxed = true))
                    )
                    When("로컬 데이터에 북마크 정보를 성공적으로 불러왔다면") {
                        every { getFavoriteItemDetailUseCase.invoke(any()) } returns flowOf(
                            Result.success(mockk(relaxed = true))
                        )
                        handler.mutate(action).test {
                            Then("로딩을 띄워준다.") { awaitItem().shouldBe(DetailMutate.Reduce.UpdateLoading(true)) }
                            Then("뷰 상태를 SHOW_DATA로 바꾼다") {
                                awaitItem().shouldBe(DetailMutate.Reduce.UpdateViewState(DetailMutate.State.ViewState.SHOW_DATA))
                            }
                            Then("이미지 데이터를 세팅한다.") {
                                awaitItem().shouldBeInstanceOf<DetailMutate.Reduce.UpdateImageData>()
                            }
                            Then("로딩을 닫는다.") { awaitItem().shouldBe(DetailMutate.Reduce.UpdateLoading(false)) }
                            awaitComplete()
                        }
                    }
                    When("로컬 데이터에 북마크 정보를 가져오지 못했다면") {
                        every { getFavoriteItemDetailUseCase.invoke(any()) } returns flowOf(
                            Result.failure(mockk(relaxed = true))
                        )
                        handler.mutate(action).test {
                            Then("로딩을 띄워준다.") { awaitItem().shouldBe(DetailMutate.Reduce.UpdateLoading(true)) }
                            Then("뷰 상태를 EMPTY로 바꾼다") {
                                awaitItem().shouldBe(DetailMutate.Reduce.UpdateViewState(DetailMutate.State.ViewState.EMPTY))
                            }
                            Then("Snackbar에 에러 메시지를 보여준다.") {
                                awaitItem().shouldBeInstanceOf<DetailMutate.SideEffect.ShowSnackBar>()
                            }
                            Then("로딩을 닫는다.") { awaitItem().shouldBe(DetailMutate.Reduce.UpdateLoading(false)) }
                            awaitComplete()
                        }
                    }
                }
                When("이미지 데이터를 가져오지 못했다면") {
                    every { getPhotoDetailUseCase.invoke(any()) } returns flowOf(
                        Result.failure(mockk(relaxed = true))
                    )
                    And("로컬 데이터에 북마크 정보를 성공적으로 불러왔다면") {
                        every { getFavoriteItemDetailUseCase.invoke(any()) } returns flowOf(
                            Result.success(mockk(relaxed = true))
                        )
                        handler.mutate(action).test {
                            Then("로딩을 띄워준다.") { awaitItem().shouldBe(DetailMutate.Reduce.UpdateLoading(true)) }
                            Then("뷰 상태를 EMPTY로 바꾼다") {
                                awaitItem().shouldBe(DetailMutate.Reduce.UpdateViewState(DetailMutate.State.ViewState.EMPTY))
                            }
                            Then("Snackbar에 에러 메시지를 보여준다.") {
                                awaitItem().shouldBeInstanceOf<DetailMutate.SideEffect.ShowSnackBar>()
                            }
                            Then("로딩을 닫는다.") { awaitItem().shouldBe(DetailMutate.Reduce.UpdateLoading(false)) }
                            awaitComplete()
                        }
                    }
                }
                When("이미지 데이터를 가져오지 못했고") {
                    every { getPhotoDetailUseCase.invoke(any()) } returns flowOf(
                        Result.failure(mockk(relaxed = true))
                    )
                    And("로컬 데이터에 북마크 정보도 가져오지 못했다면") {
                        every { getFavoriteItemDetailUseCase.invoke(any()) } returns flowOf(
                            Result.failure(mockk(relaxed = true))
                        )
                        handler.mutate(action).test {
                            Then("로딩을 띄워준다.") { awaitItem().shouldBe(DetailMutate.Reduce.UpdateLoading(true)) }
                            Then("뷰 상태를 EMPTY로 바꾼다") {
                                awaitItem().shouldBe(DetailMutate.Reduce.UpdateViewState(DetailMutate.State.ViewState.EMPTY))
                            }
                            Then("Snackbar에 에러 메시지를 보여준다.") {
                                awaitItem().shouldBeInstanceOf<DetailMutate.SideEffect.ShowSnackBar>()
                            }
                            Then("로딩을 닫는다.") { awaitItem().shouldBe(DetailMutate.Reduce.UpdateLoading(false)) }
                            awaitComplete()
                        }
                    }
                }
            }
            When("뒤로가기 버튼을 누르게 되면") {
                val action = DetailAction.OnClickBack
                handler.mutate(action).test {
                    Then("화면을 나간다") {
                        awaitItem().shouldBeInstanceOf<DetailMutate.SideEffect.Finish>()
                        awaitComplete()
                    }
                }
            }
            When("북마크 버튼을 클릭할 때 이미 북마크가 되어있을때") {
                val action = DetailAction.OnClickBookmark(DetailMutate.State.ImageData().copy(isBookmark = true))
                And("성공적으로 로컬에 저장이 된다면") {
                    every { saveAndDeleteFavoriteItemUseCase.invoke(any(), any(), any()) } returns flowOf(
                        Result.success(mockk(relaxed = true))
                    )
                    handler.mutate(action).test {
                        Then("로딩을 띄워준다.") { awaitItem().shouldBe(DetailMutate.Reduce.UpdateLoading(true)) }
                        Then("북마크에서 삭제되었습니다. 라고 메시지가 나온다") {
                            awaitItem().shouldBe(DetailMutate.SideEffect.ShowSnackBar("북마크에서 삭제되었습니다."))
                        }
                        Then("화면의 북마크를 반전 시킨다") {
                            awaitItem().shouldBe(DetailMutate.Reduce.UpdateIsBookmark(false))
                        }
                        Then("로딩을 닫는다.") { awaitItem().shouldBe(DetailMutate.Reduce.UpdateLoading(false)) }
                        awaitComplete()
                    }
                }
                When("로컬 저장에 실패하면") {
                    every { saveAndDeleteFavoriteItemUseCase.invoke(any(), any(), any()) } returns flowOf(
                        Result.failure(mockk(relaxed = true))
                    )
                    handler.mutate(action).test {
                        Then("로딩을 띄워준다.") { awaitItem().shouldBe(DetailMutate.Reduce.UpdateLoading(true)) }
                        Then("Snackbar에 에러 메시지를 보여준다.") {
                            awaitItem().shouldBeInstanceOf<DetailMutate.SideEffect.ShowSnackBar>()
                        }
                        Then("로딩을 닫는다.") { awaitItem().shouldBe(DetailMutate.Reduce.UpdateLoading(false)) }
                        awaitComplete()
                    }
                }
            }
            When("북마크 버튼을 클릭할 때 이미 북마크가 되어있지 않을때") {
                val action = DetailAction.OnClickBookmark(DetailMutate.State.ImageData().copy(isBookmark = false))
                And("성공적으로 로컬에 저장이 된다면") {
                    every { saveAndDeleteFavoriteItemUseCase.invoke(any(), any(), any()) } returns flowOf(
                        Result.success(mockk(relaxed = true))
                    )
                    handler.mutate(action).test {
                        Then("로딩을 띄워준다.") { awaitItem().shouldBe(DetailMutate.Reduce.UpdateLoading(true)) }
                        Then("북마크에 저장되었습니다. 라고 메시지가 나온다") {
                            awaitItem().shouldBe(DetailMutate.SideEffect.ShowSnackBar("북마크에 저장되었습니다."))
                        }
                        Then("화면의 북마크를 반전 시킨다") {
                            awaitItem().shouldBe(DetailMutate.Reduce.UpdateIsBookmark(true))
                        }
                        Then("로딩을 닫는다.") { awaitItem().shouldBe(DetailMutate.Reduce.UpdateLoading(false)) }
                        awaitComplete()
                    }
                }
                When("로컬 저장에 실패하면") {
                    every { saveAndDeleteFavoriteItemUseCase.invoke(any(), any(), any()) } returns flowOf(
                        Result.failure(mockk(relaxed = true))
                    )
                    handler.mutate(action).test {
                        Then("로딩을 띄워준다.") { awaitItem().shouldBe(DetailMutate.Reduce.UpdateLoading(true)) }
                        Then("Snackbar에 에러 메시지를 보여준다.") {
                            awaitItem().shouldBeInstanceOf<DetailMutate.SideEffect.ShowSnackBar>()
                        }
                        Then("로딩을 닫는다.") { awaitItem().shouldBe(DetailMutate.Reduce.UpdateLoading(false)) }
                        awaitComplete()
                    }
                }
            }
        }
    }
}
