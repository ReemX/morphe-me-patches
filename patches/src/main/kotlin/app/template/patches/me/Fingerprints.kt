package app.template.patches.me

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.methodCall
import com.android.tools.smali.dexlib2.AccessFlags

/**
 * com.nfo.me.android.presentation.ui.ads.ViewAdRecycler#setupAd(LovinAdTags)
 * Central entry that loads/binds every inline banner + native ad across the app.
 * Class name is not obfuscated, so match by class + method + parameter.
 */
object ViewAdRecyclerSetupAdFingerprint : Fingerprint(
    definingClass = "Lcom/nfo/me/android/presentation/ui/ads/ViewAdRecycler;",
    name = "setupAd",
    returnType = "V",
    parameters = listOf("Lcom/nfo/me/core_utils/ads/main/LovinAdTags;"),
)

/**
 * com.nfo.me.android.presentation.ui.ads.ViewAdRecyclerForContacts#setupAd(LovinAdTags)
 * Same as above but for the contacts list.
 */
object ViewAdRecyclerForContactsSetupAdFingerprint : Fingerprint(
    definingClass = "Lcom/nfo/me/android/presentation/ui/ads/ViewAdRecyclerForContacts;",
    name = "setupAd",
    returnType = "V",
    parameters = listOf("Lcom/nfo/me/core_utils/ads/main/LovinAdTags;"),
)

/**
 * com.nfo.me.android.presentation.views.ads.ViewAdRecyclerPopUp#b(LovinAdTags, int)
 * Ad host used by the caller pop-up / menu.
 */
object ViewAdRecyclerPopUpFingerprint : Fingerprint(
    definingClass = "Lcom/nfo/me/android/presentation/views/ads/ViewAdRecyclerPopUp;",
    returnType = "V",
    // Method name ("b") is obfuscated; class + (LovinAdTags, int) parameters are unique.
    parameters = listOf("Lcom/nfo/me/core_utils/ads/main/LovinAdTags;", "I"),
)

/**
 * Obfuscated interstitial "show if ready" facade (was ur.h#a(ur.h, Function1)).
 * Class + method names are obfuscated and change between versions, so match by the
 * two AppLovin MaxInterstitialAd calls it contains (isReady + showAd).
 * On no ad it already invokes the Function1 continuation with FALSE and returns, so
 * short-circuiting to that path is navigation-safe.
 */
object InterstitialShowFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf("L", "Lkotlin/jvm/functions/Function1;"),
    filters = listOf(
        methodCall(
            definingClass = "Lcom/applovin/mediation/ads/MaxInterstitialAd;",
            name = "isReady",
        ),
        methodCall(
            definingClass = "Lcom/applovin/mediation/ads/MaxInterstitialAd;",
            name = "showAd",
        ),
    ),
)
