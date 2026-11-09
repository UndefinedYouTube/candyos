# vendor/candyos — the CandyOS vendor overlay

The overlay directory is registered as a build-time
`PRODUCT_PACKAGE_OVERLAYS` entry (appended after the LineageOS overlays in
`vendor/lineage/config/common.mk` by `patches/vendor-lineage/`, so it has the
higher priority) and ships:

* `frameworks/base/core/res/res/drawable{,-sw600dp,-sw720dp}-nodpi/default_wallpaper.*`
  — **the system default wallpaper**, from `assets/wallpapers/default.jpg`.
  It overrides the framework-res default (the KitKat 4.4 wallpaper the
  frameworks/base P8 patch installs as fallback) in all three density
  buckets, the same mechanism the LineageOS product overlay used before P28
  deleted its eight wallpaper entries from it.

  **These drawable buckets are GENERATED** by
  `scripts/candyos-gen-wallpapers.py` from `assets/wallpapers/` — never edit
  them directly; edit the wallpapers folder and re-run the script (a plain
  `candyos-sync.sh` re-runs it too). The generator removes and regenerates
  only the three drawable buckets, nothing else under `overlay/`.

* `frameworks/base/core/res/res/values/candyos_defaults.xml` — **hand-maintained
  framework defaults** (not touched by the generator). Currently one pin,
  P39.1: `config_navBarInteractionMode = 0` — KitKat-style 3-button
  navigation is the system DEFAULT, not a lock; the CandyOS Experience wizard
  step and menu screen switch modes at runtime by enabling the
  `com.android.internal.systemui.navbar.*` mode overlays (what Settings does),
  and a runtime-enabled mode overlay beats this static overlay.

Every image in `assets/wallpapers/` (the default included) is additionally
bundled into the CandyOS Experience app (`apps/CandyExperience/res/`,
generated the same way) and listed by its **Wallpapers** screen, which
applies the picked image as the system and lock-screen wallpaper.

To change the wallpaper set: drop/remove/rename files in
`assets/wallpapers/` (the default is the single `default.<ext>` image, any
extension), then `scripts/candyos-gen-wallpapers.py [tree]` — or just
`candyos-sync.sh`, which regenerates and re-vendors it on every sync — and
commit the result.