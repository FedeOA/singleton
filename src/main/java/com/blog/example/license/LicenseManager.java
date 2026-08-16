package com.blog.example.license;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

// Singleton that centralises all license state for the application.
// Uses the "Bill Pugh" holder idiom for lazy, thread-safe initialisation
// without synchronisation overhead on every call.
public final class LicenseManager {

    // --- Singleton mechanics (Bill Pugh holder) ---

    private LicenseManager() {
        // private constructor prevents external instantiation
        this.unlockedFeatures = new LinkedHashSet<>();
        this.moduleHitCount   = new LinkedHashMap<>();
        this.activationDate   = null;
        this.licenseKey       = null;
        this.tier             = LicenseTier.NONE;
    }

    // The inner static class is loaded only when getInstance() is first called.
    private static final class Holder {
        private static final LicenseManager INSTANCE = new LicenseManager();
    }

    public static LicenseManager getInstance() {
        return Holder.INSTANCE;
    }

    // --- License tiers ---

    public enum LicenseTier {
        NONE, BASIC, PRO, ENTERPRISE
    }

    // --- State ---

    private String licenseKey;
    private LicenseTier tier;
    private LocalDate activationDate;
    private final Set<String> unlockedFeatures;
    private final Map<String, Integer> moduleHitCount;

    // --- Public API ---

    /**
     * Activates a license key. The key format determines the tier:
     *   contains "ENT"  -> ENTERPRISE
     *   contains "PRO"  -> PRO
     *   otherwise        -> BASIC
     */
    public void activateLicense(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("License key must not be blank");
        }
        this.licenseKey     = key;
        this.activationDate = LocalDate.now();

        String upper = key.toUpperCase();
        if (upper.contains("ENT")) {
            this.tier = LicenseTier.ENTERPRISE;
        } else if (upper.contains("PRO")) {
            this.tier = LicenseTier.PRO;
        } else {
            this.tier = LicenseTier.BASIC;
        }

        // Unlock features according to tier (cumulative)
        unlockedFeatures.clear();
        unlockedFeatures.add("REPORTING");
        if (tier.ordinal() >= LicenseTier.PRO.ordinal()) {
            unlockedFeatures.add("ADVANCED_ANALYTICS");
            unlockedFeatures.add("REAL_TIME_SYNC");
        }
        if (tier == LicenseTier.ENTERPRISE) {
            unlockedFeatures.add("AI_PREDICTIONS");
            unlockedFeatures.add("MULTI_TENANT");
        }

        System.out.println("License activated  -> key=" + licenseKey
                + "  tier=" + tier
                + "  features=" + unlockedFeatures);
    }

    /** Returns true when the named feature is unlocked by the current license. */
    public boolean isFeatureUnlocked(String featureName) {
        return unlockedFeatures.contains(featureName);
    }

    /** Increments the hit counter for a given module. */
    public void recordHit(String moduleName) {
        moduleHitCount.merge(moduleName, 1, Integer::sum);
    }

    public LicenseTier getTier() {
        return tier;
    }

    public Set<String> getUnlockedFeatures() {
        return Collections.unmodifiableSet(unlockedFeatures);
    }

    /** Prints a human-readable summary of the current license state. */
    public void printLicenseSummary() {
        System.out.println("--- License Summary ---");
        System.out.println("Key             : " + (licenseKey != null ? licenseKey : "(none)"));
        System.out.println("Tier            : " + tier);
        System.out.println("Activated on    : " + (activationDate != null ? activationDate : "N/A"));
        System.out.println("Features        : " + unlockedFeatures);
        System.out.println("Module hit map  : " + moduleHitCount);
        System.out.println("-----------------------");
    }
}
