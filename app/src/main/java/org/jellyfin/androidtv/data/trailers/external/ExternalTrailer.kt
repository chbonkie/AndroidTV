package org.jellyfin.androidtv.data.trailers.external

abstract class ExternalTrailer(val name: String, val playbackURL: String) {
	abstract val thumbnailURL: String?
}
