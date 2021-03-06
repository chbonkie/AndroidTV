package org.jellyfin.androidtv.ui.itemdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jellyfin.androidtv.data.itemtypes.*
import org.jellyfin.androidtv.details.fragments.SeriesDetailsFragment
import org.jellyfin.androidtv.ui.itemdetail.fragments.AlbumDetailsFragment
import org.jellyfin.androidtv.ui.itemdetail.fragments.ArtistDetailsFragment
import org.jellyfin.androidtv.ui.itemdetail.fragments.EpisodeDetailsFragment
import org.jellyfin.androidtv.ui.itemdetail.fragments.MovieDetailsFragment
import org.jellyfin.androidtv.util.apiclient.getItem
import org.jellyfin.androidtv.util.apiclient.liftToNewFormat
import org.jellyfin.apiclient.interaction.ApiClient
import org.koin.android.ext.android.get

private const val LOG_TAG = "DetailsActivity"

class DetailsActivity : FragmentActivity() {
	private lateinit var fragment: Fragment

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val id = intent.getStringExtra(EXTRA_ITEM_ID)
		if (id == null) {
			Log.e(LOG_TAG, "No id was passed to Details Activity, closing automatically again.")
			finish()
			return
		}

		Log.i(LOG_TAG, "Opening item with id $id")

		GlobalScope.launch(Dispatchers.Main) {
			val baseItem = getBaseItemDtoForID(id) ?: return@launch
			Log.i(LOG_TAG, "Item ($id) type is ${baseItem.baseItemType}")

			val item = baseItem.liftToNewFormat()

			fragment = when (item) {
				// TV/Movie
				is Series -> SeriesDetailsFragment(item)
				is Season -> TODO("Season details are not yet implemented")
				is Movie -> MovieDetailsFragment(item)
				is Episode -> EpisodeDetailsFragment(item)
				is Video -> TODO("Video details are not yet implemented")
				is LocalTrailer -> TODO("Trailer details are not yet implemented")

				// Music
				is Artist -> ArtistDetailsFragment(item)
				is Album -> AlbumDetailsFragment(item)
				is Audio -> TODO("Audio details are not yet implemented")
			}

			supportFragmentManager.beginTransaction().add(android.R.id.content, fragment).commit()
		}
	}

	private suspend fun getBaseItemDtoForID(id: String) = withContext(Dispatchers.IO) {
		get<ApiClient>().getItem(id)
	}

	companion object {
		const val EXTRA_ITEM_ID = "id"

		fun start(context: Context, targetId: String) {
			val intent = Intent(context, DetailsActivity::class.java)
			intent.putExtra(EXTRA_ITEM_ID, targetId)
			context.startActivity(intent)
		}

		fun start(context: Context, target: BaseItem) = start(context, target.id)
	}
}
