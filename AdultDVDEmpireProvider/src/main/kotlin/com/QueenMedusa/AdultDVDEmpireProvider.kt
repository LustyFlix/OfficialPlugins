package com.QueenMedusa

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.LoadResponse.Companion.addActors
import com.lagradost.cloudstream3.LoadResponse.Companion.addTrailer
import com.lagradost.cloudstream3.mvvm.safeApiCall
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.loadExtractor
import org.jsoup.nodes.Element
import java.net.URI

open class AdultDVDEmpireProvider : MainAPI() {
    override var mainUrl = "https://www.adultdvdempire.com"
    private var directUrl = ""
    override var name = "AdultDVDEmpire"
    override val hasMainPage = true
    override var lang = "en"
    override val supportedTypes = setOf(TvType.Movie)


    override val mainPage = mainPageOf(
        "unlimited/94173/studio/viv-thomas-streaming-videos.html" to "Viv Thomas",
        "unlimited/94113/studio/girlsway-streaming-videos.html" to "Girlsway",
        "unlimited/23836/studio/girlfriends-films-streaming-videos.html" to "Girlfriends Films",
        "unlimited/popular-streaming-videos.html" to "Popular Porn Movies",
    )

    override suspend fun getMainPage(
        page: Int,
        request: MainPageRequest
    ): HomePageResponse {
        val document = app.get("$mainUrl/${request.data}?page=$page").document
        val home = document.select("div.grid-item.col-xs-6.col-sm-4.col-md-3").mapNotNull {
            it.toSearchResult()
        }
        return newHomePageResponse(request.name, home)
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val title = this.selectFirst("a.boxcover")?.attr("title")?.trim() ?: return null
        val href = fixUrl(this.selectFirst("a.boxcover")?.attr("href").toString())
        val posterUrl = fixUrlNull(this.selectFirst("a.boxcover picture source:nth-child(1)")?.attr("srcset"))

        return newMovieSearchResponse(title, href, TvType.Movie) {
            this.posterUrl = posterUrl
        }
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val document = app.get("$mainUrl/unlimited/streaming-videos/search?q=$query").document

        return document.select("div.grid-item.col-xs-6.col-sm-4.col-md-3").mapNotNull {
            it.toSearchResult()
        }
    }

    override suspend fun load(url: String): LoadResponse? {
        val request = app.get(url)
        directUrl = getBaseUrl(request.url)
        val document = request.document
        val title = document.selectFirst("h1.spacing-bottom")?.text()?.trim() ?: return null
        val poster = fixUrlNull(document.selectFirst("div.col-sm-3.col-md-2.spacing-bottom a")?.attr("href"))
        val tags = document.select("div.col-sm-6.col-lg-7.spacing-bottom p:nth-child(3) a").map { it.text() }
        //val year = document.select("span.Views a").text().trim().toIntOrNull()
        //val description = document.selectFirst("div.Description p")?.text()?.trim()

        //val rating = document.select("div.mvici-right > div.imdb_r span").text().toRatingInt()
        val actors = document.select("div.row.item-grid.item-grid-girls > div.col-xs-6.col-sm-4.col-md-2.spacing-bottom").map {
            Actor(
                it.select("a.boxcover.girl img").attr("title"),
                it.select("a.boxcover.girl img").attr("src")
            )
        }
        val trailer = fixUrlNull(document.selectFirst("iframe")?.attr("src"))
        val recommendations = document.select("div.col-xs-6 col-sm-12 col-md-6 grid-item").mapNotNull {
            it.toSearchResult()
        }

        return newMovieLoadResponse(title, url, TvType.Movie, url) {
                this.posterUrl = poster
                //this.year = year
                //this.plot = description
                this.tags = tags
                //this.rating = rating
                addActors(actors)
                addTrailer(trailer)
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
