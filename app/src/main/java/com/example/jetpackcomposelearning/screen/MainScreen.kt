package com.example.jetpackcomposelearning.screen

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.jetpackcomposelearning.R
import com.example.jetpackcomposelearning.data.WeatherModel
import com.example.jetpackcomposelearning.getData
import com.example.jetpackcomposelearning.ui.theme.BlueDark
import com.example.jetpackcomposelearning.ui.theme.BlueLight
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainCard(
    context: Context,
    daysList: MutableState<List<WeatherModel>>,
    currentDay: MutableState<WeatherModel>
) {

    var visible by remember {
        mutableStateOf(false)
    }
    val message = remember {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = BlueLight,
            elevation = 0.dp,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentDay.value.time,
                        style = TextStyle(fontSize = 18.sp),
                        color = Color.White
                    )
                    AsyncImage(
                        model = "https:${currentDay.value.icon}",
                        contentDescription = "img",
                        modifier = Modifier.size(40.dp)
                    )
                }
                Text(
                    text = currentDay.value.city, style = TextStyle(fontSize = 28.sp),
                    color = Color.White
                )
                Text(
                    text = if (currentDay.value.currentTemp.isNotEmpty()) {
                        "${currentDay.value.currentTemp.toFloat().toInt()}\u2103"
                    } else {
                        "${currentDay.value.avgTemp.toFloat().toInt()}\u2103"
                    },
                    style = TextStyle(fontSize = 72.sp),
                    color = Color.White
                )
                Text(
                    text = currentDay.value.condition,
                    style = TextStyle(fontSize = 16.sp),
                    color = Color.White
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        visible = !visible
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_search_24),
                            contentDescription = "",
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                    }
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn() + slideInHorizontally(),
                        exit = fadeOut() + slideOutHorizontally()
                    ) {
                        OutlinedTextField(
                            value = message.value,
                            onValueChange = { message.value = it },
                            trailingIcon = {
                                Icon(
                                    painterResource(id = R.drawable.round_search_24),
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.widthIn(0.dp, 250.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                capitalization = KeyboardCapitalization.Sentences,
                                autoCorrect = true,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(onSearch = {
                                getData(message.value, context, daysList, currentDay)
                                keyboardController?.hide()
                                message.value = ""
                                visible = false
                            }),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = BlueLight,
                                unfocusedBorderColor = BlueLight,
                                backgroundColor = BlueLight,
                                textColor = Color.White
                            )
                        )
                    }
                    Text(
                        text = "${
                            currentDay.value.maxTemp.toFloat().toInt()
                        }\u2103/${currentDay.value.minTemp.toFloat().toInt()}\u2103",
                        style = TextStyle(fontSize = 16.sp),
                        color = Color.White,
                        modifier = Modifier.padding(8.dp),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabLayout(daysList: MutableState<List<WeatherModel>>, currentDay: MutableState<WeatherModel>) {
    val tabList = listOf("Hours", "Days")
    val pagerState = rememberPagerState()
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
    ) {
        TabRow(
            selectedTabIndex = tabIndex, indicator = {
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, it)
                )
            }, backgroundColor = BlueLight, contentColor = BlueDark
        ) {
            tabList.forEachIndexed { index, name ->
                Tab(selected = false, onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }, text = {
                    Text(text = name, color = Color.White)
                })
            }
        }
        HorizontalPager(
            count = tabList.size, state = pagerState, modifier = Modifier.weight(1f)
        ) { index ->
            val list = when (index) {
                0 -> getWeatherByHours(currentDay.value.hours)
                1 -> daysList.value
                else -> daysList.value
            }
            MainList(list, currentDay)
        }
    }
}

private fun getWeatherByHours(hours: String): List<WeatherModel> {
    if (hours.isEmpty()) return listOf()
    val hoursArray = JSONArray(hours)
    val list = ArrayList<WeatherModel>()
    for (i in 0 until hoursArray.length()) {
        val item = hoursArray[i] as JSONObject
        list.add(
            WeatherModel(
                "",
                item.getString("time"),
                item.getString("temp_c").toFloat().toInt().toString() + "\u2103",
                item.getJSONObject("condition").getString("text"),
                item.getJSONObject("condition").getString("icon"),
                "",
                "",
                "",
                ""
            )
        )
    }
    return list
}