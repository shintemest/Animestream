package ani.saikou.parsers.anime.extractors

import ani.saikou.client
import ani.saikou.findBetween
import ani.saikou.parsers.Video
import ani.saikou.parsers.VideoContainer
import ani.saikou.parsers.VideoExtractor
import ani.saikou.parsers.VideoServer
import ani.saikou.parsers.VideoType

class Voe(override val server: VideoServer) : VideoExtractor() {
    override suspend fun extract(): VideoContainer {
        val res = client.get(server.embed.url).text.findBetween("var sources = ", ";")!!
        val hls = res.findBetween("'hls': '", "'")!!
        val height = res.findBetween("'video_height': ", ",")!!.toInt()
        return VideoContainer(listOf(Video(height, VideoType.M3U8, hls)))
    }
}