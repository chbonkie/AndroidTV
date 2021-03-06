package org.jellyfin.androidtv.ui.itemdetail.actions

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.TvApp
import org.jellyfin.androidtv.data.itemtypes.BaseItem
import org.jellyfin.androidtv.util.apiclient.updateFavoriteStatus
import org.jellyfin.apiclient.interaction.ApiClient
import org.koin.core.component.get

class ToggleFavoriteAction(val context: Context, val item: MutableLiveData<out BaseItem>) : ToggleableAction {
	override val visible: LiveData<Boolean> = MutableLiveData(true)
	override val text: LiveData<String> = MutableLiveData(context.getString(R.string.lbl_favorite))
	override val icon: LiveData<Drawable> = MutableLiveData(context.getDrawable(R.drawable.ic_heart)!!)
	override val active = MediatorLiveData<Boolean>().apply {
		addSource(item) { value = it.favorite }
	}

	override suspend fun onClick(view: View?) {
		val itemValue = item.value ?: return
		val application = TvApp.getApplication()

		//todo catch exceptions (show toast?)
		get<ApiClient>().updateFavoriteStatus(
			itemValue.id,
			application.currentUser.id,
			!itemValue.favorite
		)?.let {
			itemValue.favorite = it.isFavorite
			item.value = itemValue
		}
	}
}
