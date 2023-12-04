package com.github.andreyasadchy.xtra

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.transition.Transition
import coil.util.DebugLogger
import com.github.andreyasadchy.xtra.di.AppInjector
import com.github.andreyasadchy.xtra.util.AppLifecycleObserver
import com.github.andreyasadchy.xtra.util.LifecycleListener
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class XtraApp : Application(), Configuration.Provider, ImageLoaderFactory {

    companion object {
        lateinit var INSTANCE: Application
    }

    private val appLifecycleObserver = AppLifecycleObserver()

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        AppInjector.init(this)

        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    fun addLifecycleListener(listener: LifecycleListener) {
        appLifecycleObserver.addListener(listener)
    }

    fun removeLifecycleListener(listener: LifecycleListener) {
        appLifecycleObserver.removeListener(listener)
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun newImageLoader(): ImageLoader {
        val builder = ImageLoader.Builder(this).apply {
            if (BuildConfig.DEBUG) {
                logger(DebugLogger())
            }
            transitionFactory(Transition.Factory.NONE)
            components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory(enforceMinimumFrameDelay = true))
                } else {
                    add(GifDecoder.Factory(enforceMinimumFrameDelay = true))
                }
            }
        }
        return builder.build()
    }
}
