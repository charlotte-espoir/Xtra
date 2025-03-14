package com.github.andreyasadchy.xtra.repository

import com.github.andreyasadchy.xtra.api.GraphQLApi
import com.github.andreyasadchy.xtra.model.chat.CheerEmote
import com.github.andreyasadchy.xtra.model.gql.channel.ChannelClipsDataResponse
import com.github.andreyasadchy.xtra.model.gql.channel.ChannelVideosDataResponse
import com.github.andreyasadchy.xtra.model.gql.channel.ChannelViewerListDataResponse
import com.github.andreyasadchy.xtra.model.gql.chat.ChannelPointsContextDataResponse
import com.github.andreyasadchy.xtra.model.gql.chat.ChatBadgesDataResponse
import com.github.andreyasadchy.xtra.model.gql.chat.EmoteCardResponse
import com.github.andreyasadchy.xtra.model.gql.chat.GlobalCheerEmotesDataResponse
import com.github.andreyasadchy.xtra.model.gql.chat.ModeratorsDataResponse
import com.github.andreyasadchy.xtra.model.gql.chat.UserEmotesDataResponse
import com.github.andreyasadchy.xtra.model.gql.chat.VipsDataResponse
import com.github.andreyasadchy.xtra.model.gql.clip.ClipDataResponse
import com.github.andreyasadchy.xtra.model.gql.clip.ClipVideoResponse
import com.github.andreyasadchy.xtra.model.gql.followed.FollowDataResponse
import com.github.andreyasadchy.xtra.model.gql.followed.FollowedChannelsDataResponse
import com.github.andreyasadchy.xtra.model.gql.followed.FollowedGamesDataResponse
import com.github.andreyasadchy.xtra.model.gql.followed.FollowedStreamsDataResponse
import com.github.andreyasadchy.xtra.model.gql.followed.FollowedVideosDataResponse
import com.github.andreyasadchy.xtra.model.gql.followed.FollowingGameDataResponse
import com.github.andreyasadchy.xtra.model.gql.followed.FollowingUserDataResponse
import com.github.andreyasadchy.xtra.model.gql.game.GameClipsDataResponse
import com.github.andreyasadchy.xtra.model.gql.game.GameDataResponse
import com.github.andreyasadchy.xtra.model.gql.game.GameStreamsDataResponse
import com.github.andreyasadchy.xtra.model.gql.game.GameVideosDataResponse
import com.github.andreyasadchy.xtra.model.gql.playlist.PlaybackAccessTokenResponse
import com.github.andreyasadchy.xtra.model.gql.search.SearchChannelDataResponse
import com.github.andreyasadchy.xtra.model.gql.search.SearchGameDataResponse
import com.github.andreyasadchy.xtra.model.gql.search.SearchVideosDataResponse
import com.github.andreyasadchy.xtra.model.gql.stream.StreamsDataResponse
import com.github.andreyasadchy.xtra.model.gql.stream.ViewersDataResponse
import com.github.andreyasadchy.xtra.model.gql.tag.FreeformTagDataResponse
import com.github.andreyasadchy.xtra.model.gql.tag.TagGameDataResponse
import com.github.andreyasadchy.xtra.model.gql.video.VideoGamesDataResponse
import com.github.andreyasadchy.xtra.model.gql.video.VideoMessagesDataResponse
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "GraphQLRepository"

@Singleton
class GraphQLRepository @Inject constructor(private val graphQL: GraphQLApi) {

    suspend fun loadPlaybackAccessToken(headers: Map<String, String>, login: String? = null, vodId: String? = null, playerType: String?): PlaybackAccessTokenResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "f51c71103d886ee77e4ff84bfc92352acf66a120413f8e99cfcea092e600936f")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "PlaybackAccessToken")
            add("variables", JsonObject().apply {
                addProperty("isLive", !login.isNullOrBlank())
                addProperty("login", login ?: "")
                addProperty("isVod", !vodId.isNullOrBlank())
                addProperty("vodID", vodId ?: "")
                addProperty("playerType", playerType)
            })
        }
        return graphQL.getPlaybackAccessToken(headers, json)
    }

    suspend fun loadClipUrls(headers: Map<String, String>, slug: String?): Map<String, String>? = withContext(Dispatchers.IO) {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "36b89d2507fce29e5ca551df756d27c1cfe079e2609642b4390aa4c35796eb11")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "VideoAccessToken_Clip")
            add("variables", JsonObject().apply {
                addProperty("slug", slug)
            })
        }
        val response = graphQL.getClipUrls(headers, json)
        response.body()?.data?.withIndex()?.associateBy({
            if (!it.value.quality.isNullOrBlank()) {
                if ((it.value.frameRate ?: 0) < 60) {
                    "${it.value.quality}p"
                } else {
                    "${it.value.quality}p${it.value.frameRate}"
                }
            } else {
                it.index.toString()
            }
        }, { it.value.url })
    }

    suspend fun loadClipData(headers: Map<String, String>, slug: String?): ClipDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "9796f0a2f336c1e8a78d8ff362291cc2d43bbb1036d39f146d22d636f03f641d")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "ChannelClipCore")
            add("variables", JsonObject().apply {
                addProperty("clipSlug", slug)
            })
        }
        return graphQL.getClipData(headers, json)
    }

    suspend fun loadClipVideo(headers: Map<String, String>, slug: String?): ClipVideoResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "9aa558e066a22227c5ef2c0a8fded3aaa57d35181ad15f63df25bff516253a90")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "ChatClip")
            add("variables", JsonObject().apply {
                addProperty("clipSlug", slug)
            })
        }
        return graphQL.getClipVideo(headers, json)
    }

    suspend fun loadTopGames(headers: Map<String, String>, tags: List<String>?, limit: Int?, cursor: String?): GameDataResponse {
        val array = JsonArray()
        if (tags != null) {
            for (i in tags) {
                array.add(i)
            }
        }
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "2f67f71ba89f3c0ed26a141ec00da1defecb2303595f5cda4298169549783d9e")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "BrowsePage_AllDirectories")
            add("variables", JsonObject().apply {
                addProperty("cursor", cursor)
                addProperty("limit", limit)
                add("options", JsonObject().apply {
                    addProperty("sort", "VIEWER_COUNT")
                    add("tags", array)
                })
            })
        }
        return graphQL.getTopGames(headers, json)
    }

    suspend fun loadTopStreams(headers: Map<String, String>, tags: List<String>?, limit: Int?, cursor: String?): StreamsDataResponse {
        val array = JsonArray()
        if (tags != null) {
            for (i in tags) {
                array.add(i)
            }
        }
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "19cd6b171185fa3937c4fd6f80363a7a38a7dc269c43eb205732159bc932cb01")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "BrowsePage_Popular")
            add("variables", JsonObject().apply {
                addProperty("cursor", cursor)
                addProperty("limit", limit)
                addProperty("platformType", "all")
                addProperty("sortTypeIsRecency", false)
                add("options", JsonObject().apply {
                    add("freeformTags", array)
                    addProperty("sort", "VIEWER_COUNT")
                })
            })
        }
        return graphQL.getTopStreams(headers, json)
    }

    suspend fun loadGameStreams(headers: Map<String, String>, gameSlug: String?, sort: String?, tags: List<String>?, limit: Int?, cursor: String?): GameStreamsDataResponse {
        val array = JsonArray()
        if (tags != null) {
            for (i in tags) {
                array.add(i)
            }
        }
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "3c9a94ee095c735e43ed3ad6ce6d4cbd03c4c6f754b31de54993e0d48fd54e30")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "DirectoryPage_Game")
            add("variables", JsonObject().apply {
                addProperty("cursor", cursor)
                addProperty("limit", limit)
                addProperty("slug", gameSlug)
                addProperty("sortTypeIsRecency", false)
                add("options", JsonObject().apply {
                    add("freeformTags", array)
                    addProperty("sort", sort)
                })
            })
        }
        return graphQL.getGameStreams(headers, json)
    }

    suspend fun loadGameVideos(headers: Map<String, String>, gameSlug: String?, type: String?, sort: String?, limit: Int?, cursor: String?): GameVideosDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "e7db9e6e9ca5ab518adcfe6813b236e9cd9c3a9a70663e698c909ca173ecf1f1")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "DirectoryVideos_Game")
            add("variables", JsonObject().apply {
                if (type != null) {
                    addProperty("broadcastTypes", type)
                }
                addProperty("followedCursor", cursor)
                addProperty("slug", gameSlug)
                addProperty("videoLimit", limit)
                addProperty("videoSort", sort)
            })
        }
        return graphQL.getGameVideos(headers, json)
    }

    suspend fun loadGameClips(headers: Map<String, String>, gameSlug: String?, sort: String?, limit: Int?, cursor: String?): GameClipsDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "1dd78a0ab0008cb6907046c71d94b6798d59d3828c2720a55fc3f16785ff9769")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "ClipsCards__Game")
            add("variables", JsonObject().apply {
                add("criteria", JsonObject().apply {
                    addProperty("filter", sort)
                })
                addProperty("cursor", cursor)
                addProperty("categorySlug", gameSlug)
                addProperty("limit", limit)
            })
        }
        return graphQL.getGameClips(headers, json)
    }

    suspend fun loadChannelVideos(headers: Map<String, String>, channelLogin: String?, type: String?, sort: String?, limit: Int?, cursor: String?): ChannelVideosDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "a937f1d22e269e39a03b509f65a7490f9fc247d7f83d6ac1421523e3b68042cb")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "FilterableVideoTower_Videos")
            add("variables", JsonObject().apply {
                addProperty("broadcastType", type)
                addProperty("cursor", cursor)
                addProperty("channelOwnerLogin", channelLogin)
                addProperty("limit", limit)
                addProperty("videoSort", sort)
            })
        }
        return graphQL.getChannelVideos(headers, json)
    }

    suspend fun loadChannelClips(headers: Map<String, String>, channelLogin: String?, sort: String?, limit: Int?, cursor: String?): ChannelClipsDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "af4bd82dcacdda3d693ed274e29b2c97b4990de2b1e683994b16ea26f3abd1af")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "ClipsCards__User")
            add("variables", JsonObject().apply {
                add("criteria", JsonObject().apply {
                    addProperty("filter", sort)
                })
                addProperty("cursor", cursor)
                addProperty("login", channelLogin)
                addProperty("limit", limit)
            })
        }
        return graphQL.getChannelClips(headers, json)
    }

    suspend fun loadSearchChannels(headers: Map<String, String>, query: String?, cursor: String?): SearchChannelDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "6ea6e6f66006485e41dbe3ebd69d5674c5b22896ce7b595d7fce6411a3790138")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "SearchResultsPage_SearchResults")
            add("variables", JsonObject().apply {
                add("options", JsonObject().apply {
                    add("targets", JsonArray().apply {
                        add(JsonObject().apply {
                            addProperty("cursor", cursor)
                            addProperty("index", "CHANNEL")
                        })
                    })
                })
                addProperty("query", query)
            })
        }
        return graphQL.getSearchChannels(headers, json)
    }

    suspend fun loadSearchGames(headers: Map<String, String>, query: String?, cursor: String?): SearchGameDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "6ea6e6f66006485e41dbe3ebd69d5674c5b22896ce7b595d7fce6411a3790138")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "SearchResultsPage_SearchResults")
            add("variables", JsonObject().apply {
                add("options", JsonObject().apply {
                    add("targets", JsonArray().apply {
                        add(JsonObject().apply {
                            addProperty("cursor", cursor)
                            addProperty("index", "GAME")
                        })
                    })
                })
                addProperty("query", query)
            })
        }
        return graphQL.getSearchGames(headers, json)
    }

    suspend fun loadSearchVideos(headers: Map<String, String>, query: String?, cursor: String?): SearchVideosDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "6ea6e6f66006485e41dbe3ebd69d5674c5b22896ce7b595d7fce6411a3790138")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "SearchResultsPage_SearchResults")
            add("variables", JsonObject().apply {
                add("options", JsonObject().apply {
                    add("targets", JsonArray().apply {
                        add(JsonObject().apply {
                            addProperty("cursor", cursor)
                            addProperty("index", "VOD")
                        })
                    })
                })
                addProperty("query", query)
            })
        }
        return graphQL.getSearchVideos(headers, json)
    }

    suspend fun loadFreeformTags(headers: Map<String, String>, query: String?, limit: Int?): FreeformTagDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "8bc91a618bb5f0c5f9bc19195028c9f4a6a1b8651cf5bd8e4f2408124cdf465a")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "SearchFreeformTags")
            add("variables", JsonObject().apply {
                addProperty("first", limit)
                addProperty("userQuery", query ?: "")
            })
        }
        return graphQL.getFreeformTags(headers, json)
    }

    suspend fun loadGameTags(headers: Map<String, String>, query: String?, limit: Int?): TagGameDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "b4cb189d8d17aadf29c61e9d7c7e7dcfc932e93b77b3209af5661bffb484195f")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "SearchCategoryTags")
            add("variables", JsonObject().apply {
                addProperty("limit", limit)
                addProperty("userQuery", query ?: "")
            })
        }
        return graphQL.getGameTags(headers, json)
    }

    suspend fun loadChatBadges(headers: Map<String, String>, channelLogin: String?): ChatBadgesDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "86f43113c04606e6476e39dcd432dee47c994d77a83e54b732e11d4935f0cd08")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "ChatList_Badges")
            add("variables", JsonObject().apply {
                addProperty("channelLogin", channelLogin)
            })
        }
        return graphQL.getChatBadges(headers, json)
    }

    suspend fun loadCheerEmotes(headers: Map<String, String>, channelLogin: String?, animateGifs: Boolean): List<CheerEmote> {
        val data = mutableListOf<CheerEmote>()
        val tiers = mutableListOf<GlobalCheerEmotesDataResponse.CheerTier>()
        val global = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "6a265b86f3be1c8d11bdcf32c183e106028c6171e985cc2584d15f7840f5fee6")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "BitsConfigContext_Global")
        }
        val response = graphQL.getGlobalCheerEmotes(headers, global)
        tiers.addAll(response.tiers)
        val channel = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "368aaf9c04d3876cdd0076c105af2cd44b3bfd51a688462152ed4d3a5657e2b9")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "BitsConfigContext_Channel")
            add("variables", JsonObject().apply {
                addProperty("login", channelLogin)
            })
        }
        tiers.addAll(graphQL.getChannelCheerEmotes(headers, channel).data)
        val background = (response.config.backgrounds.find { it == "dark" } ?: response.config.backgrounds.lastOrNull())
        val format = if (animateGifs) {
            response.config.types.entries.find { it.key == "animated" } ?: response.config.types.entries.find { it.key == "static" }
        } else {
            response.config.types.entries.find { it.key == "static" }
        } ?: response.config.types.entries.lastOrNull()
        tiers.forEach { tier ->
            response.config.colors.entries.find { it.key == tier.tierBits }?.let { colorMap ->
                val url = tier.template.apply {
                    replaceFirst("PREFIX", tier.prefix)
                    replaceFirst("TIER", colorMap.key.toString())
                    background?.let { replaceFirst("BACKGROUND", it) }
                    format?.let {
                        replaceFirst("ANIMATION", it.key)
                        replaceFirst("EXTENSION", it.value)
                    }
                }
                data.add(CheerEmote(
                    name = tier.prefix,
                    url1x = url.apply { (response.config.scales.find { it.startsWith("1") } ?: response.config.scales.lastOrNull())?.let { replaceFirst("SCALE", it) } },
                    url2x = response.config.scales.find { it.startsWith("2") }?.let { url.replaceFirst("SCALE", it) },
                    url3x = response.config.scales.find { it.startsWith("3") }?.let { url.replaceFirst("SCALE", it) },
                    url4x = response.config.scales.find { it.startsWith("4") }?.let { url.replaceFirst("SCALE", it) },
                    type = if (format?.key == "animated") "gif" else null,
                    isAnimated = format?.key == "animated",
                    minBits = colorMap.key,
                    color = colorMap.value
                ))
            }
        }
        return data
    }

    suspend fun loadVideoMessages(headers: Map<String, String>, videoId: String?, offset: Int? = null, cursor: String? = null): VideoMessagesDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "b70a3591ff0f4e0313d126c6a1502d79a1c02baebb288227c582044aa76adf6a")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "VideoCommentsByOffsetOrCursor")
            add("variables", JsonObject().apply {
                addProperty("cursor", cursor)
                addProperty("contentOffsetSeconds", offset)
                addProperty("videoID", videoId)
            })
        }
        return graphQL.getVideoMessages(headers, json)
    }

    suspend fun loadVideoGames(headers: Map<String, String>, videoId: String?): VideoGamesDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "8d2793384aac3773beab5e59bd5d6f585aedb923d292800119e03d40cd0f9b41")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "VideoPlayer_ChapterSelectButtonVideo")
            add("variables", JsonObject().apply {
                addProperty("videoID", videoId)
            })
        }
        return graphQL.getVideoGames(headers, json)
    }

    suspend fun loadChannelViewerList(headers: Map<String, String>, channelLogin: String?): ChannelViewerListDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "2e71a3399875770c1e5d81a9774d9803129c44cf8f6bad64973aa0d239a88caf")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "CommunityTab")
            add("variables", JsonObject().apply {
                addProperty("login", channelLogin)
            })
        }
        return graphQL.getChannelViewerList(headers, json)
    }

    suspend fun loadViewerCount(headers: Map<String, String>, channelLogin: String?): ViewersDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "00b11c9c428f79ae228f30080a06ffd8226a1f068d6f52fbc057cbde66e994c2")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "UseViewCount")
            add("variables", JsonObject().apply {
                addProperty("channelLogin", channelLogin)
            })
        }
        return graphQL.getViewerCount(headers, json)
    }

    suspend fun loadEmoteCard(headers: Map<String, String>, emoteId: String?): EmoteCardResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "556230dd63957761355ba54232c43f4781f31ed6686fc827053b9aa7b199848f")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "EmoteCard")
            add("variables", JsonObject().apply {
                addProperty("emoteID", emoteId)
                addProperty("octaneEnabled", true)
                addProperty("artistEnabled", true)
            })
        }
        return graphQL.getEmoteCard(headers, json)
    }

    suspend fun loadChannelPanel(headers: Map<String, String>, channelId: String?): Response<ResponseBody> {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "236b0ec07489e5172ee1327d114172f27aceca206a1a8053106d60926a7f622e")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "ChannelPanels")
            add("variables", JsonObject().apply {
                addProperty("id", channelId)
            })
        }
        return graphQL.getChannelPanel(headers, json)
    }

    suspend fun loadFollowedStreams(headers: Map<String, String>, limit: Int?, cursor: String?): FollowedStreamsDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "556e6457d0347d3f831b0560fabc126860ce47f14bd7ed13432bc43548e2b459")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "FollowingLive_CurrentUser")
            add("variables", JsonObject().apply {
                addProperty("cursor", cursor)
                addProperty("limit", limit)
            })
        }
        return graphQL.getFollowedStreams(headers, json)
    }

    suspend fun loadFollowedVideos(headers: Map<String, String>, limit: Int?, cursor: String?): FollowedVideosDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "a8e02d4cc25511e9997842c80333e15ba0bb9e11b4199e31c5207317faff9618")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "FollowedVideos_CurrentUser")
            add("variables", JsonObject().apply {
                addProperty("cursor", cursor)
                addProperty("limit", limit)
            })
        }
        return graphQL.getFollowedVideos(headers, json)
    }

    suspend fun loadFollowedChannels(headers: Map<String, String>, limit: Int?, cursor: String?): FollowedChannelsDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "eecf815273d3d949e5cf0085cc5084cd8a1b5b7b6f7990cf43cb0beadf546907")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "ChannelFollows")
            add("variables", JsonObject().apply {
                addProperty("cursor", cursor)
                addProperty("limit", limit)
                addProperty("order", "DESC")
            })
        }
        return graphQL.getFollowedChannels(headers, json)
    }

    suspend fun loadFollowedGames(headers: Map<String, String>, limit: Int?): FollowedGamesDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "f3c5d45175d623ed3d5ff4ca4c7de379ea6a1a4852236087dc1b81b7dbfd3114")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "FollowingGames_CurrentUser")
            add("variables", JsonObject().apply {
                addProperty("limit", limit)
                addProperty("type", "ALL")
            })
        }
        return graphQL.getFollowedGames(headers, json)
    }

    suspend fun loadFollowUser(headers: Map<String, String>, userId: String?): FollowDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "800e7346bdf7e5278a3c1d3f21b2b56e2639928f86815677a7126b093b2fdd08")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "FollowButton_FollowUser")
            add("variables", JsonObject().apply {
                add("input", JsonObject().apply {
                    addProperty("disableNotifications", false)
                    addProperty("targetID", userId)
                })
            })
        }
        return graphQL.getFollowUser(headers, json)
    }

    suspend fun loadUnfollowUser(headers: Map<String, String>, userId: String?): FollowDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "f7dae976ebf41c755ae2d758546bfd176b4eeb856656098bb40e0a672ca0d880")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "FollowButton_UnfollowUser")
            add("variables", JsonObject().apply {
                add("input", JsonObject().apply {
                    addProperty("targetID", userId)
                })
            })
        }
        return graphQL.getUnfollowUser(headers, json)
    }

    suspend fun loadFollowGame(headers: Map<String, String>, gameId: String?): FollowDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "b846b65ba4bc9a3561dbe2d069d95deed9b9e031bcfda2482d1bedd84a1c2eb3")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "FollowGameButton_FollowGame")
            add("variables", JsonObject().apply {
                add("input", JsonObject().apply {
                    addProperty("gameID", gameId)
                })
            })
        }
        return graphQL.getFollowGame(headers, json)
    }

    suspend fun loadUnfollowGame(headers: Map<String, String>, gameId: String?): FollowDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "811e02e396ebba0664f21ff002f2eff3c6f57e8af9aedb4f4dfa77cefd0db43d")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "FollowGameButton_UnfollowGame")
            add("variables", JsonObject().apply {
                add("input", JsonObject().apply {
                    addProperty("gameID", gameId)
                })
            })
        }
        return graphQL.getUnfollowGame(headers, json)
    }

    suspend fun loadFollowingUser(headers: Map<String, String>, userLogin: String?): FollowingUserDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "834a75e1c06cffada00f0900664a5033e392f6fb655fae8d2e25b21b340545a9")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "ChannelSupportButtons")
            add("variables", JsonObject().apply {
                addProperty("channelLogin", userLogin)
            })
        }
        return graphQL.getFollowingUser(headers, json)
    }

    suspend fun loadFollowingGame(headers: Map<String, String>, gameName: String?): FollowingGameDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "cfeda60899b6b867b2d7f30c8556778c4a9cc8268bd1aadd9f88134a0f642a02")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "FollowGameButton_Game")
            add("variables", JsonObject().apply {
                addProperty("name", gameName)
            })
        }
        return graphQL.getFollowingGame(headers, json)
    }

    suspend fun loadChannelPointsContext(headers: Map<String, String>, channelLogin: String?): ChannelPointsContextDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "1530a003a7d374b0380b79db0be0534f30ff46e61cffa2bc0e2468a909fbc024")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "ChannelPointsContext")
            add("variables", JsonObject().apply {
                addProperty("channelLogin", channelLogin)
            })
        }
        return graphQL.getChannelPointsContext(headers, json)
    }

    suspend fun loadClaimPoints(headers: Map<String, String>, channelId: String?, claimId: String?): Response<JsonElement> {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "46aaeebe02c99afdf4fc97c7c0cba964124bf6b0af229395f1f6d1feed05b3d0")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "ClaimCommunityPoints")
            add("variables", JsonObject().apply {
                add("input", JsonObject().apply {
                    addProperty("channelID", channelId)
                    addProperty("claimID", claimId)
                })
            })
        }
        return graphQL.getClaimPoints(headers, json)
    }

    suspend fun loadJoinRaid(headers: Map<String, String>, raidId: String?): Response<JsonElement> {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "c6a332a86d1087fbbb1a8623aa01bd1313d2386e7c63be60fdb2d1901f01a4ae")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "JoinRaid")
            add("variables", JsonObject().apply {
                add("input", JsonObject().apply {
                    addProperty("raidID", raidId)
                })
            })
        }
        return graphQL.getJoinRaid(headers, json)
    }

    suspend fun loadUserEmotes(headers: Map<String, String>, channelId: String?): UserEmotesDataResponse {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "b9ce64d02e26c6fe9adbfb3991284224498b295542f9c5a51eacd3610e659cfb")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "AvailableEmotesForChannel")
            add("variables", JsonObject().apply {
                addProperty("channelID", channelId)
                addProperty("withOwner", true)
            })
        }
        return graphQL.getUserEmotes(headers, json)
    }

    suspend fun sendAnnouncement(headers: Map<String, String>, channelId: String?, message: String?, color: String?): Response<JsonElement> {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "f9e37b572ceaca1475d8d50805ae64d6eb388faf758556b2719f44d64e5ba791")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "SendAnnouncementMessage")
            add("variables", JsonObject().apply {
                add("input", JsonObject().apply {
                    addProperty("channelID", channelId)
                    addProperty("message", message)
                    addProperty("color", color)
                })
            })
        }
        return graphQL.sendAnnouncement(headers, json)
    }

    suspend fun banUser(headers: Map<String, String>, channelId: String?, targetLogin: String?, duration: String?, reason: String?): Response<JsonElement> {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "d7be2d2e1e22813c1c2f3d9d5bf7e425d815aeb09e14001a5f2c140b93f6fb67")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "Chat_BanUserFromChatRoom")
            add("variables", JsonObject().apply {
                add("input", JsonObject().apply {
                    addProperty("channelID", channelId)
                    addProperty("bannedUserLogin", targetLogin)
                    addProperty("expiresIn", duration)
                    addProperty("reason", reason)
                })
            })
        }
        return graphQL.banUser(headers, json)
    }

    suspend fun unbanUser(headers: Map<String, String>, channelId: String?, targetLogin: String?): Response<JsonElement> {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "bee22da7ae03569eb9ae41ef857fd1bb75507d4984d764a81fe8775accac71bd")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "Chat_UnbanUserFromChatRoom")
            add("variables", JsonObject().apply {
                add("input", JsonObject().apply {
                    addProperty("channelID", channelId)
                    addProperty("bannedUserLogin", targetLogin)
                })
            })
        }
        return graphQL.unbanUser(headers, json)
    }

    suspend fun updateChatColor(headers: Map<String, String>, color: String?): Response<JsonElement> {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "0371259a74a3db4ff4bf4473d998d8ae8e4f135b20403323691d434f2790e081")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "Chat_UpdateChatColor")
            add("variables", JsonObject().apply {
                add("input", JsonObject().apply {
                    addProperty("color", color)
                })
            })
        }
        return graphQL.updateChatColor(headers, json)
    }

    suspend fun createStreamMarker(headers: Map<String, String>, channelLogin: String?): Response<JsonElement> {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "c65f8b33e3bcccf2b16057e8f445311d213ecf8729f842ccdc71908231fa9a78")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "VideoMarkersChatCommand")
            add("variables", JsonObject().apply {
                addProperty("channelLogin", channelLogin)
            })
        }
        return graphQL.createStreamMarker(headers, json)
    }

    suspend fun getModerators(headers: Map<String, String>, channelLogin: String?): Response<ModeratorsDataResponse> {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "cb912a7e0789e0f8a4c85c25041a08324475831024d03d624172b59498caf085")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "Mods")
            add("variables", JsonObject().apply {
                addProperty("login", channelLogin)
            })
        }
        return graphQL.getModerators(headers, json)
    }

    suspend fun addModerator(headers: Map<String, String>, channelId: String?, targetLogin: String?): Response<JsonElement> {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "46da4ec4229593fe4b1bce911c75625c299638e228262ff621f80d5067695a8a")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "ModUser")
            add("variables", JsonObject().apply {
                add("input", JsonObject().apply {
                    addProperty("channelID", channelId)
                    addProperty("targetLogin", targetLogin)
                })
            })
        }
        return graphQL.addModerator(headers, json)
    }

    suspend fun removeModerator(headers: Map<String, String>, channelId: String?, targetLogin: String?): Response<JsonElement> {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "1ed42ccb3bc3a6e79f51e954a2df233827f94491fbbb9bd05b22b1aaaf219b8b")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "UnmodUser")
            add("variables", JsonObject().apply {
                add("input", JsonObject().apply {
                    addProperty("channelID", channelId)
                    addProperty("targetLogin", targetLogin)
                })
            })
        }
        return graphQL.removeModerator(headers, json)
    }

    suspend fun startRaid(headers: Map<String, String>, channelId: String?, targetId: String?): Response<JsonElement> {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "f4fc7ac482599d81dfb6aa37100923c8c9edeea9ca2be854102a6339197f840a")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "chatCreateRaid")
            add("variables", JsonObject().apply {
                add("input", JsonObject().apply {
                    addProperty("sourceID", channelId)
                    addProperty("targetID", targetId)
                })
            })
        }
        return graphQL.startRaid(headers, json)
    }

    suspend fun cancelRaid(headers: Map<String, String>, channelId: String?): Response<JsonElement> {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "c388b89e7616a11a8a07b75e3d7bbe7278d37c3c46f43d7c8d4d0262edc00cd9")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "chatCancelRaid")
            add("variables", JsonObject().apply {
                add("input", JsonObject().apply {
                    addProperty("sourceID", channelId)
                })
            })
        }
        return graphQL.cancelRaid(headers, json)
    }

    suspend fun getVips(headers: Map<String, String>, channelLogin: String?): Response<VipsDataResponse> {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "612a574d07afe5db2f9e878e290225224a0b955e65b5d1235dcd4b68ff668218")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "VIPs")
            add("variables", JsonObject().apply {
                addProperty("login", channelLogin)
            })
        }
        return graphQL.getVips(headers, json)
    }

    suspend fun addVip(headers: Map<String, String>, channelId: String?, targetLogin: String?): Response<JsonElement> {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "e8c397f1ed8b1fdbaa201eedac92dd189ecfb2d828985ec159d4ae77f9920170")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "VIPUser")
            add("variables", JsonObject().apply {
                add("input", JsonObject().apply {
                    addProperty("channelID", channelId)
                    addProperty("granteeLogin", targetLogin)
                })
            })
        }
        return graphQL.addVip(headers, json)
    }

    suspend fun removeVip(headers: Map<String, String>, channelId: String?, targetLogin: String?): Response<JsonElement> {
        val json = JsonObject().apply {
            add("extensions", JsonObject().apply {
                add("persistedQuery", JsonObject().apply {
                    addProperty("sha256Hash", "2ce4fcdf6667d013aa1f820010e699d1d4abdda55e26539ecf4efba8aff2d661")
                    addProperty("version", 1)
                })
            })
            addProperty("operationName", "UnVIPUser")
            add("variables", JsonObject().apply {
                add("input", JsonObject().apply {
                    addProperty("channelID", channelId)
                    addProperty("revokeeLogin", targetLogin)
                })
            })
        }
        return graphQL.removeVip(headers, json)
    }
}