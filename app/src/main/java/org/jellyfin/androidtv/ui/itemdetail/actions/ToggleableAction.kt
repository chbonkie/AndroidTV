package org.jellyfin.androidtv.ui.itemdetail.actions

import androidx.lifecycle.LiveData

interface ToggleableAction : Action {
	val active: LiveData<Boolean>
}
