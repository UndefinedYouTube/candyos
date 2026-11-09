/*
 * Copyright (C) 2026 The KitKat UI Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3;

/**
 * KitKat UI Project: hand-rolled stand-in for the stock Launcher3 aconfig
 * flag library (com_android_launcher3_flags_lib).
 *
 * CandyLauncher replaces stock Launcher3 wholesale (the prebuilt APK carries
 * no aconfig flag sources), but frameworks/base still links the flag library
 * from three spots:
 *
 *  - libs/systemui/iconloaderlib (BaseIconCache, MonoThemedBitmap,
 *    MonoIconThemeController)
 *  - libs/WindowManager/Shell (ShellTaskOrganizer,
 *    DesktopTasksController - incl. the BooleanSupplier reference and the
 *    FLAG_ENABLE_ALT_TAB_KQS_FLATENNING adb-override name)
 *  - services/core (linked wholesale; no direct flag reads today)
 *
 * Every flag the in-tree consumers read is declared here as a static
 * method/field with the exact stock signature. All values are pinned to
 * the pre-flag (legacy Launcher2/KitKat-era) behavior, which is what a
 * launcher without themed icons or in-window overview expects:
 *
 *  - enableLauncherIconShapes / forceMonochromeAppIcons /
 *    forceMonochromeAppIconsAdaptColors / restoreArchivedAppIconsFromDb:
 *    themed-icon and archiving features off - icons render as the
 *    unmasked legacy bitmaps (the 4.4 behavior).
 *  - enableLauncherOverviewInWindow / enableFallbackOverviewInWindow /
 *    enableAltTabKqsFlatenning: no Launcher3 quickstep overview - recents
 *    stays SystemUI-side (KitKatRecentsActivity, see the frameworks-base
 *    patch series) and alt-tab keeps the legacy flattened behavior.
 *
 * If a future lineage rebase adds a new in-tree consumer, add the flag
 * here with the same rule: legacy default, stock signature.
 */
public final class Flags {

    private Flags() {}

    // --- iconloaderlib (themed icon pipeline) ---

    /** Legacy default: off - icons render as legacy bitmaps (4.4 behavior). */
    public static boolean enableLauncherIconShapes() {
        return false;
    }

    /** Legacy default: off - no forced monochrome (themed) app icons. */
    public static boolean forceMonochromeAppIcons() {
        return false;
    }

    /** Legacy default: off - monochrome icons do not adapt colors. */
    public static boolean forceMonochromeAppIconsAdaptColors() {
        return false;
    }

    /** Legacy default: off - archived app icons are not restored from db. */
    public static boolean restoreArchivedAppIconsFromDb() {
        return false;
    }

    // --- WindowManager/Shell (overview in window + desktop mode) ---

    /**
     * Legacy default: off - no Launcher3 overview running inside a window.
     * Recents is served by SystemUI's KitKat panel instead.
     */
    public static boolean enableLauncherOverviewInWindow() {
        return false;
    }

    /**
     * Legacy default: off - no fallback overview window either (paired with
     * enableLauncherOverviewInWindow in ShellTaskOrganizer).
     */
    public static boolean enableFallbackOverviewInWindow() {
        return false;
    }

    /**
     * Legacy default: off - alt-tab keeps the legacy (unflattened-by-KQS)
     * behavior. Referenced as a BooleanSupplier by DesktopTasksController.
     */
    public static boolean enableAltTabKqsFlatenning() {
        return false;
    }

    /**
     * The aconfig flag name, used by DesktopTasksController for the
     * "adb shell cmd desktop_mode ... [flag name]" developer-option
     * override of the flag above.
     */
    public static final String FLAG_ENABLE_ALT_TAB_KQS_FLATENNING =
            "com.android.launcher3.enable_alt_tab_kqs_flatenning";
}