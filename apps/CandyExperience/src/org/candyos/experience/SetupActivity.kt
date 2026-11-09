package org.candyos.experience

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ViewFlipper

class SetupActivity : Activity() {

    private lateinit var flipper: ViewFlipper
    private lateinit var back: Button
    private lateinit var next: Button
    private lateinit var networkIcon: ImageView
    private lateinit var networkStatus: TextView
    private lateinit var dots: LinearLayout
    private var appRows: Map<String, android.widget.CheckBox> = emptyMap()
    private var playRows: Map<String, android.widget.CheckBox> = emptyMap()
    private var navMode = NavConfig.MODE_3BUTTON

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The wizard runs exactly once per data image, tracked by a private
        // flag rather than DEVICE_PROVISIONED: the emulator products ship
        // com.android.sdksetup (device/generic/goldfish), whose HOME
        // activity auto-provisions the device at boot before this wizard
        // can run - on a fresh boot it has already set DEVICE_PROVISIONED,
        // so gating on it would bounce straight to the menu and the OTE
        // would never show. The flag is written in finishSetup; if this
        // activity is reached with the flag set, it is a stale HOME
        // resolution (or a manual component re-enable): the wizard is
        // done, the menu is the app.
        if (getSharedPreferences(PREFS_SETUP, MODE_PRIVATE)
                .getBoolean(KEY_SETUP_COMPLETE, false)) {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
            return
        }

        // The wizard runs exactly once per device: pin the Google-services
        // preloads off until the user opts in on the play step (they boot
        // enabled straight out of the image).
        PlayConfig.ensureFreshDefaults(this)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        setContentView(R.layout.setup_activity)

        flipper = requireViewById(R.id.steps)
        back = requireViewById(R.id.back)
        next = requireViewById(R.id.next)
        networkIcon = requireViewById(R.id.network_icon)
        networkStatus = requireViewById(R.id.network_status)
        dots = requireViewById(R.id.dots)

        requireViewById<Button>(R.id.pick_wifi).setOnClickListener {
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
        }

        back.setOnClickListener {
            if (flipper.displayedChild > 0) {
                flipper.showPrevious()
                updateChrome()
            } else {
                moveTaskToBack(true)
            }
        }
        next.setOnClickListener {
            if (flipper.displayedChild < flipper.childCount - 1) {
                flipper.showNext()
                updateChrome()
            } else {
                finishSetup()
            }
        }

        buildDots()
        appRows = AppsConfig.addRows(this, requireViewById(R.id.apps_rows))
        playRows = PlayConfig.addRows(this, requireViewById(R.id.play_rows))
        navMode = NavConfig.current(this)
        buildNavRows()
        updateChrome()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

    override fun onResume() {
        super.onResume()
        appRows = AppsConfig.addRows(this, requireViewById(R.id.apps_rows))
        if (::flipper.isInitialized) {
            playRows = PlayConfig.addRows(this, requireViewById(R.id.play_rows))
            // The nav radio rows keep their in-progress selection across
            // pauses (Settings could have changed the mode meanwhile, but
            // the wizard's pick wins at finish).
            updateNetworkStatus()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        back.performClick()
    }

    private fun buildDots() {
        dots.removeAllViews()
        val dp = resources.displayMetrics.density
        for (i in 0 until flipper.childCount) {
            val dot = ImageView(this).apply {
                setImageResource(R.drawable.ic_dot)
                alpha = DOT_ALPHA_INACTIVE
                val side = (10 * dp).toInt()
                layoutParams = LinearLayout.LayoutParams(side, side).apply {
                    setMargins((3 * dp).toInt(), 0, (3 * dp).toInt(), 0)
                }
            }
            dots.addView(dot)
        }
    }

    private fun updateChrome() {
        val step = flipper.displayedChild
        back.visibility = if (step == 0) View.INVISIBLE else View.VISIBLE
        if (step == flipper.childCount - 1) {
            next.setText(R.string.start_label)
        } else {
            next.setText(R.string.next_label)
        }
        for (i in 0 until dots.childCount) {
            dots.getChildAt(i).alpha =
                if (i == step) DOT_ALPHA_ACTIVE else DOT_ALPHA_INACTIVE
        }
        when (step) {
            STEP_NETWORK -> updateNetworkStatus()
            STEP_APPS -> appRows = AppsConfig.addRows(this, requireViewById(R.id.apps_rows))
            STEP_NAV -> buildNavRows()
            STEP_PLAY -> playRows = PlayConfig.addRows(this, requireViewById(R.id.play_rows))
        }
    }

    private fun buildNavRows() {
        NavConfig.addRows(this, requireViewById(R.id.nav_rows), navMode) { mode ->
            navMode = mode
        }
    }

    private fun updateNetworkStatus() {
        val cm = getSystemService(ConnectivityManager::class.java)
        val network = cm?.activeNetwork
        val info = network?.let { cm.getNetworkInfo(it) }
        if (info != null && info.isConnected) {
            val name = info.extraInfo?.takeIf { it.isNotBlank() } ?: info.typeName
            networkStatus.text = getString(R.string.setup_network_connected, name)
            networkIcon.setImageResource(R.drawable.ic_wifi_on)
        } else {
            networkStatus.setText(R.string.setup_network_not_connected)
            networkIcon.setImageResource(R.drawable.ic_wifi_off)
        }
    }

    private fun finishSetup() {
        AppsConfig.applySelections(this, appRows)
        PlayConfig.applySelections(this, playRows)
        NavConfig.apply(this, navMode)
        Settings.Global.putInt(contentResolver, Settings.Global.DEVICE_PROVISIONED, 1)
        Settings.Secure.putInt(contentResolver, Settings.Secure.USER_SETUP_COMPLETE, 1)

        // Pin the once-per-data-image wizard flag synchronously (commit, not
        // apply): nothing else may observe the wizard as unfinished after
        // this point, even if the process is killed right after.
        getSharedPreferences(PREFS_SETUP, MODE_PRIVATE).edit()
            .putBoolean(KEY_SETUP_COMPLETE, true)
            .commit()

        // Retire the wizard: with DEVICE_PROVISIONED set this component must
        // stop answering HOME, or the system resolves Home straight back into
        // the finished wizard (the reported reopen bug). Disabling our own
        // setup activity is what the stock SUWs do post-provisioning too; the
        // MenuActivity (the drawer entry) carries the app from here on.
        packageManager.setComponentEnabledSetting(
            ComponentName(this, SetupActivity::class.java),
            android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            android.content.pm.PackageManager.DONT_KILL_APP)

        // Land on the Launcher - the menu is only reached when the user
        // explicitly opens CandyOS Experience from the drawer afterwards.
        finishAndRemoveTask()
    }

    companion object {
        private const val PREFS_SETUP = "candy_setup"
        private const val KEY_SETUP_COMPLETE = "setup_complete"
        private const val STEP_NETWORK = 1
        private const val STEP_APPS = 2
        private const val STEP_NAV = 3
        private const val STEP_PLAY = 4
        private const val DOT_ALPHA_ACTIVE = 1f
        private const val DOT_ALPHA_INACTIVE = 0.3f
    }
}