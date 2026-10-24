# CalSnapix Privacy Policy

**Effective Date:** October 24, 2026  
**Last Updated:** October 24, 2026  
**Applicable Legal Entity:** CalSnapix Technologies Private Limited  
**Registered Office Address:** 4th Floor, Salarpuria Citadel, 80 Feet Road, Koramangala 4th Block, Bengaluru, Karnataka, India - 560001  
**Public Website URL:** https://calsnapix.in/privacy  
**Web Account Deletion Portal:** https://calsnapix.in/delete-account  

---

## 1. Introduction & Scope
CalSnapix Technologies Private Limited ("CalSnapix", "we", "our", or "us") operates the CalSnapix mobile application and related web services. We are committed to protecting your personal health and nutrition privacy. This Privacy Policy details how we collect, process, protect, and erase your information in full compliance with:
- The **Digital Personal Data Protection Act, 2023 (DPDP Act)** (India)
- The **Information Technology Act, 2000** and IT Rules 2011 (Reasonable Security Practices)
- **CERT-In Cyber Security Directions (Section 70B)**
- **Google Play Developer Program Policies** & Health/Personal Data Guidelines
- International privacy standards where applicable (including EU GDPR principles).

---

## 2. Information We Process
We collect only the minimum personal data strictly necessary to provide personalized Indian nutrition, calorie estimation, and hydration tracking:

1. **Account Credentials & Identity:**
   - Name and verified Email Address.
   - Authentication tokens via Google Sign-In (Credential Manager) or salted, SHA-256 hashed password hashes. *We never store plain text passwords.*
   - Confirmation of 18+ age status (mandatory under the India DPDP Act).
2. **Biometric & Physical Metrics:**
   - Biological gender, current height (cm/ft), current weight (kg/lbs), target weight, age, and estimated activity factor.
   - Used strictly to calculate Basal Metabolic Rate (BMR) and Total Daily Energy Expenditure (TDEE).
3. **Meal Captures & Dietary Preferences:**
   - Vegetarian, Eggetarian, Jain, Non-Veg, or Vegan dietary preferences.
   - Self-reported allergies (e.g. nuts, dairy, gluten, shellfish).
   - Indian regional cuisine preferences (North, South, East, West, Pan-Indian).
   - Barcode scans and food text descriptions.
4. **Referral & Discount Identifiers:**
   - Referral codes and linked public Instagram handles (used on an honor check to validate the `INSTA20` single-use discount). *We never ask for or access your Instagram password or private messages.*

---

## 3. Meal Photo Lifecycle & Zero-Retention Policy
- **Transient In-Memory Processing:** When you take a photo of your meal or snack, the image is held transiently in volatile device RAM solely to send to our vision model endpoint for ingredient and portion estimation.
- **Immediate Local & Cloud Deletion:** **Meal photos are retained for 0 days.** Temporary cache files (`meal_scan_*.jpg`) on your device are deleted immediately once nutritional estimates are returned or discarded.
- **No Model Training:** Uploaded meal snapshots are discarded immediately after inference and are **never** stored in permanent cloud buckets or used for public AI model training.
- **No Biometric Identification:** The camera is restricted to food and barcode detection. We do not perform facial recognition, iris scanning, or physical biometric identification.

---

## 4. AI Service Provider Disclosure
- **AI Processing Provider:** We utilize **Google Gemini Vision API (Google Cloud AI)** hosted in secure, enterprise-grade data centers.
- **Server-Side API Key Security:** AI API keys are held exclusively on secure server-side infrastructure and are never embedded into client application APK bytecode.
- **Per-User Quotas:** Daily meal scans are monitored and throttled per user account (5 scans/day on Free tier, unlimited on verified Pro) to prevent bill runaway and server resource exhaustion.

---

## 5. Security Architecture & Standards
We implement defense-in-depth security measures to protect your health records:
- **Encrypted Local Storage:** Local user preferences and caching utilize hardware-backed **AES-256-GCM** encryption managed via the **Android Keystore System**. Cryptographic keys are never hardcoded in source code.
- **Cloud Backup Leakage Disabled:** `android:allowBackup="false"` and `android:fullBackupContent="false"` are enforced in the Android manifest so sensitive health metrics do not leak into unencrypted cloud device backups.
- **HTTPS/TLS 1.3 Strict Enforcement:** Non-HTTPS cleartext traffic is blocked across the application (`android:usesCleartextTraffic="false"`).
- **Brute-Force Login Lockout:** Accounts are temporarily locked for 5 minutes after 5 consecutive failed login attempts.
- **Zero-Trust Database Row-Level Security (RLS):** Database records in PostgreSQL/Supabase and Firestore require authenticated identity matching (`auth.uid() = user_id`). No user can view or modify another user's meals or biometrics.

---

## 6. Data Ownership, Retention & Account Deletion
You retain 100% ownership of your data at all times.

### How to Delete Your Account:
1. **In-App Deletion:** Navigate to **Profile > Account Settings > Delete Account**. Tap "Permanently Delete My Account & Wipe Data".
2. **Web Deletion Portal:** Visit **https://calsnapix.in/delete-account**, enter your registered email address, verify with one-time security code (OTP), and request instant erasure.

### What Happens on Deletion:
- Your user profile, biometric statistics, goal targets, daily logs, scanned meals, and coupon records are permanently and irreversibly purged from our active databases within **24 hours**.
- Temporary file caches and local database SQLite records are wiped immediately.
- Encrypted disaster-recovery backups are overwritten in the ordinary course of business within **30 days**.

---

## 7. Data Breach Notification Protocol
In the event of a confirmed security incident impacting personal data:
- **CERT-In (Indian Computer Emergency Response Team):** Reported to `incident@cert-in.org.in` within **6 hours** of confirmation, pursuant to CERT-In Cyber Security Directions under IT Act Section 70B.
- **Affected Users:** Notified via registered email without undue delay, and no later than **72 hours**, detailing the nature of the event, affected data fields, immediate containment measures taken, and recommended user precautions.
- **Data Protection Board of India:** Formal breach disclosure submitted in accordance with Section 8(6) of the DPDP Act 2023.

---

## 8. Grievance Officer & Contact Information
Under the Information Technology (Intermediary Guidelines and Digital Media Ethics Code) Rules, 2021 and the Digital Personal Data Protection Act, 2023, the designated Grievance Officer and Data Protection Officer for CalSnapix is:

- **Designated Grievance Officer:** Yashwant Raj (Lead Grievance & Compliance Officer)  
- **Email:** `grievance@calsnapix.in`  
- **Data Protection Officer (DPO):** `dpo@calsnapix.in`  
- **General Privacy Enquiries:** `privacy@calsnapix.in`  
- **Physical Mailing Address:**  
  CalSnapix Technologies Private Limited  
  Attn: Grievance Officer  
  4th Floor, Salarpuria Citadel, 80 Feet Road, Koramangala 4th Block,  
  Bengaluru, Karnataka, India - 560001  

We acknowledge grievances within **24 hours** and resolve them within **15 days** as mandated by Indian law.
