package com.blog.example.client;

import com.blog.example.license.LicenseManager;

// Simulates a telemetry component that records how often
// each licensed module is used. It writes through the singleton
// so every part of the application shares the same counters.
public class UsageReporter {

    /**
     * Records one hit for the given module, but only if the
     * feature is actually unlocked.
     */
    public void recordModuleUsage(String moduleName) {
        LicenseManager lm = LicenseManager.getInstance();
        if (lm.isFeatureUnlocked(moduleName)) {
            lm.recordHit(moduleName);
            System.out.println("[UsageReporter] Recorded hit for " + moduleName);
        } else {
            System.out.println("[UsageReporter] Skipped " + moduleName + " (not licensed)");
        }
    }
}
