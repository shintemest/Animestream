package ani.saikou.parsers.anime.extractors

import ani.saikou.asyncMap
import ani.saikou.client
import ani.saikou.getSize
import ani.saikou.parsers.*
import kotlinx.serialization.Serializable

class FPlayer(override val server: VideoServer) : VideoExtractor() {

    override suspend fun extract(): VideoContainer {
        val url = server.embed.url
        val apiLink = url.replace("/v/", "/api/source/")
        try {
            val json = client.post(apiLink, referer = url).parsed<Json>()
            if (json.success) {
                return VideoContainer(json.data?.asyncMap {
                    Video(
                        it.label.replace("p", "").toIntOrNull(),
                        VideoType.CONTAINER,
                        it.file,
                        getSize(it.file)
                    )
                }?: listOf())
            }

        } catch (e: Exception) {}
        return VideoContainer(listOf())
    }

    @Serializable
    private data class Data(
        val file: String,
        val label: String
    )

    @Serializable
    private data class Json(
        val success: Boolean,
        val data: List<Data>?
    )
}