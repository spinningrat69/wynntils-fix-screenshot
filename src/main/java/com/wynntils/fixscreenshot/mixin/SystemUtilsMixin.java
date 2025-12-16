package com.wynntils.fixscreenshot.mixin;

import com.wynntils.utils.SystemUtils;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SystemUtils.class, remap = false)
public abstract class SystemUtilsMixin {
    private static final Logger LOGGER = LogManager.getLogger("WynntilsFixScreenshot");

    @Inject(method = "copyImageToClipboard", at = @At("HEAD"), cancellable = true)
    private static void wynntilsFixScreenshot$useWaylandClipboard(BufferedImage bi, CallbackInfo ci) {
        if (!SystemUtils.isWayland()) return;
        if (!GraphicsEnvironment.isHeadless()) return;

        if (copyWithWlCopy(bi)) {
            // Clipboard was handled using wl-copy; skip the default AWT path that crashes on headless Wayland.
            ci.cancel();
        }
    }

    private static boolean copyWithWlCopy(BufferedImage image) {
        Process process;
        try {
            process = new ProcessBuilder("wl-copy", "--type", "image/png").start();
        } catch (IOException e) {
            LOGGER.debug("wl-copy not available; keeping Wynntils default clipboard path", e);
            return false;
        }

        try (BufferedOutputStream outputStream = new BufferedOutputStream(process.getOutputStream())) {
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            process.destroy();
            LOGGER.warn("Failed to stream screenshot to wl-copy", e);
            return false;
        }

        try {
            boolean finished = process.waitFor(1, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                LOGGER.warn("wl-copy did not finish within 1s");
                return false;
            }

            if (process.exitValue() == 0) {
                LOGGER.debug("Copied Wynntils screenshot via wl-copy on Wayland");
                return true;
            }

            LOGGER.warn("wl-copy exited with code {}", process.exitValue());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.warn("Interrupted while waiting for wl-copy", e);
        }

        return false;
    }
}
