package com.blog.example.license;

import com.blog.example.state.LicenseState;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// Singleton that centralises all license state for the application.
// Uses the "Bill Pugh" holder idiom for lazy, thread-safe initialisation
// without synchronisation overhead on every call.
public final class LicenseManager {

    // --- Singleton mechanics (Bill Pugh holder) ---

    private LicenseManager() {
        this.licenseState = new LicenseState(
                null,
                LicenseTier.NONE,
                null,
                Set.of()
        );

        this.moduleHitCount = new ConcurrentHashMap<>();
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

    /*
     * The complete license state is replaced atomically when the license
     * is activated. The state itself is immutable.
     */
    private volatile LicenseState licenseState;

    /*
     * ConcurrentHashMap allows multiple threads to update different
     * modules concurrently.
     *
     * AtomicInteger guarantees that increments are atomic.
     */
    private final Map<String, AtomicInteger> moduleHitCount;

    // --- Public API ---

    /**
     * Activates a license key. The key format determines the tier:
     *   contains "ENT"  -> ENTERPRISE
     *   contains "PRO"  -> PRO
     *   otherwise        -> BASIC
     */
    public void activateLicense(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException(
                    "License key must not be blank"
            );
        }

        String upper = key.toUpperCase();

        LicenseTier newTier;

        if (upper.contains("ENT")) {
            newTier = LicenseTier.ENTERPRISE;
        } else if (upper.contains("PRO")) {
            newTier = LicenseTier.PRO;
        } else {
            newTier = LicenseTier.BASIC;
        }

        // Build the complete feature set before publishing the new state.
        Set<String> features = new LinkedHashSet<>();

        features.add("REPORTING");

        if (newTier == LicenseTier.PRO
                || newTier == LicenseTier.ENTERPRISE) {

            features.add("ADVANCED_ANALYTICS");
            features.add("REAL_TIME_SYNC");
        }

        if (newTier == LicenseTier.ENTERPRISE) {
            features.add("AI_PREDICTIONS");
            features.add("MULTI_TENANT");
        }

        // Make the Set immutable before publishing it.
        Set<String> immutableFeatures =
                Collections.unmodifiableSet(features);

        /*
         * Create the complete state first.
         *
         * Because licenseState is volatile, publishing this reference
         * makes the complete state visible to other threads.
         */

        this.licenseState = new LicenseState(
                key,
                newTier,
                LocalDate.now(),
                immutableFeatures
        );

        System.out.println(
                "License activated -> tier="
                        + newTier
                        + " features="
                        + immutableFeatures
        );
    }

    /**
     * Returns true when the named feature is unlocked
     * by the current license.
     */
    public boolean isFeatureUnlocked(String featureName) {
        return licenseState.unlockedFeatures()
                .contains(featureName);
    }

    /**
     * Increments the hit counter for a given module.
     */
    public void recordHit(String moduleName) {

        moduleHitCount
                .computeIfAbsent(
                        moduleName,
                        key -> new AtomicInteger()
                )
                .incrementAndGet();
    }

    public LicenseTier getTier() {
        return licenseState.tier();
    }

    public Set<String> getUnlockedFeatures() {
        return licenseState.unlockedFeatures();
    }

    /**
     * Prints a human-readable summary of the current license state.
     */
    public void printLicenseSummary() {

        LicenseState state = licenseState;

        System.out.println("--- License Summary ---");
        System.out.println("Key             : " + (state.licenseKey() != null ? state.licenseKey() : "(none)"));
        System.out.println("Tier            : " + state.tier());
        System.out.println("Activated on    : " + (state.activationDate() != null ? state.activationDate() : "N/A"));
        System.out.println("Features        : " + getUnlockedFeatures());
        System.out.println("Module hit map  : " + moduleHitCount);
        System.out.println("-----------------------");
    }
}
