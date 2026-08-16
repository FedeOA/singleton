package com.blog.example.client;

import com.blog.example.license.LicenseManager;

// Simulates a component that needs to verify whether
// certain features are available under the current license.
public class FeatureChecker {

    /**
     * Checks the singleton LicenseManager for access to the given feature
     * and prints the result.
     */
    public void verifyAccess(String featureName) {
        LicenseManager lm = LicenseManager.getInstance();
        boolean allowed = lm.isFeatureUnlocked(featureName);
        String status = allowed ? "GRANTED" : "DENIED";
        System.out.println("[FeatureChecker] " + featureName + " -> " + status
                + "  (tier=" + lm.getTier() + ")");
    }
}
