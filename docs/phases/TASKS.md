# CandyOS — TASKS

REVISED 2026-11-09 (user direction): the project base moved to the
latest LineageOS (lineage-24.0) on 2026-11-06 and STAYS there — but the
original vision (everything the project shipped on the old base) must be
restored, verified surface by surface on the new base. This file is now
the restoration tracker. Phase 39 is the umbrella; the per-phase gates
below all ride its first green build.

Prior revision (2026-10-28): this file is a working tracker for OPEN
WORK ONLY. It previously carried the full per-phase historical record of
P0–P27 (~2400 lines) relocated unchanged from the dev forks. Nothing is
lost by that revision or this one:

* this file's own git history (everything up to commit 8fadfe8) is the
  per-item P0–P27 record;
* the forks' tagged history (kitkat/p<n> phase tags on the
  EHowardHill/* read-only mirrors, notably android_frameworks_base
  kitkat-mods) holds the pre-rollback commits;
* docs/architecture.md maps what ships today.

Phase numbers are cumulative and never reused — old numbers stay valid
as references (patch-series subjects, code comments, the CandyOS P<n>
commit convention). Open work starts at P28; the highest used number is
P38, so the restoration is P39.

## Goal

Make LineageOS 24 (the latest LineageOS — user direction 2026-11-06:
the base stays current) look and behave like stock Android 4.4 KitKat
across all system surfaces, with the feature set staying modern,
delivered as patch series + vendored apps + vendor overlays
(docs/architecture.md).

Hard constraint (unchanged): modern apps must keep working. All changes
stay at the visual, resource, and UX-shell layer; nothing alters APIs
or behaviors apps depend on.

## Rules

1. AUTHENTIC, not a theme: port real 4.4 code and assets at the real
   tree path where a donor can run on the modern API; never
   costume-restyle.
2. Never break modern functionality: a surface whose 4.4 donor cannot
   be made modern-functional ships modern on the theme cascade —
   "tacky theme" is the failure mode this rule exists to prevent.
3. Vanilla: defaults are KitKat (Holo, Roboto 2013, Holo blue, 4.4
   sounds). Where 4.4 has no counterpart, keep stock and let the
   cascade dress it. No invented hybrid designs.
4. DeviceDefault is the boundary: Theme.Material apps keep Material;
   Holo ships as Theme.DeviceDefault == Holo. Nothing force-breaks
   third parties.
5. Accept and document: where a port or pixel-match is not achievable,
   record the delta here — never silently approximate.
6. Verification standard: against the DONOR SOURCE TREE (~/kitkat-src)
   plus the canonical Pixel 3a-sized AVD (440dpi class) and the
   audio-enabled AVD for sound. NO 4.4 reference VM (P19: 4.4 cannot
   build on this host). On-device standard: adb screencap pixel
   battery; human eyeball for feel and sub-second effects. AVD-only
   until real hardware exists.
7. Markers: [ ] open, [X] done, [?] blocked on a question, [/] partial.
8. LOS-24 migration corollary: "it rebased clean" is NOT "it works".
   Nothing carried across the base move counts as shipped until its
   Phase 39 (or riding) gate passes on the new base. Compile fixes
   land one error wall at a time (0020–0022, latinime 0005 were each
   found by the previous build attempt) — assume more walls.

## Donor sources (~/kitkat-src — local reference only, never committed)

* frameworks-base (android-4.4.4_r1) — core/res themes + fonts, the
  4.4 SystemUI in its original in-tree layout, services. THE donor for
  shade/keyguard work.
* latinime-4.4, settings-4.4, noto-fonts-4.4 (NotoColorEmoji2013).
* Per-app donors: calculator / soundrecorder / music / deskclock /
  calendar-4.4, ex-4.4 (chips + common), colorpicker /
  datetimepicker / timezonepicker / tzsupport / calendar-opt-4.4,
  support-4.4.
* /tmp/opencode/kitkat-donor — sparse old copy (fonts + sounds only);
  ephemeral, prefer ~/kitkat-src.

## State after the LOS-24 migration (2026-11-09 snapshot)

The original vision, as shipped and device-verified on lineage-21.0
(the 2026-10-28 shipped-state list, unchanged in content): framework
Holo rebase, 2013 Roboto, hybrid emoji, 4.4 window animations / dimens
/ sounds, 4.4 wallpaper + easter egg (P1/P8/P17); opaque status bar,
4.4 icons, right-hand clock, ticker (P2/P3/P23); 4.4 navbar glyphs +
press glow (P9/P11), translucent-bar gradient (P35), home scrim
(P37), 2-button fallback fix (P36); KitKat recents (P5); keyguard
colors, 2013 Roboto Thin clock, Holo PIN pad (P6); volume + power menu
restyle (P4/P14); KitKat keyboard forced default (P13); Settings dark
Holo + 4.4 icons + Holo switches (P7/P11.6/P38); notification shade +
QS re-land (P12/P15/P20 lineage, re-landed P28, footer strip P28.6);
runtime permission dialog (P14.4/P28); app ports Calculator /
SoundRecorder / Music / DeskClock / Calendar (P16); Notes + Reminders
(P24); CandyExperience onboarding + About + Testing menu (P25); boot
animation wordmark (P26); microG + Aurora + Organic Maps (P34,
replacing the P27 MindTheGapps era); bundled wallpapers + default
(Phase 32); KitKat launcher icons (P33); signature spoofing (P34).

The migration (2026-11-06 → 11-09):

* Base: lineage-21.0 → lineage-24.0 (scripts/common.sh,
  manifest/candyos.xml; every series' BASE regenerated).
* Every patch series regenerated onto the new upstream ("the
  compatibility commit"); the frameworks-base series is now 22
  patches, latinime 5, settings 5, vendor-lineage 5.
* Upstream dropped Trebuchet and Eleven: CandyLauncher moved to the
  Launcher3 slot (module Launcher3QuickStep) and the 4.4 Music port to
  the Twelve slot (module Twelve) — module names still chosen so
  vendor/lineage product config resolves unchanged.
* Compile fixes, one error wall per build attempt: latinime 0005
  (orphaned OLED theme resources broke aapt2), frameworks-base 0020
  (SystemUI-core Kotlin imports + opaque-mode enum), 0021 (wm.shell
  type drift), 0022 (SystemUI R imports, ThumbnailData getter,
  TaskInfo).

What that means: everything above is REBASED and nothing is VERIFIED
on the new base. There is no green full build, no emulator battery,
and no user hands-on pass on LOS 24 yet. The 2026-10-28 verification
claims (e.g. 28.1's byte-for-byte diff of the re-landed shade) were
made against the LOS-21 tree and are superseded by the rebase.

## Phase 39 — LOS-24 restoration: the original vision on the new base [ ]

User direction (2026-11-09): restore everything the project had before
the base move — the five named surfaces first, then the full sweep —
keeping lineage-24.0. NOTHING in this phase counts as done on
rebase-cleanliness; each item closes only on the 39.0 build.

- [ ] 39.0 First green LOS-24 build gate: `m` on the emulator target
      builds clean (each fix so far exposed the next wall — keep going
      until a full build passes), candyos-verify.sh green across all
      15 series, candyos-emulator.sh boots to the launcher. This is
      the gate for everything below; every riding QA item in this file
      opens here.
- [ ] 39.1 THREE-BUTTON NAVIGATION BY DEFAULT, KitKat style (user
      direction; the one genuinely NEW surface — never pinned even on
      the old base): the stock base boots gesture nav; the KitKat
      navbar only exists in 3-button mode. Pin the default to
      3-button — find the LOS-24 default mechanism (framework core
      config_navBarMode / SettingsProvider default / LineageSettings
      default) and pin it via the vendor/candyos overlay or a new
      frameworks-base patch, as a DEFAULT not a lock: the OTE /
      Experience nav-style switches (P25) must keep switching modes
      (confirm the wizard nav-style step survived the app's current
      source; restore it if the base move lost it). Verify on the
      39.0 build: fresh boot shows back/home/recents in the P9/P11
      4.4 glyphs + press glow, P36 quickstep fallback fix (0016)
      intact, P35 gradient (0017) + P37 home scrim (0018) over the
      CandyLauncher wallpaper, opaque black in apps.
      CODE DONE 2026-11-09 (pending the 39.0 build verification):
      * The LOS-24 mechanism is `config_navBarInteractionMode`
        (framework-res, read by SystemUI NavigationModeController and
        mirrored into Settings.Secure.NAVIGATION_MODE); the mode
        overlays (com.android.internal.systemui.navbar.{three,two}
        button / gestural) are opt-in RROs switched with
        IOverlayManager.setEnabledExclusiveInCategory — what the
        Settings "System navigation" screen uses.
      * Default pin: vendor/candyos overlay gains the hand-maintained
        frameworks/base/core/res/res/values/candyos_defaults.xml with
        config_navBarInteractionMode = 0 (registered last by
        patches/vendor-lineage/0005, so it wins over any base default
        that ships gesture). The wallpaper generator now regenerates
        only its own drawable buckets, so the hand-maintained
        values/ survives every sync (see vendor/candyos/README.md).
      * The wizard nav-style step was indeed LOST by the base move
        (no nav code anywhere in the app) — RESTORED: a NavConfig
        shared by a new OTE wizard step (between Default apps and
        Google services; pick applied at finish, pre-checked with the
        current mode) and a new Experience menu "Navigation" screen
        (instant apply), both switching via the mode overlays.
        Manifest + privapp whitelist gain CHANGE_OVERLAY_PACKAGES
        (signature|privileged; the app is platform-signed).
      PENDING: the 39.0 emulator pass (fresh boot in 3-button with the
      P9/P11 glyphs, wizard/menu round-trips into 2-button/gesture
      and back, 0016/0017/0018 riding intact).
- [ ] 39.2 KitKat status bar (user direction; re-verify): opaque black
      bar, 4.4 icon set, right-hand clock (P2/P3, patch 0004), ticker
      (P23, patch 0012; posts from the Testing menu), carrier label
      header (P24, patch 0013). The 0020 fix mapped the opaque default
      onto OPAQUE_DARK — verify that actually renders black on the
      new SystemUI, and that the right-hand clock survived the
      rebase (modern AOSP moved it left).
- [ ] 39.3 KitKat fonts (user direction; re-verify): 2013 Roboto
      family + kitkat-lock-clock Thin (patch 0003) and the hybrid
      emoji + NotoColorEmoji2013 set. NOTE: apps/assets/fonts/ now
      carries only Regular + Bold (the boot-animation render set);
      the ROM's full 12-TTF family lives in patch 0003 — confirm the
      whole family + fallback config landed through the rebase and
      renders (Settings body text, lock clock, Testing menu samples)
      instead of the new base's default Roboto.
- [ ] 39.4 Boot animation — CandyOS logo with spinning candy (user
      direction; re-verify): patch vendor-lineage 0003 ships the
      generated tar (scripts/candyos-gen-bootanimation.py from
      apps/assets/CandyOSwordswhite.svg, Roboto 2013, 6:1 canvas,
      part0 fade-in, part1 loop + fade-out on boot completion).
      Verify the vendor/lineage Android.mk still computes the 6:1
      IMAGEHEIGHT on the new base, the tar applies, and the AVD boot
      shows: candy disc spinning ~1 rev/s, wordmark static, fade-in at
      start, fade-out into the launcher. Regenerate + re-save the
      series if the base's bootanimation packaging changed. (This is
      the P26 build pass.)
- [ ] 39.5 Default wallpaper from apps/assets/wallpapers (user
      direction; re-verify): default.png is the system default via
      the generated vendor/candyos overlay (all three density
      buckets, registered last by patch 0005; the LOS RRO deletions
      of 31.1 ride in patch 0004 — both must apply on the new
      vendor/lineage). Verify: fresh boot shows it on home + lock,
      the CandyExperience Wallpapers screen renders every image in
      the folder and applies system + lock, and answer 32.4's open
      ENFORCE_RRO question — does the override actually win in the
      built image.
- [ ] 39.6 Full-surface restoration sweep — the "many more". Each
      item rides the 39.0 build; cite-and-close against its phase:
  * [ ] Holo theme cascade (P1, 0001): Theme.DeviceDefault == Holo;
        third-party Material apps stay Material (rule 4 check: launch
        a Play/Aurora app).
  * [ ] 4.4 window animations / dimens / UI sounds (P1/P8, 0002 +
        0003): unlock/lock keystones, effect texts, Charger sounds —
        audio AVD.
  * [ ] Notification shade + QS (P28, 0011 + 0013 + 0014 + 0012):
        48dp donor header, QQS = notifications-only, two-panel flip +
        gear toggle, donor tiles + brightness dialog,
        KitKatPanelFling physics, flat footer strip. The P21
        feel-pass (28.3) now happens on the LOS-24 build — its
        LOS-21-era "byte for byte" verification (28.1) is superseded
        by the compatibility rebase; the 28.2 fixed-in-tree trio
        (QQS max-tiles, drag handle, max-expansion writers) re-checks
        here too.
  * [ ] KitKat recents (P5): KitKatRecentsImpl compiled through the
        0021/0022 symbol fixes — re-verify cards, scrolling, empty
        state; feel pass per the hardware list.
  * [ ] Keyguard (P6, 0006): Holo bouncer, Roboto Thin clock, PIN
        pad. Then Phase 29's open work (ring-hint geometry port,
        29.2 leftovers) — still gated on shade stability, i.e. on
        the 28.3/28.5 pass riding this build.
  * [ ] Volume panel + power menu (P4/P14) and the runtime permission
        dialog (patches/permission, Theme.DeviceDefault.Dialog.Alert).
  * [ ] Keyboard (P13 + latinime 0005): KitKat theme forced default,
        donor label geometry, drawer icon; confirm the OLED-orphan
        cleanup left no aapt2 residue and gesture typing works
        (13.2, touch pass).
  * [ ] Settings (P7/P11.6/P38): dark Holo, 4.4 icons, Holo row
        switches + switch bar (0019), dark AndroidX dialogs + pinned
        TabLayout (38.1–38.3). 38.4's `m Settings` compile check rides
        39.0; then the 30.1 visual walk.
  * [ ] Vendored ports in their NEW slots: Calculator→ExactCalculator,
        SoundRecorder→Recorder, DeskClock, Calendar→Etar, Music→
        Twelve (module rename), CandyLauncher→Launcher3
        (Launcher3QuickStep) — every one builds on the new base and
        runs (module names keep vendor/lineage config resolving).
  * [ ] Launcher icons (P33): all series apply; every drawer entry
        KitKat-styled at all densities on the AVD; presigned
        exceptions unchanged (CandyLauncher, microG/Companion/Aurora,
        Organic Maps).
  * [ ] CandyExperience (P25/P28/P34): fresh-wipe OTE flow, About /
        Menu / Testing menus, Wallpapers screen, Google-services step
        + menu, updater daily pass.
  * [ ] microG + Aurora + Organic Maps (P34): preloads ship disabled
        at first boot, opt-in enables exactly the checked packages,
        spoof check per 34.6 (cert dump shows the Google cert),
        push notification end-to-end.
  * [ ] Notes + Reminders (P24): build, drawer presence, KitKat icons
        (33.4 amendments).
  * [ ] Branding: lineage-sdk strings (system label, version) render
        as CandyOS; easter egg (P17).
- [ ] 39.7 New-base audit (rule 3/5): walk the LOS-24 build for
      surfaces the cascade does not dress — taskbar behavior (the
      0008 series' 3-button-no-taskbar mode), predictive back, new
      system dialogs, anything else the walk finds. Keep stock where
      4.4 has no counterpart; record every accepted delta here.
- [ ] 39.8 Sync idempotence: candyos-init.sh + candyos-sync.sh on a
      clean tree, twice in a row — all 15 series apply clean,
      vendored slots + overlays land, wallpapers + bootanimation
      regenerate, candyos-verify.sh green. (The Phase 31 class of
      bug — live-tree edits a sync silently reverts — must not
      re-enter through the migration.)
- [ ] 39.9 USER ACCEPTANCE GATE — the P21 lesson, restated for the
      migration: Phase 39 does not close without the user's hands-on
      pass on the 39.0 build (the five named surfaces first, then
      the sweep), on the canonical AVD at minimum; hardware when it
      exists. All riding gates (28.3/28.5, 32.5, 33.6, 34.7) flip
      with it.

## Phase 28 — KitKat notification shade + QS re-land [ ]

User direction (2026-10-28): the KitKat theme is the new default system
UI style, so the shade + QS come back into scope. This SUPERSEDES the
P21 rollback and the 2026-10-22 scope amendment.

This is a RE-LAND, not a rewrite. The complete implementation — deep
port (P12), wholesale port (P15), donor rebuild (P20), with the 11.x
geometry fixes — was device-verified before the P21 revert and survives
verbatim in the frameworks/base fork history (kitkat-mods, tags
kitkat/p12..p20, commits before the single P21 revert commit). The
per-item record is the old TASKS.md (this file's git history):
12.1–12.6, 15.1–15.4, 20.1–20.10, 11.1/11.2.

P21 happened because the shade "did not feel good" on the user's pass —
so this phase carries the fix-the-feel burden, not just the revert:

- [/] 28.1 Recover the shade/QS commit set from fork history onto the
      candyos frameworks/base series (30 patches today; the rollback is
      0029): 48dp donor header, QQS = notifications-only, two-panel
      flip + gear toggle, donor QS grid + tiles + brightness dialog,
      KitKatPanelFling physics, ticker interplay unchanged (the ticker
      survived P21 and ships today). DONE 2026-10-28: the series was
      rebuilt as a clean 13-patch chain (the P21 rollback patch and
      the old fork-tree tracker commits folded out — see 31.3), replayed on
      the synced upstream revision, and diff-verified against the
      pre-P21 tree state: the shade/QS set + the P23 ticker fix, byte
      for byte. candyos-verify.sh green. SUPERSEDED 2026-11-09: the
      LOS-24 "compatibility" rebase rewrote the series for the new
      upstream — the byte-for-byte claim held on LOS 21 only; the
      shade/QS patches now live in the 22-patch frameworks-base series
      (0011 + 0013 + 0014) and verification re-opens in 39.6.
- [X] 28.2 Carry the three fixed-in-tree-but-never-rebuilt fixes of
      the last batch: QQS max-tiles check-before-add, drag handle
      above the navbar, applyDonorFlipToMaxExpansionHeight on both
      height writers. Carried by construction through the P28 series
      and the LOS-24 rebase; re-verify on the 39.0 build (39.6).
- [ ] 28.3 The P21 feel pass, now on the LOS-24 build (rides 39.0/39.9)
      AND the user's hands before any marker flips: drag/fling feel,
      gear/X taps, QQS<->QS transitions, brightness dialog open.
- [X] 28.4 Regenerate the series (candyos-save.sh), candyos-verify.sh
      green, architecture.md change-map row updated. 13 patches,
      BASE 993528cf0efb; all six series verified clean (LOS-21 era).
      The series was regenerated again for LOS 24 (2026-11-08) —
      verify green per 39.8.
- [ ] 28.5 Full build + emulator battery + USER ACCEPTANCE GATE — the
      P21 lesson: this phase does not close without the user pass.
      Now rides the first green LOS-24 build (39.0) — build + emulator
      run by the user (28.3 and 28.5 markers flip on that pass).
- [/] 28.6 QS footer actions flat-donor strip — user 28.3 emulator-pass
      critique: the bottom band (settings/power/user row,
      qs_footer_actions) was stock AOSP's M3 "underSurface" layer —
      material_dynamic_neutral0 pure black, rounded top corners, 4dp
      elevation shadow, alpha delayed to the last 10% of the QS
      expansion (FooterActionsViewModel) — which the P24 flat
      #ff161616 page turned into a black band popping in over the
      gray page (read as a bug; no P12/P15/P20-era patch ever touched
      the row). DONE 2026-10-29 (frameworks/base 0015):
      qs_footer_actions_background flat #ff161616 with square top
      corners (donor band: status_bar_expanded_header /
      notification_header_bg, flat opaque #FF000000 full-bleed from
      the first frame) + footer_actions outlineProvider=none (no
      elevation shadow; the 4dp elevation stays matched to the QS
      scroll view for z-order parity). The stock last-10% fade is
      kept on purpose: band color == panel color makes the surface
      transition invisible, only the glyphs ride the fade; the
      keyguard QS gets the same flat band, matching its #ff161616
      tiles. PENDING: the 39.0 emulator pass (verify the rebase kept
      it).

## Phase 29 — Lock screen [ ]

The keyguard already ships KitKat colors and typography (P6: Holo
bouncer + PIN pad, 2013 Roboto thin clock, 4.4 Lock/Unlock sounds,
monet pinned) — on the old base; re-verified per 39.6. Open work:

- [ ] 29.1 Ring-hint geometry port (the 14.13 carry): the modern
      swipe-up pill hint + bottom shortcuts vs the 4.4 unlock-ring
      pattern, donor ~/kitkat-src/frameworks-base keyguard. Was gated
      on shade stability (P20-era ruling); the gate is the 39.6 shade
      pass now — do this once 28.3/28.5 close on the LOS-24 build.
- [ ] 29.2 P6 leftovers: PIN container + delete/enter button styling
      review; bouncer over light wallpapers; emergency-button contrast
      on dark SIM states.

## Phase 30 — Settings visual pass [ ]

Settings ships dark Holo with 4.4 icons (P7 + 11.6, plus the P38 code
additions below) — pending LOS-24 re-verification. Remaining:

- [ ] 30.1 On-device visual walk: About phone, main list, dialogs,
      dark-mode switch colors, search pill (restyles itself dark via
      the cascade), the modern-only pages' vector-icon hybrid
      (accepted per rule 5). The walk now also covers the P38 output:
      Holo row/bar switches, dark in-app dialogs, the pinned-dark
      TabLayout/credential surfaces. One deliberate leave-alone to
      eyeball: Theme.Settings colorPrimary still points at
      primary_device_default_settings_light (the P11.6 sweep chose
      that; flip it dark only if the walk shows a light surface).
      Rides the 39.0 build (with 39.6 Settings + 38.5).

## Phase 37 — Homescreen navbar gradient scrim [ ]

The 2026-11-04 "Nav bar shadow attempt" commit landed as a hand-forged
patch file (placeholder hunk headers, non-existent
win.isHomeWindow()) that could never apply — candyos-verify died on
it. Re-landed for real in frameworks-base (0018 in the LOS-24 series):

- [X] 37.1 The real change: DisplayPolicy.adjustWindowParamsLw forces
      FLAG_TRANSLUCENT_NAVIGATION on home-activity windows, so the
      launcher draws behind the bar and NavigationBarTransitions
      renders the P35 nav_bar_kitkat_scrim.xml gradient over the
      wallpaper. The attempt's second half (BarBackground mOpaque
      pinned black) was already the tree state
      (system_bar_background_opaque = #ff000000) — a no-op, not
      carried.
- [ ] 37.2 On-device QA (rides the 39.0 build): scrim over the
      CandyLauncher wallpaper, launcher lays out correctly edge-to-edge
      (CandyLauncher is presigned/unpatchable), no regressions on the
      opaque-black bar in apps.

## Phase 38 — Settings 4.4 fidelity pass [ ]

User direction (2026-11-06): finish making Settings resemble the 4.4
donor. The 11.6 cascade had dressed the page chrome (dark surfaces,
4.4 category headers, 16dp paddings); four widget/dialog surfaces
still rendered modern:

- [X] 38.1 Row switches: androidx SwitchPreferenceCompat's widget
      layout was a MaterialSwitch under
      Theme.Material3.DynamicColors.DayNight — M3 pills with
      force-styled Material colors on every row. Now the plain
      android.widget.Switch (the 4.4 donor widget); androidx binds
      any Checkable, so no code changed. (Settings series.)
- [X] 38.2 SettingsLib switch styles (Switch.SettingsLib,
      SwitchCompat.SettingsLib, SwitchBar.Switch.Settingslib — the
      switchStyle for the main switch bar, panel dialogs, framework
      switches) reparented to Widget.Holo.CompoundButton.Switch:
      the 4.4 switch_bg_holo_dark 9-patch track, state-list thumb
      and ON/OFF track labels, replacing the M3 shape drawables.
      The 48dp fixed heights are dropped so the 9-patch renders at
      intrinsic size. (frameworks-base 0019 in the LOS-24 series.)
- [X] 38.3 DayNight light-mode leaks pinned dark: the app's AndroidX
      alert dialogs (Theme.AlertDialog.Base was
      AppCompat.DayNight.Dialog.Alert with surface_light — every
      in-app dialog rendered light in day mode; now the dark
      AppCompat parent + system_neutral1_800, mirroring the
      SettingsLib 11.6 fix) and Theme.TabTheme /
      Theme.CollapsingToolbar.Settings (MaterialComponents.DayNight
      with light pins; now the dark MaterialComponents base).
      (Settings series.)
- [ ] 38.4 Compile check: `m Settings` (validates the new XML res —
      the @*android:drawable refs and style parents — plus
      SettingsLib res; the DisplayPolicy change rides the services
      jar build). Rides the 39.0 build.
- [ ] 38.5 On-device QA (rides 30.1's walk, on the 39.0 build): Holo
      switches in rows and the Wi-Fi/Bluetooth switch bar, ON/OFF
      track labels at intrinsic size, dark dialogs (Wi-Fi
      forget/edit, reset confirmations), dark TabLayout/credential
      pages.

Rule 5 deltas accepted: the face-enroll accessibility toggle keeps
its MaterialSwitch (no 4.4 counterpart); Theme.Settings colorPrimary
stays settings_light pending the 30.1 eyeball (see above); the M3
settingslib switch shape drawables stay in-tree (unused, upstream
drift kept low).

## Phase 31 — Single-repo migration loose ends [X]

Live-tree edits that pre-date the candyos patch-series model and are
NOT captured by any series — every sync silently reverts them. All
three verified and captured on the LOS-21 base; re-check the class
itself on LOS 24 via 39.8:

- [X] 31.1 vendor/lineage default-wallpaper overlay deletions (the 8
      entries under overlay/common/frameworks/base/core/res — the P8
      root cause; the word "wallpaper" appears nowhere in
      patches/vendor-lineage). Without them, the LOS product RRO
      re-overrides the KitKat framework-res wallpaper on every sync.
      CAPTURED 2026-10-28: committed in the vendor/lineage project and
      regenerated into patches/vendor-lineage (0004, the P28 patch);
      no XML/mk references the drawables. Fixed for every future sync.
- [X] 31.2 packages/modules/Permission GrantPermissions restyle (P14.4:
      Theme.DeviceDefault.Dialog.Alert parent) — not a patched project;
      the runtime-permission dialog reverts to Material light on sync.
      CAPTURED 2026-10-28: the live-tree edit was still present (never
      sync-clobbered since); committed it, added the NEW
      patches/permission series (1 patch) and a CANDYOS_PATCHED entry
      (packages/modules/Permission) to scripts/common.sh so sync/save/
      verify/manage it like the others. Fixed for every future sync.
- [X] 31.3 The frameworks-base series carries the old fork-tree
      TASKS.md bookkeeping (roughly half the 30 patches touch only the
      tracker). docs/phases/TASKS.md is canonical now; fold the
      tracker-only commits out when regenerating the series for P28.
      DONE 2026-10-28 with the P28 series rebuild: the 15 tracker-only
      patches (TASKS.md / kitkat_notes.txt) are gone, the surviving
      patches are code+assets only, and the tree no longer contains
      TASKS.md or kitkat_notes.txt at all.

## Phase 32 — Bundled wallpapers [ ]

User direction (2026-10-28): `apps/assets/wallpapers/` is the wallpaper
set — the single `default.<ext>` image becomes the system default,
every image ships on the system, and the folder updates often, so a
sync/build must always pick up the current contents. (Default file is
currently `default.png` — any extension per the 33.4 amendment.)

- [X] 32.1 Default wallpaper: the generated framework overlay
      `vendor/candyos/overlay` overrides framework-res
      `default_wallpaper` (drawable-nodpi + sw600dp + sw720dp buckets)
      with `default.<ext>`, registered as the LAST
      `PRODUCT_PACKAGE_OVERLAY` by patches/vendor-lineage/0005 (wins over
      the LineageOS overlays; the LOS RRO's own wallpaper entries were
      already deleted in 31.1). The KitKat 4.4 wallpaper from the
      frameworks/base P8 patch stays as the in-tree fallback.
- [X] 32.2 All images on the system + pickable: CandyOS Experience gains
      a Wallpapers screen (menu entry, Holo grid of thumbnails,
      system + lock apply via WallpaperManager; SET_WALLPAPER +
      platform-signed SET_WALLPAPER_HINTS). The images + the
      string-array that drives the screen are GENERATED app res — folder
      edits never touch app code.
- [X] 32.3 The "every build" plumbing: scripts/candyos-gen-wallpapers.py
      regenerates BOTH generated locations (repo overlay + repo app res)
      from apps/assets/wallpapers/ and pushes them into a standing tree when
      given the tree argument; candyos-sync.sh runs it before the
      vendored copies on every sync (so a sync IS a wallpaper refresh),
      and `vendor/candyos` rides CANDYOS_VENDOR_TREES. Edit the folder,
      re-run sync (or the script alone + rebuild), commit, done.
- [ ] 32.4 On-device QA (rides the 39.0 build): default wallpaper
      boots (home + lock), the Wallpapers grid renders every image,
      each pick applies system + lock, default entry applies what the
      overlay ships. NOTE: whether the overlay lands static or as an
      enforced RRO depends on the emulator target's ENFORCE_RRO
      setting — verify the override actually wins in the built image.
- [ ] 32.5 USER ACCEPTANCE (rides the 39.0 build + 39.9 pass).

## Phase 33 — KitKat launcher icons [ ]

User direction (2026-10-29): ALL built-in LineageOS apps use KitKat-styled
icons; `reference/icons/` (the committed 43-PNG KitKat reference set) is
the reference. Source of truth + generator: `reference/icons/`,
`reference/donor-icons/`, `reference/originals/` and
`scripts/candyos-gen-icons.py` (repo app res + the eleven patched projects;
see docs/architecture.md "Launcher icons (P33)").

Already KitKat by construction (no change): the vendored 4.4 ports
(Calculator, Calendar, DeskClock, Music, Sound Recorder) — donor icons,
verified against the reference set. CandyOS Experience keeps its candy
mark (already period-styled; user direction).

- [X] 33.1 Donor-byte icons where a 4.4 donor exists: Settings
      (settings-4.4 ic_launcher_settings, per-density drawables replacing
      the adaptive icon) and the LatinIME keyboard drawer entry
      (latinime-4.4 ic_launcher_keyboard, mdpi..xxhdpi).
- [X] 33.2 Reference-set icons for the stock drawer apps: Contacts
      (People), Dialer (Phone), Messaging, Jelly (Browser), Glimpse
      (Gallery), DocumentsUI (Downloads — Files' 4.4 ancestor), Aperture
      (Camera). Nine NEW one-patch series (patches/contacts dialer
      messaging jelly glimpse documentsui aperture audiofx stk),
      registered in CANDYOS_PATCHED; candyos-save.sh + candyos-verify.sh
      green (all series apply clean — re-verify on LOS 24 per 39.8).
- [X] 33.3 No-counterpart apps: AudioFX (synthesized Holo-dark EQ-fader
      tile) and Stk (synthesized gray SIM-card tile) — KitKat-family
      geometry (rounded corners, darker border ring, soft shadow), drawn
      by candyos-gen-icons.py.
- [X] 33.4 CandyOS originals re-tiled (Notes, Reminders): brand art kept,
      re-tiled onto the reference family geometry by candyos-gen-icons.py
      (repo apps/ + tree slots; no patch series — vendored apps).
      AMENDED 2026-10-29 (user direction): Notes switched to the
      user-supplied reference/icons/Notes.png (used as-is; non-square
      references auto-cropped to content and square-padded), Reminders
      stays re-tiled. Same pass: candyos-gen-wallpapers.py now takes the
      system-default wallpaper as any single default.<ext> (Phase 32's
      plumbing convention; the strict default.jpg name requirement is
      gone).
- [ ] 33.5 On-device QA (rides the 39.0 build): every drawer entry shows
      its KitKat icon at all densities, Settings + keyboard included;
      presigned exceptions documented (CandyLauncher "thelauncher",
      the microG/Aurora APKs, Organic Maps — cannot be restyled without
      breaking signatures; list current as of P34).
- [ ] 33.6 USER ACCEPTANCE (rides the 39.0 build + 39.9 pass).

## Backlog — deferred by ruling, not scheduled

* Gallery (P16 Tier 3): the 4.4 Gallery2 donor == the app the modern
  tree already ships, so the port is a git-history replacement + JNI
  modernization — a focused batch bigger than Music + DeskClock +
  Calendar combined. Modern Gallery2 rides the cascade until then.
* Camera (P16 Tier 3): camera1 legacy-mode spike; needs a working
  capture HAL (the emulator camera HAL is unreliable for capture) —
  real hardware or a dedicated AVD. Modern Aperture rides the cascade.
* 24.4 (optional polish): a CandyOS-hosted WebUSB installer page on
  the fastboot.js + android-webinstall pattern — not a gap; Lineage
  Flash Tools already installs CandyOS today.
* OTE nav-style step (from 39.1): if the wizard's navigation-style
  step turns out to have been lost before the base move, restoring it
  is CandyExperience app work that may be scheduled separately from
  the 39.1 default pin.

## QA passes riding the first green LOS-24 build (39.0)

- [ ] P25.3: CandyExperience fresh-wipe on-device QA — Setup flow,
      Wi-Fi pick round-trip, apps disable/restore, nav mode switches
      (ties into 39.1), About / Menu / Testing menus.
- [ ] P26 build pass: boot animation on the LOS-24 build (now 39.4).
- [ ] P27.3: SUPERSEDED (P34 dropped MindTheGapps) — the equivalent
      hardware pass is 34.6 (microG spoof + Aurora download on
      hardware).
- [ ] P24.3: first real-device WebUSB install pass (hardware-gated).
- [ ] 34.6: fresh-wipe OTE pass with the microG/Aurora stack + spoof
      check (34.7 flips with 39.9).

## Hardware / touch passes (rule 6: AVD-only until hardware)

- [ ] ticker marquee smoothness (14.11), gesture typing (13.2),
      overscroll glow eyeball (14.1), volume slim-rail detail (14.10),
      power-menu row press highlight (P4), recents card feel (P5),
      Music playback + DeskClock alarm-ring (audio AVD), Calendar
      month/day/week + event flows (P16), bouncer-over-wallpaper
      eyeball (= 29.2).
- [ ] The 39.1 nav-mode default on real hardware (gesture-mode default
      pinning differs per-device in vendor config).

## Phase 34 — microG + Aurora Store replace MindTheGapps [ ]

User direction (2026-10-29): drop the real Google services (MindTheGapps)
and ship the opt-in Play compatibility stack instead — microG Services +
microG Companion + Aurora Store — with **whether it runs at all** being
the user's call: a dedicated OTE step decides at first run, and the
CandyOS Experience menu ("Google services") can flip it anytime after.

- [X] 34.1 Remove MindTheGapps: fetch script pins, gapps-arm64/gapps-arm
      extraction, gen-gapps-mk.py retired, .gitignore/common.sh cleaned,
      docs rewritten (README, architecture, apps, getting-started,
      install-webusb).
- [X] 34.2 Signature spoofing, take two (the documented reason microG was
      originally dropped): frameworks-base P34 patch
      (0015 in the LOS-24 series) declares the hidden
      FAKE_PACKAGE_SIGNATURE (signature|privileged) and spoofs the fixed
      Google certificate for exactly com.google.android.gms +
      com.android.vending when they hold it — CalyxOS approach,
      metadata-independent (the LOS built-in userdebug hook is dead with
      current microG: resource-typed fake-signature meta-data is dropped
      by package parsing), works on user builds too. Whitelist grants
      scoped in vendor/partner_gms/microg (GmsCore: +INSTALL_LOCATION_
      PROVIDER, CHANGE_DEVICE_IDLE_TEMP_WHITELIST, UPDATE_APP_OPS_STATS +
      power-save sysconfig — the LineageOS-for-microG grant set;
      Companion: spoofing only).
- [X] 34.3 vendor/partner_gms/microg: GmsCore + Companion product
      priv-apps, AuroraStore product app, PRESIGNED verbatim installs,
      uses-library sets mirrored from the pinned APKs; products/gms.mk
      unconditional (universal APKs — the emulator QA images now carry
      the whole stack).
- [X] 34.4 CandyOS Experience: dedicated OTE "Google services" step
      (after Default apps), fresh-boot defaults pinned disabled by the
      wizard, PlayConfig shared by the step and the new
      PlayServicesActivity ("Google services" menu entry) with
      instant-apply toggles; Aurora moved out of the generic
      optional-apps list into the dedicated step.
- [X] 34.5 Updater integration: microG Services + Companion tracked from
      the shared microg/GmsCore release repo (new TrackedApp.assetFilter
      regex per entry — a release carries -hw/.asc/org.microg.gms assets
      that must not be picked; ETag cache key now per package so two
      entries may share one repo). Aurora NOT tracked (GitLab-only
      releases); its own self-updater covers it, and the pinned APK is
      the upstream-signed build so in-app updates install over the
      preload.
- [ ] 34.6 QA (rides the 39.0 build): fresh-wipe OTE pass — play step
      shows both rows unchecked, opt-in enables exactly the checked
      packages, finish leaves microG off when unchecked; menu screen
      round-trip both ways; spoof check — `adb shell dumpsys package
      com.google.android.gms` shows the Google certificate
      (F0:FD:6C:5B:41:0F:25:CB:25:C3:B5:33:46:C8:97:2F:AE:30:F8:EE:74:11:
      DF:91:04:80:AD:6B:2D:60:DB:83), not the NOGAPPS one
      (9B:D0:67:27:...); push notification end-to-end after battery-
      optimization exemption; microG update pass once a newer GmsCore
      release lands (assetFilter picks the right APKs). Re-verify the
      spoof patch applies on the LOS-24 package-parsing code — the
      P34.2 metadata claim was verified on LOS 21.
- [ ] 34.7 USER ACCEPTANCE (rides 34.6 + the 39.9 pass).

Notes / deltas accepted by this phase (rule 5):
* Compatibility narrows vs real GApps: Play Integrity / some banking /
  anti-tamper apps and paid-app licensing stay broken — inherent to
  microG + a stub Play Store, not fixable at the CandyOS layer.
* P27.3 (MindTheGapps arm64 device build + Play login pass) is superseded:
  the equivalent hardware pass is now 34.6 (microG spoof + Aurora
  download on hardware).
