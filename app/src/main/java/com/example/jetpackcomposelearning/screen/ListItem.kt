package com.example.jetpackcomposelearning.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.jetpackcomposelearning.data.WeatherModel
import com.example.jetpackcomposelearning.ui.theme.BlueLight

@Composable
fun ListItem(item: WeatherModel, currentDay: MutableState<WeatherModel>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 4.dp, end = 4.dp)
            .clickable {
                if (item.hours.isEmpty()) return@clickable
                currentDay.value = item
            },
        elevation = 0.dp,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = BlueLight
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BlueLight)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.time, fontSize = 16.sp, color = Color.White
                )
                Text(
                    text = item.condition, fontSize = 16.sp, color = Color.White,
                )
            }
            Text(
                text = if (item.currentTemp.isEmpty()) {
                    "${item.maxTemp.toFloat().toInt()}\u2103/" +
                            "${item.minTemp.toFloat().toInt()}\u2103"
                } else {
                    "${currentDay.value.currentTemp.toFloat().toInt()}\u2103"
                },
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            AsyncImage(
                model = "https:${item.icon}",
                contentDescription = "img",
                modifier = Modifier.size(40.dp)
            )
        }
    }
}