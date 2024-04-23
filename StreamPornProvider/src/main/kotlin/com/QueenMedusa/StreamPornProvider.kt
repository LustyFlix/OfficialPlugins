package com.QueenMedusa

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.LoadResponse.Companion.addActors
import com.lagradost.cloudstream3.LoadResponse.Companion.addTrailer
import com.lagradost.cloudstream3.mvvm.safeApiCall
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.loadExtractor
import org.jsoup.nodes.Element
import java.net.URI

open class StreamPornProvider : MainAPI() {
    override var mainUrl = "https://streamporn.li"
    private var directUrl = ""
    override var name = "StreamPorn"
    override val hasMainPage = true
    override var lang = "en"
    override val supportedTypes = setOf(TvType.Movie)


    override val mainPage = mainPageOf(
        "director/brazzers" to "Brazzers",
        "director/reality-kings" to "Reality Kings",
        "director/mofos" to "Mofos",
        "director/evil-angel" to "Evil Angel",
        "director/new-sensations" to "New Sensations",
        "director/naughty-america" to "Naughty America",
        "movies" to "Popular Porn Movies",
    )

    override suspend fun getMainPage(
        page: Int,
        request: MainPageRequest
    ): HomePageResponse {
        val document = app.get("$mainUrl/${request.data}/page/$page").document
        val home = document.select("div.ml-item").mapNotNull {
            it.toSearchResult()
        }
        return newHomePageResponse(request.name, home)
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val title = this.selectFirst("h2")?.text()?.trim() ?: return null
        val href = fixUrl(this.selectFirst("a")?.attr("href").toString())
        val posterUrl = fixUrlNull(this.selectFirst("img")?.attr("src"))

        return newMovieSearchResponse(title, href, TvType.Movie) {
            this.posterUrl = posterUrl
        }
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val document = app.get("$mainUrl/?s=$query").document

        return document.select("div.ml-item").mapNotNull {
            it.toSearchResult()
        }
    }

    override suspend fun load(url: String): LoadResponse? {
        val request = app.get(url)
        directUrl = getBaseUrl(request.url)
        val document = request.document
        val title = document.selectFirst("div.mvic-desc h3")?.text()?.trim() ?: return null
        val poster = fixUrlNull(document.selectFirst("div.thumb.mvic-thumb img")?.attr("src"))
        val tags = document.select("div.mvici-left p:nth-child(5) a").map { it.text() }
        val year = document.select("div.mvici-right p:nth-child(3) a").text().trim()
            .toIntOrNull()
        val description = document.selectFirst("p.f-desc")?.text()?.trim()

        val rating = document.select("div.mvici-right > div.imdb_r span").text().toRatingInt()
        val actors = document.select("div.mvici-left p:nth-child(7) a").map { it.text() }
        val recommendations = document.select("div.ml-item").mapNotNull {
            it.toSearchResult()
        }

        return newMovieLoadResponse(title, url, TvType.Movie, url) {
                this.posterUrl = poster
                this.year = year
                this.plot = description
                this.tags = tags
                this.rating = rating
                addActors(actors)
                this.recommendations = recommendations
                
        }
        
        
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val doc = app.get(data).document

        doc.select("div.Rtable1-cell a").forEach {
            loadExtractor(fixUrl(it.attr("href")), data, subtitleCallback, callback)
        }
        
        return true
    }


    private fun String.getHost(): String {
        return fixTitle(URI(this).host.substringBeforeLast(".").substringAfterLast("."))
    }

    private fun getBaseUrl(url: String): String {
        return URI(url).let {
            "${it.scheme}://${it.host}"
        }
    }

}
