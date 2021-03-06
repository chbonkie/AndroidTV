package org.jellyfin.androidtv.ui.itemdetail.fragments

import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ClassPresenterSelector
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.data.itemtypes.Artist
import org.jellyfin.androidtv.ui.itemdetail.actions.InstantMixAction
import org.jellyfin.androidtv.ui.itemdetail.actions.PlayFromBeginningAction
import org.jellyfin.androidtv.ui.itemdetail.actions.ShuffleAction
import org.jellyfin.androidtv.ui.itemdetail.actions.ToggleFavoriteAction
import org.jellyfin.androidtv.ui.itemdetail.presenters.ItemPresenter
import org.jellyfin.androidtv.ui.itemdetail.rows.DetailsOverviewRow
import org.jellyfin.androidtv.util.addIfNotEmpty
import org.jellyfin.androidtv.util.apiclient.getAlbumsForArtist
import org.jellyfin.androidtv.util.apiclient.getSimilarItems
import org.jellyfin.androidtv.util.dp
import org.jellyfin.apiclient.interaction.ApiClient
import org.koin.android.ext.android.get

class ArtistDetailsFragment(private val artist: Artist) : BaseDetailsFragment<Artist>(artist) {
	// Action definitions
	private val actions by lazy {
		val item = MutableLiveData(artist)

		listOf(
			PlayFromBeginningAction(requireContext(), item),
			InstantMixAction(requireContext(), item),
			ShuffleAction(requireContext(), item),
			ToggleFavoriteAction(requireContext(), item)
		)
	}

	// Row definitions
	private val detailRow by lazy { DetailsOverviewRow(artist, actions, artist.images.primary, artist.images.backdrops) }
	private val albumsRow by lazy { createListRow(getString(R.string.lbl_albums), emptyList(), ItemPresenter(requireContext(), 150.dp, 150.dp, false)) }
	private val relatedItemsRow by lazy { createListRow(getString(R.string.lbl_similar_items_library), emptyList(), ItemPresenter(requireContext(), 150.dp, 150.dp, false)) }

	override suspend fun onCreateAdapters(rowSelector: ClassPresenterSelector, rowAdapter: ArrayObjectAdapter) {
		super.onCreateAdapters(rowSelector, rowAdapter)

		// Retrieve additional info
		loadAdditionalInformation()

		// Add rows
		rowAdapter.apply {
			add(detailRow)
			addIfNotEmpty(albumsRow)
			addIfNotEmpty(relatedItemsRow)
		}
	}

	private suspend fun loadAdditionalInformation() = withContext(Dispatchers.IO) {
		// Get additional information asynchronously
		awaitAll(
			async {
				val albums = get<ApiClient>().getAlbumsForArtist(artist).orEmpty()
				(albumsRow.adapter as ArrayObjectAdapter).apply { albums.forEach(::add) }
			},
			async {
				val relatedItems = get<ApiClient>().getSimilarItems(artist).orEmpty()
				(relatedItemsRow.adapter as ArrayObjectAdapter).apply { relatedItems.forEach(::add) }
			}
		)
	}
}
