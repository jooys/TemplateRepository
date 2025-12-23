package com.jooys.template.feature.detail

import app.cash.turbine.test
import com.jooys.template.domain.photo.usecase.GetFavoriteItemListUseCase
import com.jooys.template.domain.photo.usecase.GetSearchPhotoUseCase
import com.jooys.template.domain.photo.usecase.SaveAndDeleteFavoriteItemUseCase
import com.jooys.template.model.photo.FavoriteItemLocalEntity
import com.jooys.template.model.photo.PhotoEntity
import com.jooys.template.model.photo.ResultEntity
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf

class SearchMutateHandlerTest : BehaviorSpec() {
    val getSearchPhotoUseCase = mockk<GetSearchPhotoUseCase>(relaxed = true)
    val getFavoriteItemListUseCase = mockk<GetFavoriteItemListUseCase>(relaxed = true)
    val saveAndDeleteFavoriteItemUseCase = mockk<SaveAndDeleteFavoriteItemUseCase>(relaxed = true)


    init {
        Given("검색 화면에") {
            val handler = SearchMutateHandler(
                getSearchPhotoUseCase = getSearchPhotoUseCase,
                getFavoriteItemListUseCase = getFavoriteItemListUseCase,
                saveAndDeleteFavoriteItemUseCase = saveAndDeleteFavoriteItemUseCase
            )
            val state = SearchMutate.State()
            When("처음 진입 하면") {
                val action = SearchAction.OnViewCreated
                handler.mutate(action, state).test {
                    Then("초기 화면을 보여준다") {
                        awaitItem().shouldBe(SearchMutate.Reduce.UpdateViewState(viewState = SearchMutate.State.ViewState.INITIALIZE))
                        awaitComplete()
                    }
                }
            }
            When("화면을 새로고침 해야할 때") {
                val action = SearchAction.OnRefreshBookmark
                When("로컬 데이터에 북마크 정보를 성공적으로 불러왔다면") {
                    val filledList = listOf(mockk<FavoriteItemLocalEntity>(relaxed = true))

                    every { getFavoriteItemListUseCase.invoke() } returns flowOf(
                        Result.success(filledList)
                    )

                    handler.mutate(action, state).test {
                        Then("이미지 리스트를 세팅한다 ") {
                            awaitItem().shouldBeInstanceOf<SearchMutate.Reduce.UpdateImageList>()
                        }
                        awaitComplete()
                    }
                }
                When("로컬 데이터에 북마크 정보를 불러오지 못했다면") {
                    every { getFavoriteItemListUseCase.invoke() } returns flowOf(
                        Result.failure(mockk(relaxed = true))
                    )

                    handler.mutate(action, state).test {
                        Then("스낵바에 에러메시지를 표시한다.") {
                            awaitItem().shouldBeInstanceOf<SearchMutate.SideEffect.ShowSnackBar>()
                        }
                        awaitComplete()
                    }
                }
            }
            When("텍스트가 입력될때마다") {
                val action = SearchAction.OnTextChanged("")
                handler.mutate(action, state).test {
                    Then("검색어 입력창에 입력한 텍스트가 보여진다") {
                        awaitItem().shouldBeInstanceOf<SearchMutate.Reduce.UpdateSearchText>()
                    }
                    awaitComplete()
                }
            }
            When("북마크 메뉴를 클릭 하면") {
                val action = SearchAction.OnClickBookmark
                handler.mutate(action, state).test {
                    Then("북마크 화면으로 넘어간다") {
                        awaitItem().shouldBeInstanceOf<SearchMutate.SideEffect.NaviToBookmark>()
                        awaitComplete()
                    }
                }
            }
            When("이미지를 클릭 하면") {
                val action = SearchAction.OnClickItem("")
                handler.mutate(action, state).test {
                    Then("상세 화면으로 넘어간다") {
                        awaitItem().shouldBeInstanceOf<SearchMutate.SideEffect.NaviToDetail>()
                        awaitComplete()
                    }
                }
            }
            When("북마크 버튼을 클릭할 때 이미 북마크가 되어있을때") {
                val action = SearchAction.OnItemClickBookmark(SearchMutate.State.ImageItem("", "").copy(isBookmark = true))
                And("성공적으로 로컬에 저장이 된다면") {
                    every { saveAndDeleteFavoriteItemUseCase.invoke(any(), any(), any()) } returns flowOf(
                        Result.success(mockk(relaxed = true))
                    )
                    handler.mutate(action, state).test {
                        Then("로딩을 띄워준다.") { awaitItem().shouldBe(SearchMutate.Reduce.UpdateLoading(true)) }
                        Then("북마크에서 삭제되었습니다. 라고 메시지가 나온다") {
                            awaitItem().shouldBe(SearchMutate.SideEffect.ShowSnackBar("북마크에서 삭제되었습니다."))
                        }
                        Then("화면을 새로고침 한다") {
                            awaitItem().shouldBeInstanceOf<SearchMutate.Reduce.UpdateImageList>()
                        }
                        Then("로딩을 닫는다.") { awaitItem().shouldBe(SearchMutate.Reduce.UpdateLoading(false)) }
                        awaitComplete()
                    }
                }
                When("로컬 저장에 실패하면") {
                    every { saveAndDeleteFavoriteItemUseCase.invoke(any(), any(), any()) } returns flowOf(
                        Result.failure(mockk(relaxed = true))
                    )
                    handler.mutate(action, state).test {
                        Then("로딩을 띄워준다.") { awaitItem().shouldBe(SearchMutate.Reduce.UpdateLoading(true)) }
                        Then("Snackbar에 에러 메시지를 보여준다.") {
                            awaitItem().shouldBeInstanceOf<SearchMutate.SideEffect.ShowSnackBar>()
                        }
                        Then("로딩을 닫는다.") { awaitItem().shouldBe(SearchMutate.Reduce.UpdateLoading(false)) }
                        awaitComplete()
                    }
                }
            }
            When("북마크 버튼을 클릭할 때 이미 북마크가 되어있지 않을때") {
                val action = SearchAction.OnItemClickBookmark(SearchMutate.State.ImageItem("", "").copy(isBookmark = false))
                And("성공적으로 로컬에 저장이 된다면") {
                    every { saveAndDeleteFavoriteItemUseCase.invoke(any(), any(), any()) } returns flowOf(
                        Result.success(mockk(relaxed = true))
                    )
                    handler.mutate(action, state).test {
                        Then("로딩을 띄워준다.") { awaitItem().shouldBe(SearchMutate.Reduce.UpdateLoading(true)) }
                        Then("북마크에 저장되었습니다. 라고 메시지가 나온다") {
                            awaitItem().shouldBe(SearchMutate.SideEffect.ShowSnackBar("북마크에 저장되었습니다."))
                        }
                        Then("화면의 북마크를 반전 시킨다") {
                            awaitItem().shouldBeInstanceOf<SearchMutate.Reduce.UpdateImageList>()
                        }
                        Then("로딩을 닫는다.") { awaitItem().shouldBe(SearchMutate.Reduce.UpdateLoading(false)) }
                        awaitComplete()
                    }
                }
                When("로컬 저장에 실패하면") {
                    every { saveAndDeleteFavoriteItemUseCase.invoke(any(), any(), any()) } returns flowOf(
                        Result.failure(mockk(relaxed = true))
                    )
                    handler.mutate(action, state).test {
                        Then("로딩을 띄워준다.") { awaitItem().shouldBe(SearchMutate.Reduce.UpdateLoading(true)) }
                        Then("Snackbar에 에러 메시지를 보여준다.") {
                            awaitItem().shouldBeInstanceOf<SearchMutate.SideEffect.ShowSnackBar>()
                        }
                        Then("로딩을 닫는다.") { awaitItem().shouldBe(SearchMutate.Reduce.UpdateLoading(false)) }
                        awaitComplete()
                    }
                }
            }
            When("검색어 입력 후 키보드 검색 버튼을 클릭 하고") {
                val searchText = "검색어"
                val page = 1
                val action = SearchAction.OnSearch(searchText)
                When("검색한 검색어의 검색 결과가 없다면 ") {
                    val emptyResult = mockk<ResultEntity<PhotoEntity.Response>>()
                    every { emptyResult.results } returns emptyList()

                    every { getSearchPhotoUseCase.invoke(searchText, page) } returns flowOf(
                        Result.success(emptyResult)
                    )
                    When("로컬 데이터에 북마크 정보를 성공적으로 불러왔다면") {
                        every { getFavoriteItemListUseCase.invoke() } returns flowOf(
                            Result.success(mockk(relaxed = true))
                        )
                    }
                    handler.mutate(action, state).test {
                        Then("로딩을 띄워준다.") { awaitItem().shouldBe(SearchMutate.Reduce.UpdateLoading(true)) }
                        Then("뷰 상태를 EMPTY 로 변경한다") {
                            awaitItem().shouldBe(SearchMutate.Reduce.UpdateViewState(SearchMutate.State.ViewState.EMPTY))
                        }
                        Then("로딩을 닫는다.") { awaitItem().shouldBe(SearchMutate.Reduce.UpdateLoading(false)) }
                        awaitComplete()
                    }
                }
                When("검색한 검색어의 검색 결과가 있다면 ") {
                    val result = mockk<ResultEntity<PhotoEntity.Response>>()
                    every { result.results } returns listOf(mockk<PhotoEntity.Response>(relaxed = true))
                    every { result.totalPages } returns 1

                    every { getSearchPhotoUseCase.invoke(searchText, page) } returns flowOf(
                        Result.success(result)
                    )
                    When("로컬 데이터에 북마크 정보를 성공적으로 불러왔다면") {
                        every { getFavoriteItemListUseCase.invoke() } returns flowOf(
                            Result.success(mockk(relaxed = true))
                        )
                    }
                    handler.mutate(action, state).test {
                        Then("로딩을 띄워준다.") { awaitItem().shouldBe(SearchMutate.Reduce.UpdateLoading(true)) }
                        Then("뷰 상태를 SHOW_DATA 로 변경한다") {
                            awaitItem().shouldBe(SearchMutate.Reduce.UpdateViewState(SearchMutate.State.ViewState.SHOW_DATA))
                        }
                        Then("이미지를 세팅한다") {
                            awaitItem().shouldBeInstanceOf<SearchMutate.Reduce.UpdateImageList>()
                        }
                        Then("다음 페이지 번호를 +1 시켜서 업데이트 한다") {
                            awaitItem().shouldBe(SearchMutate.Reduce.UpdatePage(page + 1))
                        }
                        Then("다음 페이지를 불러올 수 있는지 업데이트 한다") {
                            awaitItem().shouldBeInstanceOf<SearchMutate.Reduce.UpdateHasMore>()
                        }
                        Then("로딩을 닫는다.") { awaitItem().shouldBe(SearchMutate.Reduce.UpdateLoading(false)) }
                        awaitComplete()
                    }
                }
            }
            When("스크롤 하여 로딩 해야할 때") {
                val state = SearchMutate.State().copy(
                    imageList = listOf(mockk<SearchMutate.State.ImageItem>(relaxed = true)),
                    hasMore = true,
                    searchText = "검색어",
                    page = 2
                )
                val action = SearchAction.OnLoadMore
                val result = mockk<ResultEntity<PhotoEntity.Response>>()
                every { result.results } returns listOf(mockk<PhotoEntity.Response>(relaxed = true))
                every { result.totalPages } returns 1

                every { getSearchPhotoUseCase.invoke(state.searchText, state.page) } returns flowOf(
                    Result.success(result)
                )
                When("로컬 데이터에 북마크 정보를 성공적으로 불러왔다면") {
                    every { getFavoriteItemListUseCase.invoke() } returns flowOf(
                        Result.success(mockk(relaxed = true))
                    )
                }
                handler.mutate(action, state).test {
                    Then("로딩을 띄워준다.") { awaitItem().shouldBe(SearchMutate.Reduce.UpdateLoading(true)) }
                    Then("뷰 상태를 SHOW_DATA 로 변경한다") {
                        awaitItem().shouldBe(SearchMutate.Reduce.UpdateViewState(SearchMutate.State.ViewState.SHOW_DATA))
                    }
                    Then("이미지를 세팅한다") {
                        awaitItem().shouldBeInstanceOf<SearchMutate.Reduce.UpdateImageList>()
                    }
                    Then("다음 페이지 번호를 +1 시켜서 업데이트 한다") {
                        awaitItem().shouldBe(SearchMutate.Reduce.UpdatePage(state.page + 1))
                    }
                    Then("다음 페이지를 불러올 수 있는지 업데이트 한다") {
                        awaitItem().shouldBeInstanceOf<SearchMutate.Reduce.UpdateHasMore>()
                    }
                    Then("로딩을 닫는다.") { awaitItem().shouldBe(SearchMutate.Reduce.UpdateLoading(false)) }
                    awaitComplete()
                }
            }
        }
    }
}
