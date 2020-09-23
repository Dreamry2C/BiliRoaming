package me.iacn.biliroaming.hook

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import me.iacn.biliroaming.BiliBiliPackage.Companion.instance
import me.iacn.biliroaming.XposedInit
import me.iacn.biliroaming.utils.Log
import me.iacn.biliroaming.utils.callMethodAs
import me.iacn.biliroaming.utils.hookAfterMethod
import java.io.File

class SplashHook(classLoader: ClassLoader) : BaseHook(classLoader) {
    override fun startHook() {
        if (!XposedInit.sPrefs.getBoolean("custom_splash", false) && !XposedInit.sPrefs.getBoolean("custom_splash_logo", false)) return
        Log.d("startHook: Splash")

        instance.brandSplashClass?.hookAfterMethod("onViewCreated", View::class.java, Bundle::class.java) { param ->
            val activity = param.thisObject.callMethodAs<Activity>("getActivity")
            val view = param.args[0] as View
            if (XposedInit.sPrefs.getBoolean("custom_splash", false)) {
                val brandId = activity.resources.getIdentifier("brand_splash", "id", activity.packageName)
                val brandSplash = view.findViewById<ImageView>(brandId)
                val splashImage = File(XposedInit.currentContext.filesDir, SPLASH_IMAGE)
                if (splashImage.exists())
                    brandSplash.setImageURI(Uri.fromFile(splashImage))
                else
                    brandSplash.alpha = .0f
            }
            if (XposedInit.sPrefs.getBoolean("custom_splash_logo", false)) {
                val logoId = activity.resources.getIdentifier("brand_logo", "id", activity.packageName)
                val brandLogo = view.findViewById<ImageView>(logoId)
                val logoImage = File(XposedInit.currentContext.filesDir, LOGO_IMAGE)
                if (logoImage.exists())
                    brandLogo.setImageURI(Uri.fromFile(logoImage))
                else
                    brandLogo.alpha = .0f
            }
        }
    }

    companion object {
        const val SPLASH_IMAGE = "biliroaming_splash"
        const val LOGO_IMAGE = "biliroaming_logo"
    }

}