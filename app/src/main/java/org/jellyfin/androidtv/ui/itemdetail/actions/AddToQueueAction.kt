package org.jellyfin.androidtv.ui.itemdetail.actions

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.data.itemtypes.BaseItem
import org.jellyfin.androidtv.ui.playback.MediaManager
import org.jellyfin.androidtv.util.apiclient.getItem
import org.jellyfin.apiclient.interaction.ApiClient
import org.koin.core.get

private const val LOG_TAG = "AddToQueueAction"

class AddToQueueAction(context: Context, private val item: BaseItem) : Action {
	override val visible = MutableLiveData(true)
	override val text = MutableLiveData(context.getString(R.string.lbl_add_to_queue))
	override val icon = MutableLiveData(context.getDrawable(R.drawable.ic_add)!!)

	override suspend fun onClick(view: View?) {
		// TODO if audio is implemented, check here wheter we got an audio file and add it to audio queue
		val retrieved = get<ApiClient>().getItem(item.id)
		if (retrieved == null) {
			Log.e(LOG_TAG, "Failed adding item %s to queue because retrieval failed!".format(item.id))
		} else {
			MediaManager.addToVideoQueue(retrieved)
		}
	}
}
