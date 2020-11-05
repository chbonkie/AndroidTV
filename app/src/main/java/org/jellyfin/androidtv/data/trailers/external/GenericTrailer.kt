package org.jellyfin.androidtv.data.trailers.external

class GenericTrailer(name: String, playbackURL: String) : ExternalTrailer(name, playbackURL) {
	override val thumbnailURL: String? = null
}
