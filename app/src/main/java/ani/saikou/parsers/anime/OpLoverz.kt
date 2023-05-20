package ani.saikou.parsers.anime

import android.net.Uri
import ani.saikou.asyncMapNotNull
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
import ani.saikou.parsers.anime.extractors.Blogger
import ani.saikou.parsers.anime.extractors.Kraken
import ani.saikou.parsers.anime.extractors.Voe

class OpLoverz : AnimeParser() {

    override val name = "OpLoverz"
    override val saveName = "oploverz"
    override val hostUrl = "https://oploverz.top"
    override val isDubAvailableSeparately = false

    override suspend fun loadEpisodes(animeLink: String, extra: Map<String, String>?): List<Episode> {
        return client.get(animeLink).document.select(".eplister li a").map {
            Episode(
                it.select(".epl-num").text().trimStart('0'),
                it.attr("href")
            )
        }.reversed()
    }

    override suspend fun loadVideoServers(episodeLink: String, extra: Map<String, String>?): List<VideoServer> {
        val res = client.get(episodeLink).document
        val list = res.select(".player-embed iframe").attr("src").let {
            mutableListOf(VideoServer("Default", it))
        }
        res.select(".soradlg").forEach { e ->
            val type = e.select("h3").text()
            val qualities = e.select(".soraurlx strong").map { it.text().substringBefore("p") }
            val links = e.select(".soraurlx a")
            val directs = links.filter { it -> it.text() == "Direct" }
                .map { it.attr("href") }
            val krakens = links.filter{ it -> it.text() == "KrakenFiles" }
                .map { it.attr("href") }
            list.add(
                VideoServer(
                    "Direct $type", "", mapOf(
                        "s" to directs.joinToString("|"),
                        "q" to qualities.joinToString("|")
                    )
                )
            )
            list.add(
                VideoServer(
                    "Kraken $type", "", mapOf(
                        "s" to krakens.joinToString("|"),
                        "q" to qualities.joinToString("|")
                    )
                )
            )
        }
        return list
    }

    override suspend fun getVideoExtractor(server: VideoServer): VideoExtractor? {
        if (server.embed.url == "") return OpExtractor(server)
        val domain = Uri.parse(server.embed.url).host ?: ""
        val extractor: VideoExtractor? = when {
            "blog" in domain   -> Blogger(server)
            "kraken" in domain -> Kraken(server)
            "voe" in domain    -> Voe(server)
            else               -> null
        }
        return extractor
    }

    class OpExtractor(override val server: VideoServer) : VideoExtractor() {
        override suspend fun extract(): VideoContainer {
            val videos = server.extraData!!["s"]!!.split("|")
            val qualities = server.extraData["q"]!!.split("|")
            if(videos.size != qualities.size) return VideoContainer(emptyList())
            return if(server.name.contains("Kraken")){
                VideoContainer(videos.asyncMapNotNull {
                    Kraken(VideoServer("Kraken", it)).extract().videos.firstOrNull()
                })
            }else{
                VideoContainer(videos.mapIndexed { i, v ->
                    Video(qualities[i].toIntOrNull(), VideoType.CONTAINER, v, getSize(v))
                })
            }
        }
    }

    override suspend fun search(query: String): List<ShowResponse> {
        return client.get("$hostUrl/?s=$query").document
            .select("div.listupd article a")
            .map {
                ShowResponse(
                    it.attr("title"),
                    it.attr("href"),
                    it.select("img").attr("src")
                )
            }
    }
}