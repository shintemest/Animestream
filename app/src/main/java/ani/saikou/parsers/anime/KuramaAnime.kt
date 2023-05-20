package ani.saikou.parsers.anime

import ani.saikou.client
import ani.saikou.getSize
import ani.saikou.parsers.AnimeParser
import ani.saikou.parsers.Episode
import ani.saikou.parsers.ShowResponse
import ani.saikou.parsers.Video
import ani.saikou.parsers.VideoContainer
import ani.saikou.parsers.VideoExtractor
import ani.saikou.parsers.VideoServer
import ani.saikou.parsers.VideoType
import ani.saikou.parsers.anime.extractors.FPlayer
import ani.saikou.parsers.anime.extractors.StreamSB
import ani.saikou.parsers.anime.extractors.StreamTape
import ani.saikou.printIt
import org.jsoup.Jsoup

class KuramaAnime : AnimeParser() {

    override val name = "KuramaAnime"
    override val saveName = "kurama"
    override val hostUrl = "https://kuramanime.net"
    override val isDubAvailableSeparately = false

    override suspend fun loadEpisodes(animeLink: String, extra: Map<String, String>?): List<Episode> {
        val eps = mutableListOf<Episode>()
        suspend fun parse(link: String) {
            val res = Jsoup.parse(
                client.get(link).document.select("#episodeLists").attr("data-content")
            )
            res.select("a.btn-danger").forEach {
                eps.add(
                    Episode(it.text().substringAfter("Ep "), it.attr("href"))
                )
            }
            val next = res.select("a.btn-dark")
                .find { e -> e.selectFirst("i.fa") != null }
                ?.attr("href")
            if (next != null) parse(next)
        }
        parse(animeLink)
        return eps
    }

    override suspend fun loadVideoServers(episodeLink: String, extra: Map<String, String>?): List<VideoServer> {
        return client.get(episodeLink).document.select("#changeServer option").map {
            VideoServer(
                it.attr("value"),
                "$episodeLink?activate_stream=1&stream_server=${it.attr("value")}"
            )
        }
    }

    override suspend fun getVideoExtractor(server: VideoServer): VideoExtractor? {
        val res = client.get(server.embed.url).document.selectFirst("div.video-content")!!
        res.printIt("res : ")
        res.selectFirst("iframe")?.let {
            val new = VideoServer(server.name, it.attr("src"))
            new.printIt("new : ")
            return when (server.name) {
                "streamsb" -> StreamSB(new)
                "fembed" -> FPlayer(new)
                "streamtape" -> StreamTape(new)
                else -> null
            }
        }
        res.selectFirst("#player")?.let { i ->
            val src = i.select("source")
            src.printIt("src : ")
            return KuramaExtractor(
                VideoServer(server.name, "",
                    mapOf(
                        "s" to src.joinToString("|") { it.attr("src") },
                        "q" to src.joinToString("|") { it.attr("size") }
                    )
                )
            )
        }
        return null
    }

    class KuramaExtractor(override val server: VideoServer) : VideoExtractor() {
        override suspend fun extract(): VideoContainer {
            val videos = server.extraData!!["s"]!!.split("|")
            val qualities = server.extraData["q"]!!.split("|")
            return VideoContainer(videos.mapIndexed { i, v ->
                Video(qualities[i].toInt(), VideoType.CONTAINER, v, getSize(v))
            })
        }
    }

    override suspend fun search(query: String): List<ShowResponse> {
        return client.get("$hostUrl/anime?search=$query").document.select("#animeList a").map {
            ShowResponse(
                it.select("h5").text(),
                it.attr("href"),
                it.select(".set-bg").attr("data-setbg")
            )
        }
    }
}