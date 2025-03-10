package com.github.andreyasadchy.xtra.ui.games

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.andreyasadchy.xtra.model.Account
import com.github.andreyasadchy.xtra.model.offline.LocalFollowGame
import com.github.andreyasadchy.xtra.repository.ApiRepository
import com.github.andreyasadchy.xtra.repository.LocalFollowGameRepository
import com.github.andreyasadchy.xtra.util.C
import com.github.andreyasadchy.xtra.util.DownloadUtils
import com.github.andreyasadchy.xtra.util.SingleLiveEvent
import com.github.andreyasadchy.xtra.util.TwitchApiHelper
import com.github.andreyasadchy.xtra.util.prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class GamePagerViewModel @Inject constructor(
    private val repository: ApiRepository,
    private val localFollowsGame: LocalFollowGameRepository) : ViewModel() {

    private val _integrity by lazy { SingleLiveEvent<Boolean>() }
    val integrity: LiveData<Boolean>
        get() = _integrity

    val follow = MutableLiveData<Pair<Boolean, String?>>()
    private var updatedLocalGame = false

    fun isFollowingGame(context: Context, gameId: String?, gameName: String?) {
        if (!follow.isInitialized) {
            viewModelScope.launch {
                try {
                    val setting = context.prefs().getString(C.UI_FOLLOW_BUTTON, "0")?.toInt() ?: 0
                    val gqlHeaders = TwitchApiHelper.getGQLHeaders(context, true)
                    val isFollowing = if (setting == 0 && !gqlHeaders[C.HEADER_TOKEN].isNullOrBlank()) {
                        gameName?.let {
                            repository.loadGameFollowing(gqlHeaders, gameName)
                        } == true
                    } else {
                        gameId?.let {
                            localFollowsGame.getFollowByGameId(it)
                        } != null
                    }
                    follow.postValue(Pair(isFollowing, null))
                } catch (e: Exception) {
                    if (e.message == "failed integrity check") {
                        _integrity.postValue(true)
                    }
                }
            }
        }
    }

    fun saveFollowGame(context: Context, gameId: String?, gameSlug: String?, gameName: String?) {
        GlobalScope.launch {
            val setting = context.prefs().getString(C.UI_FOLLOW_BUTTON, "0")?.toInt() ?: 0
            val account = Account.get(context)
            val helixClientId = context.prefs().getString(C.HELIX_CLIENT_ID, "ilfexgv3nnljz3isbm257gzwrzr7bi")
            val gqlHeaders = TwitchApiHelper.getGQLHeaders(context, true)
            try {
                if (setting == 0 && !gqlHeaders[C.HEADER_TOKEN].isNullOrBlank()) {
                    val errorMessage = repository.followGame(gqlHeaders, gameId)
                    follow.postValue(Pair(true, errorMessage))
                } else {
                    if (gameId != null) {
                        try {
                            Glide.with(context)
                                .asBitmap()
                                .load(TwitchApiHelper.getTemplateUrl(repository.loadGameBoxArt(gameId, helixClientId, account.helixToken, gqlHeaders), "game"))
                                .into(object: CustomTarget<Bitmap>() {
                                    override fun onLoadCleared(placeholder: Drawable?) {

                                    }

                                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                        DownloadUtils.savePng(context, "box_art", gameId, resource)
                                    }
                                })
                        } catch (e: Exception) {

                        }
                        val downloadedLogo = File(context.filesDir.toString() + File.separator + "box_art" + File.separator + "${gameId}.png").absolutePath
                        localFollowsGame.saveFollow(LocalFollowGame(gameId, gameSlug, gameName, downloadedLogo))
                        follow.postValue(Pair(true, null))
                    }
                }
            } catch (e: Exception) {
                if (e.message == "failed integrity check") {
                    _integrity.postValue(true)
                }
            }
        }
    }

    fun deleteFollowGame(context: Context, gameId: String?) {
        GlobalScope.launch {
            val setting = context.prefs().getString(C.UI_FOLLOW_BUTTON, "0")?.toInt() ?: 0
            val gqlHeaders = TwitchApiHelper.getGQLHeaders(context, true)
            try {
                if (setting == 0 && !gqlHeaders[C.HEADER_TOKEN].isNullOrBlank()) {
                    val errorMessage = repository.unfollowGame(gqlHeaders, gameId)
                    follow.postValue(Pair(false, errorMessage))
                } else {
                    if (gameId != null) {
                        localFollowsGame.getFollowByGameId(gameId)?.let { localFollowsGame.deleteFollow(context, it) }
                        follow.postValue(Pair(false, null))
                    }
                }
            } catch (e: Exception) {
                if (e.message == "failed integrity check") {
                    _integrity.postValue(true)
                }
            }
        }
    }

    fun updateLocalGame(context: Context, gameId: String?, gameName: String?) {
        if (!updatedLocalGame) {
            updatedLocalGame = true
            GlobalScope.launch {
                if (gameId != null) {
                    try {
                        val get = repository.loadGameBoxArt(
                            gameId = gameId,
                            helixClientId = context.prefs().getString(C.HELIX_CLIENT_ID, "ilfexgv3nnljz3isbm257gzwrzr7bi"),
                            helixToken = Account.get(context).helixToken,
                            gqlHeaders = TwitchApiHelper.getGQLHeaders(context)
                        )
                        Glide.with(context)
                            .asBitmap()
                            .load(TwitchApiHelper.getTemplateUrl(get, "game"))
                            .into(object: CustomTarget<Bitmap>() {
                                override fun onLoadCleared(placeholder: Drawable?) {

                                }

                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    DownloadUtils.savePng(context, "box_art", gameId, resource)
                                }
                            })
                    } catch (e: Exception) {

                    }
                    val downloadedLogo = File(context.filesDir.toString() + File.separator + "box_art" + File.separator + "${gameId}.png").absolutePath
                    localFollowsGame.getFollowByGameId(gameId)?.let { localFollowsGame.updateFollow(it.apply {
                        this.gameName = gameName
                        boxArt = downloadedLogo }) }
                }
            }
        }
    }
}
