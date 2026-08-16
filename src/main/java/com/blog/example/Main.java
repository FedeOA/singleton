package com.blog.example;

import com.blog.example.license.LicenseManager;
import com.blog.example.client.FeatureChecker;
import com.blog.example.client.UsageReporter;

// Demonstrates the Singleton pattern through a software license manager.
// Multiple parts of the application access the same LicenseManager instance
// to verify features, register modules, and report usage.
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Software License Manager — Singleton Demo ===");
        System.out.println();

        // Retrieve the singleton from two different places and prove identity
        LicenseManager first  = LicenseManager.getInstance();
        LicenseManager second = LicenseManager.getInstance();

        System.out.println("first  instance hash: " + System.identityHashCode(first));
        System.out.println("second instance hash: " + System.identityHashCode(second));
        System.out.println("Same object? " + (first == second));
        System.out.println();

        // Activate a license key that unlocks certain feature tiers
        first.activateLicense("ACME-PRO-2024-XK9R");
        System.out.println();

        // A feature-checking component queries the singleton
        FeatureChecker checker = new FeatureChecker();
        checker.verifyAccess("REPORTING");
        checker.verifyAccess("ADVANCED_ANALYTICS");
        checker.verifyAccess("REAL_TIME_SYNC");
        checker.verifyAccess("AI_PREDICTIONS");
        System.out.println();

        // Register module usage through the singleton
        UsageReporter reporter = new UsageReporter();
        reporter.recordModuleUsage("REPORTING");
        reporter.recordModuleUsage("REPORTING");
        reporter.recordModuleUsage("ADVANCED_ANALYTICS");
        reporter.recordModuleUsage("REAL_TIME_SYNC");
        System.out.println();

        // Print a summary from the singleton
        second.printLicenseSummary();
    }
}
