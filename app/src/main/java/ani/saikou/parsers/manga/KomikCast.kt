package ani.saikou.parsers.manga

import ani.saikou.client
import ani.saikou.findBetween
import ani.saikou.parsers.MangaChapter
import ani.saikou.parsers.MangaImage
import ani.saikou.parsers.MangaParser
import ani.saikou.parsers.ShowResponse
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class KomikCast : MangaParser() {

    override val name = "KomikCast"
    override val saveName = "komik_cast"
    override val hostUrl = "https://komikcast.site"

    override suspend fun loadChapters(mangaLink: String, extra: Map<String, String>?): List<MangaChapter> {
        return client.get(mangaLink).document.select("#chapter-wrapper li a").mapNotNull {
            if (it.text().contains("LQ")) return@mapNotNull null
            MangaChapter(
                it.text().substringAfter("Chapter").substringBefore("HQ")
                    .substringBefore("HD").substringBefore("LQ").trim().trimStart('0'),
                it.attr("href")
            )
        }.reversed()
    }

    override suspend fun loadImages(chapterLink: String): List<MangaImage> {
        return client.get(chapterLink).document.select(".main-reading-area img").map {
            MangaImage(it.attr("src"))
        }
    }

    //No idea why List<SearchResponse> doesn't work here
    //kotlinx.serialization.json.internal.JsonDecodingException: Expected class kotlinx.serialization.json.JsonObject
    //as the serialized body of kotlinx.serialization.Polymorphic<List>, but had class kotlinx.serialization.json.JsonArray
    override suspend fun search(query: String): List<ShowResponse> {
        return client.post(
            "$hostUrl/wp-admin/admin-ajax.php", data = mapOf(
                "action" to "searchkomik_komikcast_redesign",
                "search" to query,
                "page" to "25",
                "orderby" to "relevance"
            )
        ).parsed<JsonArray>().map {
            ShowResponse(
                it.jsonObject["title"]!!.jsonPrimitive.content,
                it.jsonObject["permalink"]!!.jsonPrimitive.content,
                it.jsonObject["thumbnail"]!!.jsonPrimitive.content.findBetween("src=\"", "\"") ?: ""
            )
        }
    }

}