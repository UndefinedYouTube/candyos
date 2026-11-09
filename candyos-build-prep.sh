#!/usr/bin/env bash
# candyos-build-prep.sh — one-shot host setup for building CandyOS
# (lineage-24 / Android 17) on a 32GB RAM machine.
#
# What this does (all steps idempotent — safe to re-run):
#   1. Ensures a 48G build-swapfile exists and is enabled (soong bootstrap
#      spikes to ~36G total VM on this tree; 31G RAM alone OOMs with exit 137).
#   2. Raises vm.swappiness for the bootstrap (favors swap for soong's big
#      anonymous heap) and vm.max_map_count (Go runtimes map a lot).
#   3. Stops the CandyOS emulator if running (its stale pages pin swap and
#      its SwiftShader load steals memory during the spike).
#   4. Prints the exact build commands.
#
# After the FIRST successful `m`, the soong ninja file exists and later runs
# are incremental — you can then disable the extra swap and relaunch the
# emulator:
#   sudo swapoff /swapfile2 && sudo rm /swapfile2
#   ~/android/candyos/scripts/candyos-emulator.sh
#
# Usage:
#   sudo -E bash ~/android/candyos/candyos-build-prep.sh      (root needed)
# or just:
#   sudo bash $(which candyos-build-prep.sh 2>/dev/null || echo ~/android/candyos/candyos-build-prep.sh)

set -euo pipefail

SWAPFILE=/swapfile2
SWAPSIZE=48G

echo "== CandyOS build prep =="

# --- 1. Big build swapfile ------------------------------------------------
if swapon --show=NAME --noheadings 2>/dev/null | grep -qx "$SWAPFILE"; then
    echo "ok: $SWAPFILE already active ($(swapon --show | awk -v s="$SWAPFILE" '$1==s{print $3,$4}'))"
else
    if [ ! -e "$SWAPFILE" ]; then
        echo "creating $SWAPSIZE swapfile at $SWAPFILE ..."
        fallocate -l "$SWAPSIZE" "$SWAPFILE"
        chmod 600 "$SWAPFILE"
        mkswap "$SWAPFILE" >/dev/null
    fi
    swapon "$SWAPFILE"
    echo "ok: $SWAPFILE enabled (+$SWAPSIZE)"
fi

# --- 2. Kernel tunables ---------------------------------------------------
sysctl -w vm.swappiness=100 >/dev/null
sysctl -w vm.max_map_count=262144 >/dev/null
echo "ok: vm.swappiness=100, vm.max_map_count=262144 (session-only)"

# --- 3. Stop the emulator (frees swap + RAM) ------------------------------
if pgrep -f "[q]emu-system-x86_64" >/dev/null; then
    EMUSCRIPT="$(dirname "$0")/scripts/candyos-emulator.sh"
    if [ -x "$EMUSCRIPT" ]; then
        runuser -u "$(logname 2>/dev/null || echo ehill)" -- "$EMUSCRIPT" --kill \
            || pkill -f "[q]emu-system-x86_64" || true
    else
        pkill -f "[q]emu-system-x86_64" || true
    fi
    echo "ok: emulator stopped"
else
    echo "ok: emulator not running"
fi

# --- report ---------------------------------------------------------------
echo
free -h
echo
echo "== ready. Build with: =="
echo "  cd ~/android/lineage"
echo "  source build/envsetup.sh"
echo "  lunch lineage_sdk_phone_x86_64-aosp_current-userdebug"
echo "  m -j\$(nproc)"
echo
echo "Tip: keep this shell (and the swappiness) for the first full build;"
echo "after it succeeds you can drop $SWAPFILE and relaunch the emulator."