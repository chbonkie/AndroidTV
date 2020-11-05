package org.jellyfin.androidtv.ui.itemdetail.fragments

import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ClassPresenterSelector
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.data.itemtypes.Album
import org.jellyfin.androidtv.ui.itemdetail.actions.InstantMixAction
import org.jellyfin.androidtv.ui.itemdetail.actions.PlayFromBeginningAction
import org.jellyfin.androidtv.ui.itemdetail.actions.ShuffleAction
import org.jellyfin.androidtv.ui.itemdetail.actions.ToggleFavoriteAction
import org.jellyfin.androidtv.ui.itemdetail.presenters.ItemPresenter
import org.jellyfin.androidtv.ui.itemdetail.presenters.SongPresenter
import org.jellyfin.androidtv.ui.itemdetail.rows.DetailsOverviewRow
import org.jellyfin.androidtv.util.addIfNotEmpty
import org.jellyfin.androidtv.util.apiclient.getAlbumsForArtists
import org.jellyfin.androidtv.util.apiclient.getSimilarItems
import org.jellyfin.androidtv.util.apiclient.getSongsForAlbum
import org.jellyfin.androidtv.util.dp
import org.jellyfin.apiclient.interaction.ApiClient
import org.koin.android.ext.android.get

class AlbumDetailsFragment(private val album: Album) : BaseDetailsFragment<Album>(album) {
	// Action definitions
	private val actions by lazy {
		val item = MutableLiveData(album)

		listOf(
			PlayFromBeginningAction(requireContext(), item),
			InstantMixAction(requireContext(), item),
			ShuffleAction(requireContext(), item),
			ToggleFavoriteAction(requireContext(), item)
		)
	}

	// Row definitions
	private val detailRow by lazy { DetailsOverviewRow(album, actions, album.images.primary, album.images.backdrops) }
	private val songsRow by lazy { createVerticalListRow(getString(R.string.lbl_songs), SongPresenter()) }
	private val relatedDiscographyRow by lazy { createListRow(getString(R.string.lbl_more_from_x, album.artist.joinToString(", ") { it.name }), emptyList(), ItemPresenter(requireContext(), 150.dp, 150.dp, false)) }
	private val relatedItemsRow by lazy { createListRow(getString(R.string.lbl_similar_items_library), emptyList(), ItemPresenter(requireContext(), 150.dp, 150.dp, false)) }

	override suspend fun onCreateAdapters(rowSelector: ClassPresenterSelector, rowAdapter: ArrayObjectAdapter) {
		super.onCreateAdapters(rowSelector, rowAdapter)

		// Retrieve additional info
		loadAdditionalInformation()

		// Add rows
		rowAdapter.apply {
			add(detailRow)
			addIfNotEmpty(songsRow)
			addIfNotEmpty(relatedDiscographyRow)
			addIfNotEmpty(relatedItemsRow)
		}
	}

	private suspend fun loadAdditionalInformation() = withContext(Dispatchers.IO) {
		// Get additional information asynchronously
		awaitAll(
			async {
				val songs = get<ApiClient>().getSongsForAlbum(album.id).orEmpty()
				(songsRow.adapter as ArrayObjectAdapter).apply { songs.forEach(::add) }
			},
			async {
				val relatedDiscography = get<ApiClient>().getAlbumsForArtists(album.artist.map { it.id }.toTypedArray()).orEmpty()
				(relatedDiscographyRow.adapter as ArrayObjectAdapter).apply { relatedDiscography.forEach(::add) }
			},
			async {
				val relatedItems = get<ApiClient>().getSimilarItems(album).orEmpty()
				(relatedItemsRow.adapter as ArrayObjectAdapter).apply { relatedItems.forEach(::add) }
			}
		)
	}
}

