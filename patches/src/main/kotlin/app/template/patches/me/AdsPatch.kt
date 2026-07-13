package app.template.patches.me

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.template.patches.shared.Constants.COMPATIBILITY_ME

/**
 * Collapses an ad host View and returns before any ad is requested.
 * `p0` is the View (this); 0x8 == View.GONE.
 */
private const val HIDE_AND_RETURN =
    """
        const/16 v0, 0x8
        invoke-virtual {p0, v0}, Landroid/view/View;->setVisibility(I)V
        return-void
    """

@Suppress("unused")
val hideAdsPatch = bytecodePatch(
    name = "Hide ads",
    description = "Removes all inline banner and native ads (call log, contacts, dialer, " +
        "caller pop-up, profiles, menu, etc.) by neutering the ad host views.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_ME)

    execute {
        ViewAdRecyclerSetupAdFingerprint.method.addInstructions(0, HIDE_AND_RETURN)
        ViewAdRecyclerForContactsSetupAdFingerprint.method.addInstructions(0, HIDE_AND_RETURN)
        ViewAdRecyclerPopUpFingerprint.method.addInstructions(0, HIDE_AND_RETURN)
    }
}

@Suppress("unused")
val disableInterstitialsPatch = bytecodePatch(
    name = "Disable interstitial ads",
    description = "Skips full-screen interstitial ads (including the one on launch). " +
        "Calls the app's own \"no ad, continue\" callback so navigation is unaffected.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_ME)

    execute {
        // p1 is the Function1 continuation; invoking it with Boolean.FALSE reproduces the
        // app's existing "interstitial not ready -> proceed" path, then returns before showAd().
        InterstitialShowFingerprint.method.addInstructions(
            0,
            """
                sget-object p0, Ljava/lang/Boolean;->FALSE:Ljava/lang/Boolean;
                invoke-interface {p1, p0}, Lkotlin/jvm/functions/Function1;->invoke(Ljava/lang/Object;)Ljava/lang/Object;
                return-void
            """,
        )
    }
}
