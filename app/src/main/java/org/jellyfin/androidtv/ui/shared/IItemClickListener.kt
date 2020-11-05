package org.jellyfin.androidtv.ui.shared

interface IItemClickListener {
	suspend fun onItemClicked(item: Any?)
}
