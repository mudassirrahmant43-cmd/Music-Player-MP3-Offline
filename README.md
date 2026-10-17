# Music Player: MP3 & Offline

A sleek, independently branded local music player for Android with offline playback, powerful equalizer, custom playlists, modern adaptive design, and Google Play Premium subscriptions.

---

## Features

- **Offline Local Audio Playback**: Plays local MP3, FLAC, WAV, AAC, and OGG files directly on device.
- **Powerful Equalizer & Preset Manager**: Adjust audio frequencies, bass boost, and sound effects.
- **Sleep Timer**: Gentle fade-out timer for relaxing nights.
- **Playlist & Library Management**: Organize songs by Tracks, Albums, Artists, Genres, and Folders.
- **Lock-screen & Notification Controls**: Integrated AndroidX Media3 session controls.
- **Custom Themes & Dark Mode**: Modern dark/light interface with adaptive color schemes.
- **Non-Intrusive Bottom Banner Ad Integration**: A compact bottom banner ad powered by Google Mobile Ads (AdMob) for free users.
- **Premium Subscriptions**: Option to remove bottom banner ads via Monthly or Yearly subscription plans.

---

## Premium Subscription Architecture & Google Play Billing

The application implements a secure, single-source-of-truth entitlement architecture (`PremiumManager`) integrated with Google Play Billing (`BillingHelper`).

### Subscription Product Config

| Plan | Product ID | Target Price (PKR) | Value Highlight |
|---|---|---|---|
| **Free** | N/A | PKR 0 | Displays bottom banner ad; full music playback features available |
| **Premium Monthly** | `premium_monthly` | PKR 150 / month | Removes bottom banner ad completely |
| **Premium Yearly** | `premium_yearly` | PKR 1,260 / year | **SAVE 30%** compared to monthly plan |

### Single Source of Truth (`PremiumManager`)

- `PremiumManager` manages active entitlement state using `StateFlow<Boolean>` for reactive UI updates.
- When Premium is active, `AdHelper` automatically hides `ad_banner_holder` (`View.GONE`), removing both the ad and its layout space without interrupting music playback.
- Offers a **Restore Purchases** flow and direct link to Google Play Subscriptions management (`https://play.google.com/store/account/subscriptions`).

---

## Google Play Console Setup Instructions

To activate live subscriptions for **Music Player: MP3 & Offline**:

1. **Create Subscription Products**:
   - Go to **Google Play Console** -> **Monetize** -> **Subscriptions**.
   - Create Subscription 1: Product ID `premium_monthly`, set base plan price to **PKR 150 / month**.
   - Create Subscription 2: Product ID `premium_yearly`, set base plan price to **PKR 1,260 / year**.
   - Activate both subscription products.

2. **Setup Internal Testing & License Testers**:
   - Go to **Play Console** -> **Setup** -> **License Testing**.
   - Add tester Gmail accounts to the license testing list to test real Google Play purchase flows without actual charges.
   - Upload a signed build to an **Internal Testing** track on Google Play Console.

3. **Backend / Token Verification (Optional for Production Scale)**:
   - For server-validated purchase tokens, connect `BillingHelper` to a backend server endpoint using Google Play Developer API (`purchases.subscriptions.get`).

---

## Advertising Configuration (AdMob)

Ads are integrated using Google Mobile Ads (AdMob) with official test ad unit IDs during development.

To configure production ads:
1. Open [`app/src/main/AndroidManifest.xml`](file:///D:/Disk%20D/Android%20studio%20projects/Music%20Player/app/src/main/AndroidManifest.xml) and replace `ca-app-pub-3940256099942544~3347511713` with your production AdMob Application ID.
2. Open [`AdHelper.kt`](file:///D:/Disk%20D/Android%20studio%20projects/Music%20Player/app/src/main/kotlin/org/fossify/musicplayer/helpers/AdHelper.kt) and replace `TEST_BANNER_AD_UNIT_ID` with your production Banner Ad Unit ID.
3. To disable ads entirely in development or specific builds, set `AdHelper.isAdsEnabled = false`.

---

## License & Attribution

Music Player: MP3 & Offline is derived from Fossify Music Player and is licensed under the [GNU General Public License v3.0 (GPLv3)](file:///D:/Disk%20D/Android%20studio%20projects/Music%20Player/LICENSE).

Original copyright (C) Fossify and contributors. All original license notices, copyright headers, and GPLv3 rights are retained.

---

## Building the App

1. Open the project in **Android Studio**.
2. Sync Gradle dependencies (`Gradle Sync`).
3. Build the debug APK via CLI or Android Studio:
   ```bash
   ./gradlew assembleFossDebug
   ```
