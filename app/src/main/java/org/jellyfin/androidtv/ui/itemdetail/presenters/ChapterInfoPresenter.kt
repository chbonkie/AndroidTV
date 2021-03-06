package org.jellyfin.androidtv.ui.itemdetail.presenters

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.TvApp
import org.jellyfin.androidtv.data.itemtypes.ChapterInfo
import org.jellyfin.androidtv.ui.shared.IItemClickListener
import org.jellyfin.androidtv.util.PlaybackUtil
import org.jellyfin.androidtv.util.TimeUtils
import org.jellyfin.androidtv.util.apiclient.getItem
import org.jellyfin.androidtv.util.dp
import org.jellyfin.apiclient.interaction.ApiClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class ChapterInfoPresenter(private val context: Context) : Presenter(), IItemClickListener, KoinComponent {

	override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
		return ViewHolder(ImageCardView(ContextThemeWrapper(parent!!.context, R.style.MarqueeImageCardViewTheme)))
	}

	override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
		val chapterInfo = item as ChapterInfo
		val cardView = viewHolder.view as ImageCardView

		cardView.titleText = chapterInfo.name
		cardView.contentText = TimeUtils.formatMillis(chapterInfo.startPositionTicks)
		cardView.isFocusable = true
		cardView.isFocusableInTouchMode = true
		cardView.setMainImageDimensions(250.dp, 140.dp)
		cardView.mainImage =  ContextCompat.getDrawable(context, R.drawable.tile_chapter)

		if (chapterInfo.image != null) {
			GlobalScope.launch(Dispatchers.Main) {
				cardView.mainImage = BitmapDrawable(chapterInfo.image.getBitmap(TvApp.getApplication()))
			}
		}

	}

	override suspend fun onItemClicked(item: Any?) = withContext(Dispatchers.IO) {
		requireNotNull(item)
		val chapterInfo = item as ChapterInfo

		val baseItemDto = get<ApiClient>().getItem(chapterInfo.baseItem.id)
		if (baseItemDto == null) {
			Log.e(LOG_TAG, "Failed to get a base item for the given ID")
			return@withContext
		}
		PlaybackUtil.play(context, baseItemDto, chapterInfo.startPositionTicks, false)
	}

	override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
		val cardView = viewHolder!!.view as ImageCardView

		// TODO: Somehow release BitmapDrawable?
	}

	companion object {
		private const val LOG_TAG = "ChapterInfoPresenter"
	}
}
