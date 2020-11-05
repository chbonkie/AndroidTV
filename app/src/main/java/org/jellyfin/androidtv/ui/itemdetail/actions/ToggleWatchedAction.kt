package org.jellyfin.androidtv.ui.itemdetail.actions

import android.content.Context
import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.TvApp
import org.jellyfin.androidtv.data.itemtypes.PlayableItem
import org.jellyfin.androidtv.util.apiclient.markPlayed
import org.jellyfin.androidtv.util.apiclient.markUnplayed
import org.jellyfin.apiclient.interaction.ApiClient
import org.koin.core.get

class ToggleWatchedAction(context: Context, val item: MutableLiveData<out PlayableItem>) : ToggleableAction {
	override val visible = MutableLiveData(true)
	override val text = MutableLiveData(context.getString(R.string.lbl_watched))
	override val icon = MutableLiveData(context.getDrawable(R.drawable.ic_watch)!!)
	override val active = MediatorLiveData<Boolean>().apply {
		addSource(item) { value = it.played }
	}

	override suspend fun onClick(view: View?) {
		val itemValue = item.value ?: return
		val application = TvApp.getApplication()

		//todo catch exceptions (show toast?)
		val response = if (itemValue.played) get<ApiClient>().markUnplayed(itemValue.id, application.currentUser.id)
		else get<ApiClient>().markPlayed(itemValue.id, application.currentUser.id, null)

		response?.let {
			itemValue.playbackPositionTicks = it.playbackPositionTicks
			itemValue.played = it.played

			item.value = itemValue
		}
	}
}
