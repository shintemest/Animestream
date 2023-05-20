package ani.saikou.parsers.anime.extractors

import ani.saikou.client
import ani.saikou.findBetween
import ani.saikou.getSize
import ani.saikou.parsers.Video
import ani.saikou.parsers.VideoContainer
import ani.saikou.parsers.VideoExtractor
import ani.saikou.parsers.VideoServer
import ani.saikou.parsers.VideoType

class DesuDesu(override val server: VideoServer) : VideoExtractor() {
    override suspend fun extract(): VideoContainer {
        return client.get(server.embed.url).text.findBetween("{'file':'", "',")!!.let {
            VideoContainer(listOf(Video(null, VideoType.CONTAINER, it, getSize(it))))
        }
    }
}