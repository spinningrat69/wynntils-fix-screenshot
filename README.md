# Wynntils Fix Screenshot

Intercepts Wynntils `SystemUtils.copyImageToClipboard` and pipes screenshots to `wl-copy --type image/png` when running headless on Wayland, avoiding `HeadlessException` while leaving other paths untouched.

## Requirements
- `wl-copy` from the `wl-clipboard` package on `PATH`.
- Wynntils present at runtime.

## Build
- `./gradlew build`
- Optional: place a Wynntils jar at `libs/wynntils.jar` or pass `-Pwynntils_maven=<group:artifact:version>` to compile against the real API instead of the stub.
