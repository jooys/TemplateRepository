package com.jooys.template.feature.bookmark

import app.cash.turbine.test
import com.jooys.template.domain.photo.usecase.GetFavoriteItemListUseCase
import com.jooys.template.domain.photo.usecase.SaveAndDeleteFavoriteItemUseCase
import com.jooys.template.model.photo.FavoriteItemLocalEntity
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.scopes.BehaviorSpecWhenContainerScope
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf

class BookmarkMutateHandlerTest : BehaviorSpec() {
    val getFavoriteItemListUseCase = mockk<GetFavoriteItemListUseCase>(relaxed = true)
    val saveAndDeleteFavoriteItemUseCase = mockk<SaveAndDeleteFavoriteItemUseCase>(relaxed = true)

    init {
        Given("북마크 화면에") {
            val handler = BookmarkMutateHandler(
                getFavoriteItemListUseCase = getFavoriteItemListUseCase,
                saveAndDeleteFavoriteItemUseCase = saveAndDeleteFavoriteItemUseCase
            )
            When("처음 진입하고") {
                val action = BookmarkAction.OnViewCreated
                responseBookmarkList(handler, action)
            }
            When("화면을 새로고침 해아한다면") {
                val action = BookmarkAction.OnRefresh
                responseBookmarkList(handler, action)
            }
            When("뒤로가기 버튼을 누르면") {
                val action = BookmarkAction.OnClickBackButton
                handler.mutate(action).test {
                    Then("화면을 나간다.") {
                        awaitItem().shouldBeInstanceOf<BookmarkMutate.SideEffect.Finish>()
                        awaitComplete()
                    }
                }

            }
            When("이미지 아이템을 누르면") {
                val action = BookmarkAction.OnClickItem("")
                handler.mutate(action).test {
                    Then("상세 화면으로 넘어간다") {
                        awaitItem().shouldBeInstanceOf<BookmarkMutate.SideEffect.NaviToDetail>()
                        awaitComplete()
                    }
                }
            }
            When("북마크 버튼을 클릭하고 ") {
                val action = BookmarkAction.OnItemClickBookmark(mockk(relaxed = true))
                When("성공적으로 로컬에 저장이 된다면") {
                    every { saveAndDeleteFavoriteItemUseCase.invoke(any(), any(), any()) } returns flowOf(
                        Result.success(mockk(relaxed = true))
                    )
                    When("로컬 데이터에 북마크 정보를 성공적으로 불러왔고 리스트가 비어있다면") {
                        val emptyList = emptyList<FavoriteItemLocalEntity>()

                        every { getFavoriteItemListUseCase.invoke() } returns
                                flowOf(Result.success(emptyList))

                        handler.mutate(action).test {
                            Then("로딩을 보여준다") {
                                awaitItem().shouldBe(BookmarkMutate.Reduce.UpdateLoading(true))
                            }
                            Then("북마크에서 삭제되었습니다. 라고 메시지가 나온다") {
                                awaitItem().shouldBe(BookmarkMutate.SideEffect.ShowSnackBar("북마크에서 삭제되었습니다."))
                            }
                            Then("뷰 상태를 EMPTY 로 변경한다") {
                                awaitItem().shouldBe(
                                    BookmarkMutate.Reduce.UpdateViewState(
                                        BookmarkMutate.State.ViewState.EMPTY
                                    )
                                )
                            }
                            Then("로딩을 닫는다") {
                                awaitItem().shouldBe(BookmarkMutate.Reduce.UpdateLoading(false))
                            }
                            awaitComplete()
                        }
                    }
                    When("로컬 데이터에 북마크 정보를 성공적으로 불러왔고 리스트가 채워져 있다면") {
                        val filledList = listOf(mockk<FavoriteItemLocalEntity>(relaxed = true))

                        every { getFavoriteItemListUseCase.invoke() } returns
                                flowOf(Result.success(filledList))

                        handler.mutate(action).test {
                            Then("로딩을 보여준다") {
                                awaitItem().shouldBe(BookmarkMutate.Reduce.UpdateLoading(true))
                            }
                            Then("북마크에서 삭제되었습니다. 라고 메시지가 나온다") {
                                awaitItem().shouldBe(BookmarkMutate.SideEffect.ShowSnackBar("북마크에서 삭제되었습니다."))
                            }
                            Then("뷰 상태를 SHOW_DATA 로 변경한다") {
                                awaitItem().shouldBe(
                                    BookmarkMutate.Reduce.UpdateViewState(
                                        BookmarkMutate.State.ViewState.SHOW_DATA
                                    )
                                )
                            }
                            Then("이미지 리스트를 세팅한다 ") {
                                awaitItem().shouldBeInstanceOf<BookmarkMutate.Reduce.UpdateImageList>()
                            }
                            Then("로딩을 닫는다") {
                                awaitItem().shouldBe(BookmarkMutate.Reduce.UpdateLoading(false))
                            }
                            awaitComplete()
                        }
                    }
                }
                When("로컬 저장에 실패하면") {
                    every { saveAndDeleteFavoriteItemUseCase.invoke(any(), any(), any()) } returns flowOf(
                        Result.failure(mockk(relaxed = true))
                    )

                    every { getFavoriteItemListUseCase.invoke() } returns
                            flowOf(Result.failure(mockk(relaxed = true)))
                    handler.mutate(action).test {
                        Then("로딩을 띄워준다.") { awaitItem().shouldBe(BookmarkMutate.Reduce.UpdateLoading(true)) }
                        Then("뷰 상태를 EMPTY 로 변경한다") {
                            awaitItem().shouldBe(
                                BookmarkMutate.Reduce.UpdateViewState(
                                    BookmarkMutate.State.ViewState.EMPTY
                                )
                            )
                        }
                        Then("Snackbar에 에러 메시지를 보여준다.") {
                            awaitItem().shouldBeInstanceOf<BookmarkMutate.SideEffect.ShowSnackBar>()
                        }
                        Then("로딩을 닫는다.") { awaitItem().shouldBe(BookmarkMutate.Reduce.UpdateLoading(false)) }
                        awaitComplete()
                    }
                }
            }
        }
    }

    private suspend fun BehaviorSpecWhenContainerScope.responseBookmarkList(
        handler: BookmarkMutateHandler,
        action: BookmarkAction,
    ) {
        When("로컬 데이터에 북마크 정보를 불러오지 못했다면") {
            every { getFavoriteItemListUseCase.invoke() } returns
                    flowOf(Result.failure(mockk(relaxed = true)))

            handler.mutate(action).test {
                Then("로딩을 보여준다") {
                    awaitItem().shouldBe(BookmarkMutate.Reduce.UpdateLoading(true))
                }
                Then("뷰 상태를 EMPTY 로 변경한다") {
                    awaitItem().shouldBe(
                        BookmarkMutate.Reduce.UpdateViewState(
                            BookmarkMutate.State.ViewState.EMPTY
                        )
                    )
                }
                Then("스낵바에 에러메시지를 보여준다.") {
                    awaitItem().shouldBeInstanceOf<BookmarkMutate.SideEffect.ShowSnackBar>()
                }
                Then("로딩을 닫는다") {
                    awaitItem().shouldBe(BookmarkMutate.Reduce.UpdateLoading(false))
                }
                awaitComplete()
            }
        }
        When("로컬 데이터에 북마크 정보를 성공적으로 불러왔고 리스트가 비어있다면") {
            val emptyList = emptyList<FavoriteItemLocalEntity>()

            every { getFavoriteItemListUseCase.invoke() } returns
                    flowOf(Result.success(emptyList))

            handler.mutate(action).test {
                Then("로딩을 보여준다") {
                    awaitItem().shouldBe(BookmarkMutate.Reduce.UpdateLoading(true))
                }
                Then("뷰 상태를 EMPTY 로 변경한다") {
                    awaitItem().shouldBe(
                        BookmarkMutate.Reduce.UpdateViewState(
                            BookmarkMutate.State.ViewState.EMPTY
                        )
                    )
                }
                Then("로딩을 닫는다") {
                    awaitItem().shouldBe(BookmarkMutate.Reduce.UpdateLoading(false))
                }
                awaitComplete()
            }
        }
        When("로컬 데이터에 북마크 정보를 성공적으로 불러왔고 리스트가 채워져 있다면") {
            val filledList = listOf(mockk<FavoriteItemLocalEntity>(relaxed = true))

            every { getFavoriteItemListUseCase.invoke() } returns
                    flowOf(Result.success(filledList))

            handler.mutate(action).test {
                Then("로딩을 보여준다") {
                    awaitItem().shouldBe(BookmarkMutate.Reduce.UpdateLoading(true))
                }
                Then("뷰 상태를 SHOW_DATA 로 변경한다") {
                    awaitItem().shouldBe(
                        BookmarkMutate.Reduce.UpdateViewState(
                            BookmarkMutate.State.ViewState.SHOW_DATA
                        )
                    )
                }
                Then("이미지 리스트를 세팅한다 ") {
                    awaitItem().shouldBeInstanceOf<BookmarkMutate.Reduce.UpdateImageList>()
                }
                Then("로딩을 닫는다") {
                    awaitItem().shouldBe(BookmarkMutate.Reduce.UpdateLoading(false))
                }
                awaitComplete()
            }
        }
    }
}
