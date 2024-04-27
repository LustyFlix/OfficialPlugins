// use an integer for version numbers
version = 1


cloudstream {
    language = "en"
    // All of these properties are optional, you can safely remove them

     description = "Film1K.Com"
     authors = listOf("Queen Medusa")

    /**
     * Status int as the following:
     * 0: Down
     * 1: Ok
     * 2: Slow
     * 3: Beta only
     * */
    status = 1 // will be 3 if unspecified
    tvTypes = listOf(
        "Movie",
    )

    iconUrl = "https://www.film1k.com/wp-content/uploads/2023/05/cropped-film1k-1.png"
}
