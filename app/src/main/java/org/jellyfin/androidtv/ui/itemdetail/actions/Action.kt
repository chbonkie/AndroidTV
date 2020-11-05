package org.jellyfin.androidtv.ui.itemdetail.actions

import android.graphics.drawable.Drawable
import android.view.View
import androidx.lifecycle.LiveData
import org.koin.core.KoinComponent

interface Action : KoinComponent {
	val visible: LiveData<Boolean>
	val text: LiveData<String>
	val icon: LiveData<Drawable>

	suspend fun onClick(view: View?)
}
