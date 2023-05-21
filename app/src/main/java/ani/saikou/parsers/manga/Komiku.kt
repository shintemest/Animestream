package ani.saikou.parsers.manga

import ani.saikou.client
import ani.saikou.parsers.MangaChapter
import ani.saikou.parsers.MangaImage
import ani.saikou.parsers.MangaParser
import ani.saikou.parsers.ShowResponse

class Komiku : MangaParser() {

    override val name = "Komiku"
    override val saveName = "komiku"
    override val hostUrl = "https://komiku.id"

    override suspend fun loadChapters(mangaLink: String, extra: Map<String, String>?): List<MangaChapter> {
        return client.get(mangaLink).document.select(".judulseries a").map {
            MangaChapter(
                it.text()
                    .substringAfter("Chapter ")
                    .substringBefore("HD")
                    .substringBefore("HQ")
                    .trim(),
                "$hostUrl${it.attr("href")}"
            )
        }.reversed()
    }

    override suspend fun loadImages(chapterLink: String): List<MangaImage> {
        return client.get(chapterLink).document.select("img.ww").map {
            MangaImage(it.attr("src"))
        }
    }

    override suspend fun search(query: String): List<ShowResponse> {
        return client.get("https://data.komiku.id/cari/?post_type=manga&s=$query")
            .document.select(".daftar .bge").map {
                val kan = it.select(".kan a")
                val img = it.select("img").attr("src")
                ShowResponse(
                    name = kan.text(),
                    link = kan.attr("href"),
                    coverUrl = img
                )
            }
    }
}