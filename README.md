# PDL Child Safety Android Prototype

Android MVP for validating the core loop:

1. Capture the visible screen with Android MediaProjection.
2. Pass sampled frames to a local safety-classifier interface.
3. Immediately cover the screen when high-risk content is detected.

## Scope

This repository intentionally focuses only on the first end-to-end prototype: **screen capture → local risk decision → immediate blocking overlay**.

Not included in this MVP: parent dashboards, location, SMS, remote control, cloud upload, or background surveillance.

## Privacy

The prototype is designed for on-device processing. Captured frames are not uploaded by this code.
