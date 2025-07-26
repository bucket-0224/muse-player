package kr.co.donghyun.player.presentation.screen

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.co.donghyun.player.data.album.model.Music
import kr.co.donghyun.player.data.album.model.VideoItem
import kr.co.donghyun.player.presentation.components.ArtistDetailComponents
import kr.co.donghyun.player.presentation.components.RecentSearchedArtistComponents
import kr.co.donghyun.player.presentation.components.VideoDetailComponents
import kr.co.donghyun.player.presentation.ui.activites.PlayerActivity
import kr.co.donghyun.player.presentation.ui.activites.SearchArtistActivity
import kr.co.donghyun.player.presentation.util.Util
import kr.co.donghyun.player.presentation.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: MainViewModel, onPlayMusic : (videoId : String, playingType : String, onResult : () -> Unit) -> Unit,  innerPadding : PaddingValues) {
    with(viewModel) {
        val context = LocalContext.current
        val searchedArtists = remember { searchedArtists }
        val searchedVideos = remember { searchedVideos }
        val isClickable = remember { mutableStateOf(true) }

        var searchType = remember { mutableStateOf(Util.SEARCH.ARTIST) }

        val modalBottomSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { true },
        )
        var showBottomSheet by remember { mutableStateOf(false) }
        var inputTextState by remember { mutableStateOf("") }

        LaunchedEffect(showBottomSheet) {
            if (showBottomSheet && !modalBottomSheetState.isVisible) {
                modalBottomSheetState.show()
            } else if (!showBottomSheet && modalBottomSheetState.isVisible) {
                modalBottomSheetState.hide()
            }
        }

        if(recentlySearchedArtists.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = "검색", modifier = Modifier.padding(start = 16.dp, top = 16.dp), textAlign = TextAlign.Start, fontWeight = FontWeight(800), fontSize = 32.sp)
                        Text(text = "아티스트/영상을 검색해보세요!", modifier = Modifier.padding(start = 16.dp, bottom = 24.dp), textAlign = TextAlign.Start, fontWeight = FontWeight(400), fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        modifier = Modifier.size(48.dp).padding(end = 8.dp),
                        onClick = {
                            showBottomSheet = true
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(26.dp),
                            imageVector = Icons.Filled.Search,
                            tint = Color.White,
                            contentDescription = "Search"
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "최근 검색된 아티스트가 존재하지 않습니다.", fontWeight = FontWeight(800), fontSize = 18.sp)
                    Text(text = "검색으로 아티스트 찾고, 음원을 들어보세요!", fontSize = 14.sp, modifier = Modifier.padding(bottom = 24.dp))
                    Button(onClick = {
                        showBottomSheet = true
                    }) {
                        Text(text = "아티스트 검색하기", fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.padding(bottom = 36.dp))
            }
        } else {
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),) {

                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(text = "검색", modifier = Modifier.padding(start = 16.dp, top = 16.dp), textAlign = TextAlign.Start, fontWeight = FontWeight(800), fontSize = 32.sp)
                            Text(text = "아티스트/영상을 검색해보세요!", modifier = Modifier.padding(start = 16.dp, bottom = 24.dp), textAlign = TextAlign.Start, fontWeight = FontWeight(400), fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            modifier = Modifier.size(48.dp).padding(end = 8.dp),
                            onClick = {
                                showBottomSheet = true
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(26.dp),
                                imageVector = Icons.Filled.Search,
                                tint = Color.White,
                                contentDescription = "Search"
                            )
                        }
                    }
                }

                items(recentlySearchedArtists.size) { idx ->
                    RecentSearchedArtistComponents(onClick = {
                        context.startActivity(Intent(context, SearchArtistActivity::class.java).apply {
                            putExtra("channelId", it)
                        })
                    }, artist = recentlySearchedArtists[idx], albums = recentlySearchedArtistsAlbums[idx])
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth() ,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.padding(bottom = 48.dp))
                    }
                }
            }
        }


        if(showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                modifier = Modifier.padding(top = 64.dp),
                sheetState = modalBottomSheetState
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "검색", fontWeight = FontWeight(700), fontSize = 18.sp)
                            Text(text = "${if(searchType.value == Util.SEARCH.ARTIST) "유튜브 아티스트를" else "유튜브 영상을"} 검색하세요.", modifier = Modifier.padding(bottom = 8.dp))
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        OutlinedButton(onClick = {
                            inputTextState = ""
                            searchType.value = if(searchType.value == Util.SEARCH.ARTIST)
                                Util.SEARCH.VIDEO
                            else
                                Util.SEARCH.ARTIST
                        }) {
                            Text(text = if(searchType.value == Util.SEARCH.ARTIST) "영상 검색" else "아티스트 검색" , fontSize = 14.sp)
                        }
                    }
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        OutlinedTextField(
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                                .fillMaxWidth(),
                            value = inputTextState,
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Search,
                                    contentDescription = "Search Icon"
                                )
                            },
                            singleLine = true,
                            maxLines = 1,
                            onValueChange = {
                                inputTextState = it
                                if(searchType.value == Util.SEARCH.ARTIST)
                                    searchChannel(inputTextState)
                                else
                                    searchVideos(inputTextState)
                            },
                            placeholder = {
                                Text("검색어를 입력하세요.")
                            },
//                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
//                            keyboardActions = KeyboardActions(
//                                onSearch = {
//                                    if(searchType.value == Util.SEARCH.ARTIST)
//                                        searchChannel(inputTextState)
//                                    else
//                                        searchVideos(inputTextState)
//                                }
//                            )
                        )
                    }
                    if(searchType.value == Util.SEARCH.ARTIST) {
                        if(searchedArtists.isNotEmpty()) {
                            LazyColumn {
                                items(searchedArtists) { artist ->
                                    ArtistDetailComponents(artist = artist) {
                                        showBottomSheet = false
                                        insertSearchedArtist(artist)
                                        context.startActivity(Intent(context, SearchArtistActivity::class.java).apply {
                                            putExtra("channelId", artist?.artistId.orEmpty())
                                        })
                                    }
                                }
                                item {
                                    Spacer(modifier = Modifier.padding(bottom = 16.dp))
                                }
                            }
                        }
                    } else {
                        if(searchedVideos.isNotEmpty()) {
                            LazyColumn {
                                items(searchedVideos) { video ->
                                    VideoDetailComponents(video = video) {
                                        with(playbackManager) {
                                            if(isClickable.value) {
                                                onPlayMusic(video?.id ?: "", Util.SEARCH.VIDEO.name) {
                                                    isClickable.value = false
                                                    context.startActivity(Intent(context, PlayerActivity::class.java).apply {
                                                        putExtra("videoId", video?.id)
                                                        putExtra("isAnotherMusic", video?.id != if(playingStateOfResponse.value is VideoItem?) (playingStateOfResponse.value as VideoItem?)?.id else (playingStateOfResponse.value as Music?)?.youtubeId)
                                                        putExtra("playingType", Util.SEARCH.VIDEO.name)

                                                        playbackManager.setUpFetchedMusicVideoList(searchedVideos.toList(), searchedVideos.indexOf(video))

                                                        showBottomSheet = false
                                                        isClickable.value = true
                                                        isOnPlaylist.value = false
                                                        playingStateOfResponse.value = video
                                                    })
                                                }
                                            }
                                        }
                                    }
                                }
                                item {
                                    Spacer(modifier = Modifier.padding(bottom = 16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}