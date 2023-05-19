package ani.saikou.parsers.anime

import android.net.Uri
import ani.saikou.asyncMap
import ani.saikou.client
import ani.saikou.parsers.AnimeParser
import ani.saikou.parsers.Episode
import ani.saikou.parsers.ShowResponse
import ani.saikou.parsers.VideoExtractor
import ani.saikou.parsers.VideoServer
import ani.saikou.parsers.anime.extractors.Blogger
import ani.saikou.parsers.anime.extractors.Kraken

class Samehadaku : AnimeParser() {

    override val name = "Samehadaku"
    override val saveName = "samehadaku"
    override val hostUrl = "https://samehadaku.cc"
    override val isDubAvailableSeparately = false

    override suspend fun loadEpisodes(animeLink: String, extra: Map<String, String>?): List<Episode> {
        return client.get(client.get(animeLink).document.selectFirst(".epsright a")!!.attr("href"))
            .document.select(".epsright a")
            .map {
                val img = it.select("img")
                Episode(
                    img.attr("alt").substringAfter("Episode "),
                    it.attr("href"),
                    null,
                    img.attr("src")
                )
            }.reversed()
    }

    override suspend fun loadVideoServers(episodeLink: String, extra: Map<String, String>?): List<VideoServer> {
        return client.get(episodeLink).document.select("#server li div").asyncMap {
            VideoServer(
                it.text(),
                getEmbed(
                    mapOf(
                        "action" to "player_ajax",
                        "post" to it.attr("data-post"),
                        "nume" to it.attr("data-nume"),
                        "type" to it.attr("data-type"),
                    )
                )
            )
        }
    }

    private suspend fun getEmbed(data: Map<String, String>): String {
        return client.post("$hostUrl/wp-admin/admin-ajax.php", data = data)
            .document.select("iframe")
            .attr("src")
    }

    override suspend fun getVideoExtractor(server: VideoServer): VideoExtractor? {
        val domain = Uri.parse(server.embed.url).host ?: ""
        val extractor: VideoExtractor? = when {
            "kraken" in domain  -> Kraken(server)
            "blogger" in domain -> Blogger(server)
            else                -> null
        }
        return extractor
    }

    override suspend fun search(query: String): List<ShowResponse> {
        return client
            .get("$hostUrl/?s=$query")
            .document.select("#main .animposx a")
            .map {
                ShowResponse(
                    it.attr("title"),
                    it.attr("href"),
                    it.select("img").attr("src")
                )
            }
    }
}
