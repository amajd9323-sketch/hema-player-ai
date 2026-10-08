package com.hema.playerai

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem

class MainActivity : ComponentActivity() {
    private val permissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissions.launch(arrayOf(
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.POST_NOTIFICATIONS
        ))
        setContent { HemaPlayerApp() }
    }
}

data class DemoMedia(val title: String, val artist: String, val type: String)

@Composable
fun HemaPlayerApp() {
    var tab by remember { mutableIntStateOf(0) }
    var query by remember { mutableStateOf("") }
    var playing by remember { mutableStateOf(false) }
    var aiPrompt by remember { mutableStateOf("") }
    val media = listOf(
        DemoMedia("Midnight Drive", "Hema Library", "MUSIC"),
        DemoMedia("Neon Dreams", "Hema Library", "MUSIC"),
        DemoMedia("Summer Memories", "Local Video", "VIDEO"),
        DemoMedia("Chill Session", "Hema Library", "MUSIC")
    )

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color(0xFF08090D),
            surface = Color(0xFF11131A),
            primary = Color(0xFF7C5CFF),
            secondary = Color(0xFF19D3AE)
        )
    ) {
        Box(Modifier.fillMaxSize().background(Color(0xFF08090D))) {
            Column(Modifier.fillMaxSize()) {
                Header(query) { query = it }

                when (tab) {
                    0 -> HomeScreen(media, playing) { playing = !playing }
                    1 -> LibraryScreen(media, query) { playing = true }
                    2 -> AiScreen(aiPrompt) { aiPrompt = it }
                    else -> SettingsScreen()
                }

                if (playing) MiniPlayer(
                    title = "Midnight Drive",
                    playing = playing,
                    onPlay = { playing = !playing }
                )

                NavigationBar(containerColor = Color(0xFF0D0F15)) {
                    NavItem(Icons.Default.Home, "Home", tab == 0) { tab = 0 }
                    NavItem(Icons.Default.LibraryMusic, "Library", tab == 1) { tab = 1 }
                    NavItem(Icons.Default.AutoAwesome, "AI", tab == 2) { tab = 2 }
                    NavItem(Icons.Default.Settings, "Settings", tab == 3) { tab = 3 }
                }
            }
        }
    }
}

@Composable
fun Header(query: String, onQuery: (String) -> Unit) {
    Column(Modifier.padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("HEMA PLAYER", color = Color.White, fontWeight = FontWeight.Black)
                Text("AI MEDIA CENTER", color = Color(0xFF7C5CFF), style = MaterialTheme.typography.labelSmall)
            }
            Icon(Icons.Default.AccountCircle, null, tint = Color.LightGray)
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = query,
            onValueChange = onQuery,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search music, videos, artists…") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            singleLine = true,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
fun HomeScreen(media: List<DemoMedia>, playing: Boolean, onPlay: () -> Unit) {
    LazyColumn(
        modifier = Modifier.weight(1f),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Good afternoon", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Your library, smarter.", color = Color.Gray)
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    Modifier.fillMaxWidth().background(
                        Brush.linearGradient(listOf(Color(0xFF2A1B64), Color(0xFF102D35)))
                    ).padding(22.dp)
                ) {
                    Column {
                        Text("AI MIX", color = Color(0xFFB9A8FF), fontWeight = FontWeight.Bold)
                        Text("Late Night Focus", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                        Text("A smart mix from your library", color = Color.LightGray)
                        Spacer(Modifier.height(14.dp))
                        Button(onClick = onPlay) {
                            Icon(Icons.Default.PlayArrow, null)
                            Spacer(Modifier.width(6.dp))
                            Text("Play mix")
                        }
                    }
                }
            }
        }
        item { Text("Recently played", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        items(media.take(3)) { item ->
            MediaRow(item, onPlay)
        }
    }
}

@Composable
fun LibraryScreen(media: List<DemoMedia>, query: String, onPlay: () -> Unit) {
    val filtered = media.filter { "${it.title} ${it.artist}".contains(query, true) }
    Column(Modifier.weight(1f).padding(horizontal = 20.dp)) {
        Text("Your Library", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered) { MediaRow(it, onPlay) }
        }
    }
}

@Composable
fun MediaRow(item: DemoMedia, onPlay: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable { onPlay() }.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(54.dp).background(
                if (item.type == "VIDEO") Color(0xFF24354B) else Color(0xFF302454),
                RoundedCornerShape(14.dp)
            ),
            contentAlignment = Alignment.Center
        ) {
            Icon(if (item.type == "VIDEO") Icons.Default.PlayCircle else Icons.Default.MusicNote, null)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(item.title, fontWeight = FontWeight.Bold)
            Text(item.artist, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }
        IconButton(onClick = onPlay) { Icon(Icons.Default.MoreVert, null) }
    }
}

@Composable
fun AiScreen(prompt: String, onPrompt: (String) -> Unit) {
    Column(Modifier.weight(1f).padding(20.dp)) {
        Text("AI Player", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
        Text("Talk to your media library naturally.", color = Color.Gray)
        Spacer(Modifier.height(18.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF14111F)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("✨ Smart commands", color = Color(0xFFB9A8FF), fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("“Play my chill songs”")
                Text("“Make me a 30 minute workout mix”")
                Text("“Find videos I watched recently”")
                Text("“Play something like this”")
            }
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = prompt,
            onValueChange = onPrompt,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ask Hema AI…") },
            trailingIcon = { Icon(Icons.Default.Mic, null) },
            shape = RoundedCornerShape(18.dp)
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.AutoAwesome, null)
            Spacer(Modifier.width(8.dp))
            Text("Ask AI")
        }
        Spacer(Modifier.height(18.dp))
        Text("AI engine status", fontWeight = FontWeight.Bold)
        Text("Local command parser ready • Cloud AI connector ready", color = Color.Gray)
    }
}

@Composable
fun SettingsScreen() {
    Column(Modifier.weight(1f).padding(20.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(12.dp))
        listOf("Equalizer", "Playback", "Appearance", "Library folders", "AI provider", "Privacy").forEach {
            ListItem(
                headlineContent = { Text(it) },
                leadingContent = { Icon(Icons.Default.ChevronRight, null) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }
    }
}

@Composable
fun MiniPlayer(title: String, playing: Boolean, onPlay: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)
            .background(Color(0xFF171922), RoundedCornerShape(18.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(42.dp).background(Color(0xFF302454), RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.MusicNote, null)
        }
        Spacer(Modifier.width(10.dp))
        Text(title, Modifier.weight(1f), fontWeight = FontWeight.Bold)
        IconButton(onClick = onPlay) {
            Icon(if (playing) Icons.Default.Pause else Icons.Default.PlayArrow, null)
        }
    }
}

@Composable
fun RowScope.NavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Icon(icon, null) },
        label = { Text(label) }
    )
}
