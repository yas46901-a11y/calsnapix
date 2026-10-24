# CalSnapix Security Incident & Data Breach Response Plan

**Document Version:** 1.0  
**Effective Date:** October 2026  
**Regulatory Compliance:** India Digital Personal Data Protection Act (DPDP Act 2023), CERT-In Cyber Security Directions (Section 70B), Google Play Security & Privacy Guidelines, EU GDPR Article 33/34.

---

## 1. Incident Response Team & Responsibilities

| Role | Designee / Title | Primary Responsibilities |
|---|---|---|
| **Incident Commander** | Lead Security Engineer | Coordinates triage, system isolation, forensics, and technical containment. |
| **Data Protection Officer (DPO)** | Compliance & Privacy Lead | Assesses impact on personal data, evaluates legal reporting triggers, logs breach evidence. |
| **Communications Lead** | Operations Lead | Drafts public notices, manages user email communications, and coordinates regulator filings. |
| **Engineering Lead** | Core Android & Backend Lead | Deploys hotfixes, patches vulnerabilities, revokes compromised tokens/keys. |

**Dedicated Security Contact:** `security@calsnapix.in`  
**Escalation Phone:** Monitored 24/7 on-call rotation.

---

## 2. Breach Response Workflow & Timelines

### Phase 1: Detection & Triage (Hour 0 – 2)
1. **Intake:** Automated anomaly alerts (e.g. rate-limit spikes, unauthorized token usage) or user report received.
2. **Severity Classification:**
   - **Critical (P1):** Exposure of user biometric data, credentials, or unauthorized database access.
   - **Major (P2):** Disruption to authentication or payment verification services.
   - **Minor (P3):** Isolated client-side logic error with no data exfiltration.
3. **Immediate Containment:**
   - Revoke affected API tokens, rotate database credentials, and block malicious IPs/subnets.
   - Isolate compromised server instances within 60 minutes.

### Phase 2: Forensics & Investigation (Hour 2 – 6)
- **Who Investigates:** Lead Security Engineer & Core Backend Lead.
- **Actions:**
  - Audit database query logs and authentication attempt logs.
  - Determine exact scope: which user IDs, emails, or records were impacted.
  - Verify that no plain passwords were breached (passwords are salted and hashed with SHA-256).
  - Confirm meal photos were uncompromised (meal photos are processed transiently in memory and deleted after inference).

### Phase 3: Regulatory & Authority Notification (Within 6 Hours)
- **CERT-In (Indian Computer Emergency Response Team):** In compliance with CERT-In directions under IT Act Section 70B, cybersecurity incidents meeting mandatory reporting types must be reported to `incident@cert-in.org.in` within **6 hours** of confirmation.
- **Data Protection Board of India (DPBI):** Formal notification filed specifying breach nature, impacted data categories, containment actions taken, and remedial advice for data principals.

### Phase 4: Affected User Notification (Within 72 Hours)
- **Who Emails Users:** Communications Lead via verified email (`privacy@calsnapix.in`).
- **Timing:** Sent without undue delay, and no later than **72 hours** post-discovery.
- **Required Contents:**
  1. Description of the incident and what categories of information were involved.
  2. Actions CalSnapix has executed to remediate the incident.
  3. Actionable guidance for users (e.g. password resets, monitoring accounts).
  4. Contact details for our Data Protection Officer for inquiries.

### Phase 5: Post-Incident Review & Hardening (Day 7)
- Root-cause analysis report prepared.
- Preventative controls added to automated security regression test suites.
- Executive summary preserved for compliance audits.
