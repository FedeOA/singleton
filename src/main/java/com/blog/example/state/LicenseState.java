package com.blog.example.state;

import com.blog.example.license.LicenseManager;

import java.time.LocalDate;
import java.util.Set;

// --- Immutable license state ---
public record LicenseState(
        String licenseKey,
        LicenseManager.LicenseTier tier,
        LocalDate activationDate,
        Set<String> unlockedFeatures
) {
}
