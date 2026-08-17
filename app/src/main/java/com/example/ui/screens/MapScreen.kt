package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MatchingEngine
import com.example.model.GeoLocation
import com.example.model.JobPost
import com.example.model.WorkerProfile
import com.example.ui.components.MatchScoreBadge
import com.example.ui.components.RatingStars
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.PrimaryTeal
import com.example.viewmodel.MarketplaceViewModel

@Composable
fun MapScreen(
    viewModel: MarketplaceViewModel,
    onSelectJob: (JobPost) -> Unit
) {
    val context = LocalContext.current
    val allJobs by viewModel.allJobs.collectAsState()
    val allWorkers by viewModel.allWorkers.collectAsState()
    val radiusKm by viewModel.mapSearchRadiusKm.collectAsState()
    val showJobs by viewModel.mapShowJobs.collectAsState()
    val selectedCity by viewModel.selectedMapCity.collectAsState()
    val cities = viewModel.getCities()

    var selectedJobPin by remember { mutableStateOf<JobPost?>(null) }
    var selectedWorkerPin by remember { mutableStateOf<WorkerProfile?>(null) }

    // Filter items within radius of the selected city
    val nearbyJobs = remember(allJobs, selectedCity, radiusKm) {
        allJobs.filter { job ->
            val dist = MatchingEngine.calculateDistanceKm(
                selectedCity.latitude, selectedCity.longitude,
                job.location.latitude, job.location.longitude
            )
            dist <= radiusKm * 1.5 || job.location.cityName.equals(selectedCity.cityName, ignoreCase = true)
        }
    }

    val nearbyWorkers = remember(allWorkers, selectedCity, radiusKm) {
        allWorkers.filter { worker ->
            val dist = MatchingEngine.calculateDistanceKm(
                selectedCity.latitude, selectedCity.longitude,
                worker.preferredLocation.latitude, worker.preferredLocation.longitude
            )
            dist <= radiusKm * 1.5 || worker.preferredLocation.cityName.equals(selectedCity.cityName, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Controls Row
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Location Radar",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Toggle Jobs vs Workers
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(2.dp)
                    ) {
                        Surface(
                            color = if (showJobs) PrimaryTeal else Color.Transparent,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.clickable { viewModel.setMapShowJobs(true) }
                        ) {
                            Text(
                                text = "Jobs (${nearbyJobs.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (showJobs) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }

                        Surface(
                            color = if (!showJobs) PrimaryTeal else Color.Transparent,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.clickable { viewModel.setMapShowJobs(false) }
                        ) {
                            Text(
                                text = "Workers (${nearbyWorkers.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!showJobs) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // City Selector
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(cities) { city ->
                        FilterChip(
                            selected = selectedCity.cityName == city.cityName,
                            onClick = { viewModel.setSelectedMapCity(city) },
                            label = { Text("📍 ${city.cityName}", fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Radius Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Search Radius: ${radiusKm.toInt()} km", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(5.0, 15.0, 30.0, 50.0).forEach { r ->
                            Surface(
                                color = if (radiusKm == r) PrimaryTeal else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.clickable { viewModel.setMapSearchRadius(r) }
                            ) {
                                Text(
                                    text = "${r.toInt()}km",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (radiusKm == r) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Visual Interactive Map Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE2E8F0))
                .testTag("interactive_map_canvas")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val maxRadius = size.minDimension / 2.2f

                // Radar Circles
                drawCircle(color = Color.White.copy(alpha = 0.5f), radius = maxRadius, center = center)
                drawCircle(color = PrimaryTeal.copy(alpha = 0.1f), radius = maxRadius * 0.75f, center = center)
                drawCircle(color = PrimaryTeal.copy(alpha = 0.15f), radius = maxRadius * 0.45f, center = center)
                drawCircle(color = PrimaryTeal.copy(alpha = 0.25f), radius = maxRadius * 0.2f, center = center)

                drawCircle(
                    color = PrimaryTeal.copy(alpha = 0.4f),
                    radius = maxRadius,
                    center = center,
                    style = Stroke(width = 2f)
                )

                // Central City Node
                drawCircle(color = PrimaryTeal, radius = 10.dp.toPx(), center = center)
                drawCircle(color = Color.White, radius = 4.dp.toPx(), center = center)

                // Grid lines
                drawLine(
                    color = Color(0xFFCBD5E1),
                    start = Offset(0f, center.y),
                    end = Offset(size.width, center.y),
                    strokeWidth = 1f
                )
                drawLine(
                    color = Color(0xFFCBD5E1),
                    start = Offset(center.x, 0f),
                    end = Offset(center.x, size.height),
                    strokeWidth = 1f
                )
            }

            // Central City Marker Label
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 20.dp)
                    .background(Color(0xCC000000), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${selectedCity.cityName} Center",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Scatter Pins on Canvas
            if (showJobs) {
                nearbyJobs.forEachIndexed { index, job ->
                    val angle = (index * (360.0 / nearbyJobs.size.coerceAtLeast(1))) * (Math.PI / 180.0)
                    val distanceFactor = 0.35 + ((index % 3) * 0.22)
                    val offsetX = (Math.cos(angle) * distanceFactor * 130).dp
                    val offsetY = (Math.sin(angle) * distanceFactor * 130).dp

                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = offsetX, y = offsetY)
                            .clickable {
                                selectedJobPin = job
                                selectedWorkerPin = null
                            }
                            .testTag("map_job_pin_${job.jobId}")
                    ) {
                        Surface(
                            color = AccentAmber,
                            shape = CircleShape,
                            shadowElevation = 4.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.Work,
                                contentDescription = job.title,
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(16.dp)
                            )
                        }
                    }
                }
            } else {
                nearbyWorkers.forEachIndexed { index, worker ->
                    val angle = (index * (360.0 / nearbyWorkers.size.coerceAtLeast(1))) * (Math.PI / 180.0)
                    val distanceFactor = 0.35 + ((index % 3) * 0.22)
                    val offsetX = (Math.cos(angle) * distanceFactor * 130).dp
                    val offsetY = (Math.sin(angle) * distanceFactor * 130).dp

                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = offsetX, y = offsetY)
                            .clickable {
                                selectedWorkerPin = worker
                                selectedJobPin = null
                            }
                            .testTag("map_worker_pin_${worker.uid}")
                    ) {
                        Surface(
                            color = PrimaryTeal,
                            shape = CircleShape,
                            shadowElevation = 4.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = worker.fullName,
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(16.dp)
                            )
                        }
                    }
                }
            }

            // Google Maps Open Button
            FloatingActionButton(
                onClick = {
                    val gmmIntentUri = Uri.parse("geo:${selectedCity.latitude},${selectedCity.longitude}?q=${selectedCity.cityName}+Ethiopia")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    try {
                        context.startActivity(mapIntent)
                    } catch (e: Exception) {
                        viewModel.showMessage("Opening Map coordinates: ${selectedCity.cityName}")
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .size(44.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Navigation, contentDescription = "Open External Maps", tint = Color.White)
            }
        }

        // Preview Card for Selected Pin
        if (selectedJobPin != null) {
            val job = selectedJobPin!!
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = job.title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${job.employerName} • ${job.salaryMin.toInt()} ETB", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    Button(
                        onClick = { onSelectJob(job) },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("View Job", fontSize = 12.sp)
                    }
                }
            }
        } else if (selectedWorkerPin != null) {
            val worker = selectedWorkerPin!!
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = worker.fullName, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = worker.headline, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    RatingStars(rating = worker.rating)
                }
            }
        }

        Spacer(modifier = Modifier.height(70.dp))
    }
}
