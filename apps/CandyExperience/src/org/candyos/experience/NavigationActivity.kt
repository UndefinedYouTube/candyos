package org.candyos.experience

import android.app.Activity
import android.os.Bundle
import android.widget.LinearLayout

/**
 * "Navigation" — the Experience menu screen where the system navigation
 * style is switched at any time, mirroring what the OTE wizard's nav step
 * decided on first boot (and defaulting to the same 3-button mode the
 * vendor/candyos overlay pins, P39.1). Picks apply immediately: they
 * enable the matching com.android.internal.systemui.navbar.* mode
 * overlay, exactly what the Settings "System navigation" screen does.
 */
class NavigationActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nav_activity)

        val rows = requireViewById<LinearLayout>(R.id.nav_rows)
        NavConfig.addRows(this, rows, NavConfig.current(this)) { mode ->
            NavConfig.apply(this, mode)
        }
    }
}