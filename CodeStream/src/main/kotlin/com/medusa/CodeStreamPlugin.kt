
package com.medusa

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context
import com.medusa.Alions
import com.medusa.Animefever
import com.medusa.Comedyshow
import com.medusa.Embedrise
import com.medusa.Embedwish
import com.medusa.FilelionsTo
import com.medusa.FilemoonNl
import com.medusa.Flaswish
import com.medusa.Gdmirrorbot
import com.medusa.M4ufree
import com.medusa.Multimovies
import com.medusa.MultimoviesSB
import com.medusa.Mwish
import com.medusa.Netembed
import com.medusa.Playm4u
import com.medusa.Ridoo
import com.medusa.CodeStream
import com.medusa.Streamruby
import com.medusa.Streamvid
import com.medusa.Streamwish
import com.medusa.TravelR
import com.medusa.Uploadever
import com.medusa.UqloadsXyz
import com.medusa.VCloud
import com.medusa.Yipsu

@CloudstreamPlugin
class CodeStreamPlugin: Plugin() {
    override fun load(context: Context) {
        // All providers should be added in this manner. Please don't edit the providers list directly.
        registerMainAPI(CodeStream())
        registerMainAPI(CodeStreamLite())
        registerExtractorAPI(Animefever())
        registerExtractorAPI(Multimovies())
        registerExtractorAPI(MultimoviesSB())
        registerExtractorAPI(Yipsu())
        registerExtractorAPI(Mwish())
        registerExtractorAPI(TravelR())
        registerExtractorAPI(Playm4u())
        registerExtractorAPI(VCloud())

        registerExtractorAPI(M4ufree())
        registerExtractorAPI(Streamruby())
        registerExtractorAPI(Streamwish())
        registerExtractorAPI(FilelionsTo())
        registerExtractorAPI(Embedwish())
        registerExtractorAPI(UqloadsXyz())
        registerExtractorAPI(Uploadever())
        registerExtractorAPI(Netembed())
        registerExtractorAPI(Flaswish())
        registerExtractorAPI(Comedyshow())
        registerExtractorAPI(Ridoo())
        registerExtractorAPI(Streamvid())
        registerExtractorAPI(Embedrise())
        registerExtractorAPI(Gdmirrorbot())
        registerExtractorAPI(FilemoonNl())
        registerExtractorAPI(Alions())
    }
}