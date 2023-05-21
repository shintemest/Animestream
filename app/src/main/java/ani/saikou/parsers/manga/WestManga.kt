package ani.saikou.parsers.manga

import ani.saikou.Mapper
import ani.saikou.client
import ani.saikou.findBetween
import ani.saikou.parsers.MangaImage

class WestManga : Kiryuu() {
    override val name = "WestManga"
    override val saveName = "west_manga"
    override val hostUrl = "https://westmanga.info"

    override suspend fun loadImages(chapterLink: String): List<MangaImage> {
        return Mapper.parse<List<String>>(
            "[${ client.get(chapterLink).text.findBetween("\"images\":[", "]") }]"
        ).map {
            MangaImage(it)
        }
    }
}