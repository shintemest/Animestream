package ani.saikou.parsers.manga

import ani.saikou.client
import ani.saikou.parsers.MangaChapter
import ani.saikou.parsers.MangaImage
import ani.saikou.parsers.MangaParser
import ani.saikou.parsers.ShowResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

open class Kiryuu : MangaParser() {

    override val name = "Kiryuu"
    override val saveName = "kiryuu"
    override val hostUrl = "https://kiryuu.id"

    override suspend fun loadChapters(mangaLink: String, extra: Map<String, String>?): List<MangaChapter> {
        return client.get(mangaLink).document.select("#chapterlist li .eph-num > a").map {
            MangaChapter(
                it.select(".chapternum").text().lowercase().substringAfter("chapter")
                    .substringBefore("hq").substringBefore("hd").substringBefore("lq")
                    .trim().trimEnd('-').trimEnd('|').trim().trimStart('0'),
                it.attr("href")
            )
        }.reversed()
    }

    override suspend fun loadImages(chapterLink: String): List<MangaImage> {
        return client.get(chapterLink).document.select("#readerarea img").map {
            MangaImage(it.attr("src"))
        }
    }

    override suspend fun search(query: String): List<ShowResponse> {
        return client.post(
            "$hostUrl/wp-admin/admin-ajax.php", data = mapOf(
                "action" to "ts_ac_do_search",
                "ts_ac_query" to query
            )
        ).parsed<SearchResponse>().series.first().all.map {
            ShowResponse(it.postTitle, it.postLink, it.postImage)
        }
    }

    @Serializable
    data class SearchResponse(
        val series: List<Series>
    )

    @Serializable
    data class Series(
        val all: List<All>,
    )

    @Serializable
    data class All(
        @SerialName("post_image")
        val postImage: String,
        @SerialName("post_title")
        val postTitle: String,
        @SerialName("post_link")
        val postLink: String,
    )
}