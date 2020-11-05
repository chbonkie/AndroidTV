package org.jellyfin.androidtv.ui.itemdetail.presenters

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.leanback.widget.Presenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.TvApp
import org.jellyfin.androidtv.data.itemtypes.BaseItem
import org.jellyfin.androidtv.data.itemtypes.PlayableItem
import org.jellyfin.androidtv.ui.FavoriteBadge
import org.jellyfin.androidtv.ui.MultiBadgeImageCardView
import org.jellyfin.androidtv.ui.WatchedBadge
import org.jellyfin.androidtv.ui.itemdetail.DetailsActivity
import org.jellyfin.androidtv.ui.shared.IItemClickListener


class ItemPresenter(private val context: Context,
					private val imageWidth: Int,
					private val imageHeight: Int,
					private val showDescription: Boolean) : Presenter(), IItemClickListener {

	override suspend fun onItemClicked(item: Any?) {
		requireNotNull(item)
		val baseItem = item as BaseItem
		val intent = Intent(context, DetailsActivity::class.java)
		intent.putExtra(DetailsActivity.EXTRA_ITEM_ID, baseItem.id)

		context.startActivity(intent)
	}

	override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
		return ViewHolder(MultiBadgeImageCardView(ContextThemeWrapper(parent!!.context, R.style.MarqueeImageCardViewTheme)))
	}

	override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
		val baseItem = item as BaseItem
		val cardView = (viewHolder.view as MultiBadgeImageCardView).apply {
			titleText = baseItem.title
			contentText = if (showDescription) baseItem.description else null
			isFocusable = true
			isFocusableInTouchMode = true
			setMainImageDimensions(imageWidth, imageHeight)
			mainImageDrawable =  ContextCompat.getDrawable(context, R.drawable.tile_port_video)

			if (baseItem is PlayableItem)
				setBadge(MultiBadgeImageCardView.BadgeLocation.TOP_RIGHT, WatchedBadge(context, baseItem))

			setBadge(MultiBadgeImageCardView.BadgeLocation.BOTTOM_RIGHT, FavoriteBadge(context, baseItem))
		}


		if (baseItem.images.primary != null) {
			GlobalScope.launch(Dispatchers.Main) {
				cardView.mainImageDrawable = BitmapDrawable(baseItem.images.primary.getBitmap(TvApp.getApplication()))
			}
		}

	}

	override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
		val cardView = viewHolder!!.view as MultiBadgeImageCardView

		// TODO: Somehow release BitmapDrawable?
	}

	companion object {
		private const val LOG_TAG = "ItemPresenter"
	}
}
