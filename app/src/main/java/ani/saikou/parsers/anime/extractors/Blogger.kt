package ani.saikou.parsers.anime.extractors

import ani.saikou.Mapper
import ani.saikou.client
import ani.saikou.findBetween
import ani.saikou.getSize
import ani.saikou.parsers.Video
import ani.saikou.parsers.VideoContainer
import ani.saikou.parsers.VideoExtractor
import ani.saikou.parsers.VideoServer
import ani.saikou.parsers.VideoType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class Blogger(override val server: VideoServer) : VideoExtractor() {
    override suspend fun extract(): VideoContainer {
        val videos = Mapper.parse<Response>(client.get(server.embed.url).text.findBetween("VIDEO_CONFIG = ", "\n")!!)
            .streams.map {
                Video(null, VideoType.CONTAINER, it.playURL, getSize(it.playURL))
            }
        return VideoContainer(videos)
    }

    @Serializable
    data class Response(
        val streams: List<Stream>
    ) {
        @Serializable
        data class Stream(
            @SerialName("play_url")
            val playURL: String
        )
    }
}