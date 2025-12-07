package com.mazesolver.util;

/**
 * Utility class for platform detection and platform-specific operations
 */
public class PlatformUtils {
    
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    
    public static boolean isWindows() {
        return OS_NAME.contains("win");
    }
    
    public static boolean isMac() {
        return OS_NAME.contains("mac");
    }
    
    public static boolean isLinux() {
        return OS_NAME.contains("linux") || OS_NAME.contains("unix");
    }
    
    public static String getPlatformName() {
        if (isWindows()) return "Windows";
        if (isMac()) return "macOS";
        if (isLinux()) return "Linux";
        return "Unknown";
    }
    
    /**
     * Get recommended rendering mode for the platform
     */
    public static String getRecommendedRenderingMode() {
        if (isWindows()) {
            // Windows works great with hardware acceleration
            return "hw";
        } else if (isMac()) {
            // macOS may need software rendering to avoid issues
            return "sw";
        } else {
            // Linux default
            return "hw";
        }
    }
}

