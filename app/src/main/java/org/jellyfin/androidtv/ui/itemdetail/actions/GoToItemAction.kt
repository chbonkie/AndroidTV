package org.jellyfin.androidtv.ui.itemdetail.actions

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.data.itemtypes.BaseItem
import org.jellyfin.androidtv.ui.itemdetail.DetailsActivity

class GoToItemAction(private val context: Context, label: String, private val targetId: String) : Action {
	constructor(context: Context, label: String, target: BaseItem) : this(context, label, target.id)

	override val visible = MutableLiveData(true)
	override val text = MutableLiveData(label)
	override val icon = MutableLiveData(context.getDrawable(R.drawable.ic_folder)!!)

	override suspend fun onClick(view: View?) {
		DetailsActivity.start(context, targetId)
	}
}
