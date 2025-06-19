package kr.co.donghyun.player.presentation.screen

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import kr.co.donghyun.player.R
import kr.co.donghyun.player.presentation.components.ArtistDetailComponents
import kr.co.donghyun.player.presentation.components.ArtistsAlbumComponents
import kr.co.donghyun.player.presentation.ui.activites.AlbumActivity
import kr.co.donghyun.player.presentation.ui.activites.SearchArtistActivity
import kr.co.donghyun.player.presentation.viewmodel.SearchArtistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchedArtistScreen(viewModel : SearchArtistViewModel, innerPadding : PaddingValues) {
    with(viewModel) {
        val context = LocalContext.current
        val artist = remember { channelInfo }
        val singleSection = remember { singles }
        val albumsSection = remember { albums }

        val modalBottomSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { true }
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

        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(292.dp)
                        .padding(bottom = 16.dp)
                    ) {
                        AsyncImage(modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop, model = artist.value?.thumbnails?.last()?.url ?: "", contentDescription = "banner image")
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.Start) {
                            Text(text = artist.value?.name ?: "", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight(800))
                            Text(text = artist.value?.subscribers ?: "", color = Color.White, fontSize = 18.sp)
                        }
                        Row(
                            modifier = Modifier
                                .padding(top = innerPadding.calculateTopPadding())
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(modifier = Modifier.clickable {
                                showBottomSheet = true
                            }) {
                                Icon(painter = painterResource(id = R.drawable.ic_baseline_people_alt_24), contentDescription = "icon")
                            }
                        }
                    }
                }

                item {
                    Text(modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp), text = "Singles", fontSize = 24.sp, fontWeight = FontWeight(800)
                    )
                    LazyRow(modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)) {
                        item {
                            Box(modifier = Modifier.padding(start = 8.dp))
                        }
                        items(singleSection) { singleItem ->
                            ArtistsAlbumComponents(albumItem = singleItem) {
                                context.startActivity(Intent(context, AlbumActivity::class.java).apply {
                                    putExtra("albumId", singleItem?.albumId)
                                    putExtra("channelId", channelInfo.value?.artistId)
                                })
                            }
                        }
                        item {
                            Box(modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }

                item {
                    Text(modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp), text = "Albums", fontSize = 24.sp, fontWeight = FontWeight(800)
                    )
                    LazyRow(modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)) {
                        item {
                            Box(modifier = Modifier.padding(start = 8.dp))
                        }
                        items(albumsSection) { albumItem ->
                            ArtistsAlbumComponents(albumItem) {
                                context.startActivity(Intent(context, AlbumActivity::class.java).apply {
                                    putExtra("albumId", albumItem?.albumId)
                                    putExtra("channelId", channelInfo.value?.artistId)
                                })
                            }
                        }
                        item {
                            Box(modifier = Modifier.padding(start = 8.dp))
                        }
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
                    Text(text = "검색", fontWeight = FontWeight(700), fontSize = 18.sp)
                    Text(text = "유튜브 채널명을 입력하세요.", modifier = Modifier.padding(bottom = 16.dp))
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
                                searchChannel(it)
                            },
                        )
                    }
                    if(searchedArtists.isNotEmpty()) {
                        LazyColumn {
                            items(searchedArtists) { artist ->
                                ArtistDetailComponents(artist = artist) {
                                    showBottomSheet = false
                                    insertSearchedArtist(artist)
                                    getChannelInfo(artist?.artistId.orEmpty())
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