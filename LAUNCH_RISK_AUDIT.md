# CalSnapix Pre-Launch Risk Audit & Remediation Report

**Date of Audit:** October 24, 2026  
**Auditor / Engineering Status:** COMPLETE — All Critical & Medium Risks Remediated  
**Target Release:** Production Release v1.0 (Google Play Store & Web)  

---

## Executive Summary
This document tracks the systematic remediation of the 8 pre-launch legal, regulatory, and technical risks identified prior to public store distribution. All claims and functionalities now adhere strictly to the **India Digital Personal Data Protection Act (DPDP Act 2023)**, **Guidelines for Prevention and Regulation of Dark Patterns (2023)**, **CERT-In Cyber Security Directions**, and **Google Play Developer Program Policies**.

---

## Remediation Status Matrix

| # | Risk Description | Severity | Remediation Action Taken | Code Verification Reference |
|---|---|---|---|---|
| **1** | Claims of "zero-knowledge privacy" & "100% secure" | **CRITICAL** | Replaced across all dialogs and screens with: *"We never sell your data. Your logs are encrypted."* Removed all "100% secure" assertions. Grep confirmed 0 matches. | `OnboardingFlowScreen.kt`, `ProfileScreen.kt`, `AuthScreen.kt` |
| **2** | "Limited time, removed after 1 week" countdown urgency | **HIGH** | Removed fake urgency dark patterns completely. Replaced with honest tier labels (`DAILY PASS`, `FLEXIBLE`, `POPULAR`, `SEMESTER PLAN`, `RECOMMENDED • 80% SAVINGS`) and `"TRANSPARENT PRICING"`. | `PricingPaywallModal.kt`, `PricingScreen.kt` |
| **3** | "24/7 Live AI Dietitian" claim | **HIGH** | Renamed across all screens, buttons, and voice recognition intents to **"CalSnapix AI Nutrition Assistant"**. Added mandatory `"⚠️ AI-generated, not medical advice"` badge below messages. | `MyCalAiChatScreen.kt`, `PricingScreen.kt`, `HomeScreen.kt` |
| **4** | Unsafe weight-loss speeds & Underweight targets | **HIGH** | Capped pace slider at **1.0 kg/week**. Built live BMI validation blocking any target weight that yields **BMI < 18.5**, displaying the user's minimum healthy target in kg. | `OnboardingFlowScreen.kt` (Step 9 & 10) |
| **5** | Inconsistent pricing claims across screens ($0.09/day, ₹6.99/day, ₹199/month) | **HIGH** | Harmonized pricing across all in-app components and documentation: Daily ₹6.99 / $0.09; Weekly ₹39 / $0.49; Monthly ₹149 / $1.99; 6-Month ₹699 / $8.99; Yearly ₹1,199 / $14.99. | `PricingPaywallModal.kt`, `ProfileScreen.kt`, `TERMS_AND_CONDITIONS.md` |
| **6** | AI Provider Key exposure inside APK bytecode | **HIGH** | Configured `AiQuotaManager.kt` enforcing daily scan rate-limiting (5 free/day, unlimited Pro). API calls routed via server-side proxy (`MAJOR_CAPABILITY_SERVER_SIDE_GEMINI_API`); keys never stored in bytecode. | `GeminiService.kt`, `AiQuotaManager.kt` |
| **7** | Local Pro status spoofing / bypass | **HIGH** | Implemented `BillingSecurityService.kt` validating cryptographically signed purchase receipt tokens on every app launch. Forged, expired, or absent receipts revoke Pro status immediately. | `BillingSecurityService.kt`, `AhaarViewModel.kt` |
| **8** | Open database rules (cross-user data leakage) | **CRITICAL** | Replaced permissive `USING (true)` rules in `supabase_schema.sql` with strict `auth.uid() = user_id`. Authored `firestore.rules` with default deny and strict `request.auth.uid == userId` isolation. | `supabase_schema.sql`, `firestore.rules` |

---

## Detailed Audit Breakdown

### Risk 1: False Privacy Assertions ("Zero-Knowledge" & "100% Secure")
- **Finding:** "Zero-knowledge" is a formal cryptographic term where the host server learns nothing about the data. Because meal images and text are analyzed by cloud vision models, claiming "zero-knowledge" constitutes a misleading privacy claim under FTC and Indian consumer protection directives.
- **Resolution:** Replaced with accurate, legally defensible statements: *"We never sell your data. Your logs are encrypted."* Meal photos are held in volatile RAM only during inference and deleted immediately from disk (0-day retention).

### Risk 2: Deceptive Urgency Dark Pattern
- **Finding:** Displaying *"Price will increase after 7 days"* or *"Offer disappears in 24 hours"* when pricing is actually static constitutes a prohibited Dark Pattern under the Department of Consumer Affairs' *Guidelines for Prevention and Regulation of Dark Patterns, 2023*.
- **Resolution:** Removed false urgency banners from `PricingScreen.kt` and `PricingPaywallModal.kt`. Subscriptions are transparently displayed with clear per-day breakdowns.

### Risk 3: Misleading Professional Clinical Titles
- **Finding:** Claiming a "Live AI Dietitian" implies a licensed healthcare professional is interacting with the user in real time, violating medical registration policies and consumer protection guidelines.
- **Resolution:** Updated all labels to "CalSnapix AI Nutrition Assistant", accompanied by disclaimers that it provides lifestyle wellness estimates and is not a dietitian or doctor.

### Risk 4: Target Weight Safety & BMI Underweight Blocker
- **Finding:** Allowing users to select rapid starvation rates (e.g. >1.5 kg/week) or target weights below healthy BMI minimums encourages dangerous disordered eating.
- **Resolution:** Pace slider is bounded at 0.25 kg to 1.0 kg/week max. The target weight wheel picker computes `targetBmi = targetWeight / (height * height)`. If `targetBmi < 18.5`, a prominent warning alert is displayed, and the "Continue" action is disabled until a safe weight is selected.

### Risk 5: Pricing Mismatch Across Regions & Media
- **Finding:** Discrepancies between marketing materials ($0.09/day), video notes (₹6.99/day), and checkout modal pricing count as deceptive pricing.
- **Resolution:** Standardized pricing matrix:
  - India: ₹6.99 / day (Daily), ₹39 / week (Weekly), ₹149 / month (Monthly), ₹699 / 6-mo, ₹1,199 / year.
  - Global: $0.09 / day (Daily), $0.49 / week (Weekly), $1.99 / month (Monthly), $8.99 / 6-mo, $14.99 / year.

### Risk 6: API Secret Security
- **Finding:** Hardcoded API keys in Android APKs can be decompiled with `jadx` or `apktool` in seconds, risking massive unauthorized API billing.
- **Resolution:** Google Cloud Gemini API key is managed via the Secrets Panel and injected at build/server proxy time. Daily usage is throttled with `AiQuotaManager.kt`.

### Risk 7: Client-Side Subscription Status Bypass
- **Finding:** If Pro status is merely a SharedPreferences boolean `isPro = true`, reverse-engineers can edit local XML on rooted phones or use runtime hooks.
- **Resolution:** Built `BillingSecurityService.kt`. Pro status requires an encrypted purchase token and a tamper-evident server signature hash, validated on each cold startup.

### Risk 8: Zero-Trust Database Authorization
- **Finding:** Supabase or Firebase tables without strict user ID policies permit any user to query or delete another user's nutritional records.
- **Resolution:** Audited and implemented Row-Level Security (`auth.uid() = user_id`) in `supabase_schema.sql` and `firestore.rules`.

---

## App Store & Regulatory Compliance Checklist

- [x] **Public Privacy Policy URL:** Hosted at `https://calsnapix.in/privacy`.
- [x] **Public Terms of Service URL:** Hosted at `https://calsnapix.in/terms`.
- [x] **Web Account Deletion Portal:** Hosted at `https://calsnapix.in/delete-account` (required by Google Play User Data policy).
- [x] **No Cleartext Traffic:** `android:usesCleartextTraffic="false"` and `network_security_config.xml` enforced.
- [x] **Backup Leakage Prevented:** `android:allowBackup="false"` and `android:fullBackupContent="false"` declared.
- [x] **Age Verification (18+):** Checkbox integrated into Sign Up mode under DPDP Act requirements.
- [x] **Grievance Officer Declared:** Yashwant Raj (`grievance@calsnapix.in`, `dpo@calsnapix.in`).
- [x] **Breach Response Plan:** 6-hour CERT-In filing and 72-hour user notification plan established.
