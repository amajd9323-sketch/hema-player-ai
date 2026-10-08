# AI Architecture

Hema Player AI should keep AI separate from media playback.

Suggested pipeline:

User command
 -> Intent parser
 -> MediaLibrary query
 -> Recommendation engine
 -> Player command

Examples:
"شغل الأغاني الهادية"
  -> MOOD = CHILL
  -> query local songs
  -> rank by history
  -> enqueue

"اعمل playlist للجيم 30 دقيقة"
  -> MOOD = WORKOUT
  -> DURATION = 30m
  -> select tracks
  -> create temporary queue

Recommended interfaces:

interface AiEngine {
    suspend fun understand(text: String): AiIntent
}

interface RecommendationEngine {
    suspend fun recommend(intent: AiIntent): List<MediaItem>
}

For privacy, local commands should work without sending library metadata to a server.
