package com.wynntils.utils;

import java.awt.HeadlessException;
import java.awt.image.BufferedImage;

/**
 * Minimal stub so mixins compile when Wynntils is not on the classpath.
 * Excluded from the published jar; real implementation is provided by Wynntils at runtime.
 */
public class SystemUtils {
    public static boolean isWayland() {
        return false;
    }

    public static void copyImageToClipboard(BufferedImage bi) throws HeadlessException {}
}
