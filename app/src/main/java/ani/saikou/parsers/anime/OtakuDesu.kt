package ani.saikou.parsers.anime

import android.net.Uri
import android.util.Base64
import ani.saikou.Mapper
import ani.saikou.asyncMap
import ani.saikou.client
import ani.saikou.findBetween
import ani.saikou.parsers.AnimeParser
import ani.saikou.parsers.Episode
import ani.saikou.parsers.ShowResponse
import ani.saikou.parsers.VideoExtractor
import ani.saikou.parsers.VideoServer
import ani.saikou.parsers.anime.extractors.DesuDesu
import ani.saikou.parsers.anime.extractors.Kraken
import ani.saikou.printIt
import ani.saikou.tryWithSuspend
import kotlinx.serialization.Serializable
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class OtakuDesu : AnimeParser() {

    override val name = "OtakuDesu"
    override val saveName = "otakudesu"
    override val hostUrl = "https://otakudesu.lol"
    override val isDubAvailableSeparately = false

    override suspend fun loadEpisodes(animeLink: String, extra: Map<String, String>?): List<Episode> {
        return client.get(animeLink).document
            .select("div.episodelist li a")
            .map {
                Episode(
                    it.text().findBetween("Episode ", " Subtitle")!!,
                    it.attr("href")
                )
            }.reversed()
    }

    @Serializable
    data class EmbedResponse(
        val id: Long,
        val i: Long,
        val q: String
    )

    override suspend fun loadVideoServers(episodeLink: String, extra: Map<String, String>?): List<VideoServer> {
        val res = client.get(episodeLink).document
        val (nonce, action) = getActionPair(res).printIt("Action : ")
        return res.select(".mirrorstream ul li a").asyncMap {
            val embed = Mapper.parse<EmbedResponse>(
                String(Base64.decode(it.attr("data-content"), Base64.DEFAULT))
            ).printIt("Emb : ")
            VideoServer(
                "${it.text()} ${embed.q}",
                getEmbed(
                    mapOf(
                        "action" to action,
                        "i" to embed.i.toString(),
                        "q" to embed.q,
                        "id" to embed.id.toString(),
                        "nonce" to nonce
                    )
                )
            )
        }
    }


    private var pair: Pair<String, String>? = null
    private suspend fun getActionPair(document: Element): Pair<String, String> {
        return pair ?: let {
            return tryWithSuspend {
                document.select("script").forEach {
                    if (it.data().contains("data:{action:\"")) {
                        val nonce = client.post(
                            "$hostUrl/wp-admin/admin-ajax.php",
                            data = mapOf("action" to (it.data().findBetween("data:{action:\"", "\"}")!!))
                        ).parsed<PostResponse>().data
                        val action = it.data().findBetween("nonce:a,action:\"", "\"}")!!
                        pair = nonce to action
                        return@tryWithSuspend nonce to action
                    }
                }
                return@tryWithSuspend null
            } ?: ("264003f914" to "2a3505c93b0035d3f455df82bf976b84")
        }
    }

    @Serializable
    data class PostResponse(
        val data: String
    )

    private suspend fun getEmbed(data: Map<String, String>): String {
        return Jsoup.parse(
            String(
                Base64.decode(
                    client.post("$hostUrl/wp-admin/admin-ajax.php", data = data)
                        .parsed<PostResponse>().data,
                    Base64.DEFAULT
                )
            )
        ).select("iframe").attr("src")
    }

    override suspend fun getVideoExtractor(server: VideoServer): VideoExtractor? {
        val domain = Uri.parse(server.embed.url).host ?: ""
        val extractor: VideoExtractor? = when {
            "kraken" in domain -> Kraken(server)
            "desu" in domain   -> DesuDesu(server)
            else               -> null
        }
        return extractor
    }

    override suspend fun search(query: String): List<ShowResponse> {
        return client.get("$hostUrl/?post_type=anime&s=$query").document
            .select("ul.chivsrc li")
            .map {
                val img = it.select("img")
                ShowResponse(
                    img.attr("alt"),
                    it.select("a").attr("href"),
                    img.attr("src")
                )
            }
    }
}