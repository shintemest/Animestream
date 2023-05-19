package ani.saikou.parsers

import ani.saikou.Lazier
import ani.saikou.lazyList
import ani.saikou.parsers.manga.*

object MangaSources : MangaReadSources() {
    override val list: List<Lazier<BaseParser>> = lazyList(
        "MangaDex" to ::MangaDex,
    )
}

object HMangaSources : MangaReadSources() {
    val aList: List<Lazier<BaseParser>> = lazyList(
        "NineHentai" to ::NineHentai,
        "Manhwa18" to ::Manhwa18,
        "NHentai" to ::NHentai,
    )
    override val list = listOf(aList,MangaSources.list).flatten()
}
