package org.jellyfin.androidtv.ui.itemdetail.rows

import androidx.leanback.widget.Row
import org.jellyfin.androidtv.data.itemtypes.BaseItem
import org.jellyfin.androidtv.data.itemtypes.ImageCollection
import org.jellyfin.androidtv.ui.itemdetail.actions.Action

class DetailsOverviewRow(
	val item: BaseItem,
	val actions: List<Action>,
	val primaryImage: ImageCollection.Image?,
	val backdrops: List<ImageCollection.Image>
) : Row()
