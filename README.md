# Dexor

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="96" height="96" alt="Dexor Logo" />
</p>

<h3 align="center">Unified DEX2OAT Runtime & Ahead-Of-Time (AOT) Bytecode Manager for Android</h3>

<p align="center">
  <a href="https://github.com/DeveshTone/Dexor/releases"><img src="https://img.shields.io/badge/Release-v1.0.0-blue?style=flat-square" alt="Release" /></a>
  <a href="https://developer.android.com"><img src="https://img.shields.io/badge/Android-9.0%20--%2015%20(API%2028--35)-brightgreen?style=flat-square" alt="Android Support" /></a>
  <a href="https://github.com/RikkaApps/Shizuku"><img src="https://img.shields.io/badge/Shizuku-API%2013.1+-orange?style=flat-square" alt="Shizuku Optional" /></a>
  <img src="https://img.shields.io/badge/Root-Direct%20Channel-success?style=flat-square" alt="Root Supported" />
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-green?style=flat-square" alt="MIT License" /></a>
  <img src="https://img.shields.io/badge/APK%20Size-3.16%20MB-purple?style=flat-square" alt="APK Size" />
  <img src="https://img.shields.io/badge/Privacy-100%25%20Offline%20(Zero%20Internet)-success?style=flat-square" alt="100% Offline" />
</p>

---

## What is Dexor?

**Dexor** is a modern Android utility that puts you in control of how your applications are compiled and executed by the **Android Runtime (ART)**.

Dexor talks to Android's internal compilation service (`cmd package compile`) through one of two privilege channels, picked automatically:

- **Root Direct Channel** — on rooted devices (Magisk, KernelSU, etc.) Dexor runs every command through a one-shot `su` session all by itself. No other app is needed, no persistent root shell is kept, and the app process itself never elevates.
- **Shizuku** — when a [Shizuku](https://github.com/RikkaApps/Shizuku) server is running and authorized, it is preferred (it usually runs at the unprivileged shell UID) and the Root channel becomes the automatic fallback.

### How it Works (Why You Need It)

When Android apps are installed or updated, they consist of Dalvik Executable (DEX) bytecode. To execute this code quickly, the Android Runtime (`dex2oat`) compiles bytecode into native machine code.

- **The Default Behavior:** Normally, Android only runs full AOT compilation during idle maintenance—when your device is connected to a charger, at 100% battery, and completely untouched overnight (`bg-dexopt`). Otherwise, apps run through Just-In-Time (JIT) interpretation, leading to initial launch delays and micro-stutters while code compiles on the fly.
- **The Dexor Solution:** Dexor unlocks immediate, granular control over this process. You can force-compile demanding apps or games to `speed` mode to eliminate JIT warmups, or revert unneeded apps to `verify` mode to reclaim gigabytes of storage space—all with a single tap.

---

## Screenshots

<p align="center">
  <img src="screenshots/app_list.png" width="31%" alt="App Catalog" />
  <img src="screenshots/app_detail.png" width="31%" alt="Deep App Inspection" />
  <img src="screenshots/compilation_modes.png" width="31%" alt="AOT Compilation Modes" />
</p>

---

## Key Features

- **Elevated AOT Compilation Without Root:** Utilizes Shizuku IPC to safely execute native `cmd package compile` commands without modifying system partitions.
- **Self-Contained Root Direct Channel:** On rooted devices, Dexor needs nothing else — every privileged command runs through a one-shot `su` child process (hardened with timeouts, merged-output draining, and package-name whitelisting). No companion app, no resident root shell, and the app process stays at its normal UID.
- **Deep Dexopt State Inspection:** Inspect real-time compiler filters (`speed`, `space`, `verify`, `everything`), compilation trigger reasons (`cmdline`, `install`, `bg-dexopt`), and APK path details for every installed package.
- **Batch Optimization Pipeline:** Select multiple apps across user and system categories to compile sequentially, featuring live progress indicators and estimated time calculations.
- **Instant Search & Multi-Criteria Filtering:** Filter between User and System apps, and sort instantly by App Name, DEX Status, or Package Source.
- **Bilingual UI (中文 / English):** Follows the system language automatically and can be switched manually at runtime in Settings (per-app locales, persisted across restarts).
- **100% Offline & Private:** `android.permission.INTERNET` is completely absent from the manifest. Dexor does not collect telemetry, track usage, or display advertisements.
- **Lightweight & High Performance:** Built with Jetpack Compose, an asynchronous coroutine pipeline, in-memory Room caching, and compressed to just **3.16 MB** with R8 full minification.

---

## Compilation Modes

Dexor exposes Android's native compilation filters so you can balance runtime execution performance with device storage:

| Mode | Filter Flag | How It Works | Storage Impact | Best Used For |
| :--- | :--- | :--- | :---: | :--- |
| **Speed Profile** | `speed-profile` | Compiles hot code based on available runtime execution profiles. Falls back to verify without profiles. | Medium | Balanced daily performance tailored to actual app usage. |
| **Speed** | `speed` | Force-compiles all accessible bytecode methods into native machine code. Eliminates JIT compilation stutters. | High | Games, daily drivers, and performance-sensitive apps. |
| **Space** | `space` | Compiles core application initialization and entry points only. | Low–Medium | General apps where saving disk space is prioritized over peak velocity. |
| **Verify** | `verify` | Validates DEX bytecode structure without generating native machine code. Reverts the app to the raw JIT interpreter. | Minimum | Reclaiming maximum storage space from rarely used apps. |
| **Everything** | `everything` | Exhaustively compiles every method and resolves all class references unconditionally. | Maximum | Complete native execution across entire APK codebase. |
| **Reset / Clear** | `--reset` | Clears all compiled artifacts and resets execution profiles back to original install state. | Zero | Resetting problematic compilation states and recovering all disk space. |

---

## Requirements

- **Android Version:** Android 9.0 to Android 15 (API 28 – 35)
- **Privilege:** **either** a rooted device (Magisk / KernelSU / APatch) — Dexor then works standalone — **or** [Shizuku](https://github.com/RikkaApps/Shizuku) (v13.1.5 or newer) running via Wireless Debugging or ADB

> [!NOTE]
> Dexor picks the privilege channel automatically: an authorized Shizuku server is preferred (it typically runs at the unprivileged shell UID), and the Root direct channel is the fallback when Shizuku is absent. Without either, Dexor operates in a read-only inspection mode. Your root manager still shows its usual grant dialog the first time a `su` call is made.

---

## Installation

1. Download the latest **`app-release.apk`** from [GitHub Releases](https://github.com/DeveshTone/Dexor/releases).
2. Install the APK on your Android device.
3. Ensure [Shizuku](https://shizuku.rikka.app/) is running, then launch Dexor and grant permission when prompted.

---

## Building from Source

### Prerequisites
- Android Studio Iguana+ or Gradle 8.4+
- JDK 17 (Java Development Kit 17)
- Android SDK Platform 34 & Build Tools 34.0.0

```bash
# Clone the repository
git clone https://github.com/DeveshTone/Dexor.git
cd Dexor

# Assemble release APK
./gradlew assembleRelease
```
The optimized release APK will be generated at:
`app/build/outputs/apk/release/app-release.apk`

---

## Privacy & Security

Dexor adheres strictly to privacy-first Android development:
- **Zero Network Permissions:** No `INTERNET` permission requested. Network access is physically impossible.
- **Zero Trackers:** No analytics SDKs, crash report beacons, or third-party advertising libraries.
- **System Integrity:** Operates strictly through Android's documented package management endpoints; system partitions remain untouched.

---

## Credits & Open Source

- **Developer:** [DeveshTone](https://github.com/DeveshTone)
- **[Shizuku](https://github.com/RikkaApps/Shizuku)** by [RikkaApps](https://github.com/RikkaApps) — Elevated Android system APIs without root.
- **[Jetpack Compose](https://developer.android.com/jetpack/compose)** & AndroidX by Google LLC.
- **[Room Persistence](https://developer.android.com/training/data-storage/room)** by Google LLC.

---

## License

This project is licensed under the [MIT License](LICENSE).

```
Copyright (c) 2026 DeveshTone
```
