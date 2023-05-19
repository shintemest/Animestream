package ani.saikou.parsers.anime.extractors

import ani.saikou.client
import ani.saikou.getSize
import ani.saikou.parsers.Video
import ani.saikou.parsers.VideoContainer
import ani.saikou.parsers.VideoExtractor
import ani.saikou.parsers.VideoServer
import ani.saikou.parsers.VideoType

class Kraken(override val server: VideoServer) : VideoExtractor() {
    override suspend fun extract(): VideoContainer {
        val url = "https:" + client.get(server.embed.url).document.select("video").attr("data-src-url")
        return VideoContainer(
            listOf(
                Video(
                    null,
                    VideoType.CONTAINER,
                    url,
                    getSize(url)
                )
            )
        )
    }
}