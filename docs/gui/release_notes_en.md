# Release Notes

Below you will find the release notes for Smeagol. 

Technical details on a release can be found in the corresponding [Changelog](https://docs.cloudogu.com/en/docs/dogus/smeagol/CHANGELOG/).

## [Unreleased]
### Changed
* We have only made technical changes. You can find more details in the changelogs.

## [v1.7.8-4] - 2026-02-13
### Security
- Fix CVE-2025-68121

## [v1.7.8-3] - 2026-01-29

### Security
- [#249] fixed [cve-2025-15467](https://avd.aquasec.com/nvd/2025/cve-2025-15467/)

## [v1.7.8-2] - 2025-04-28

### Changed
- Usage of memory and CPU was optimized for the Kubernetes Mutlinode environment.

## [v1.7.8-1] - 2025-04-08

### Security

* This release fixes the critical security vulnerability [CVE-2025-24813](https://nvd.nist.gov/vuln/detail/CVE-2025-24813).
  An update is therefore recommended.

### Changed

* We have only made technical changes. You can find more details in the changelogs.

## [v1.7.7-1] - 2025-03-13

- The design of the error pages has been revised
- Fixed an error that occurred when an invalid CAS service ticket was used

## [v1.7.6-2] - 2025-02-12

We have only made technical changes. You can find more details in the changelogs.

## [v1.7.6-1] - 2025-01-10
**The release fixes a critical security vulnerability ([CVE-2024-56337](https://github.com/advisories/GHSA-27hp-xhwr-wr2m)). An update is therefore recommended.**

We have only made technical changes. You can find more details in the changelogs.

## 1.7.5-1

**The release fixes a critical security vulnerability ([CVE-2024-52316](https://github.com/advisories/GHSA-xcpr-7mr4-h4xq)). An update is therefore recommended.**

We have only made technical changes. You can find more details in the changelogs.

## 1.7.4-1
Parts of the application have sent requests to Google Analytics. These requests have been permanently removed.
Further information can be found on the page of the library that previously sent these requests: https://github.com/nhn/tui.editor/tree/v1.4.0?tab=readme-ov-file#collect-statistics-on-the-use-of-open-source

## 1.7.3-4
We have only made technical changes. You can find more details in the changelogs.

## 1.7.3-3
- Relicense own code to AGPL-3-only

## 1.7.3-2
* Fix of critical CVE CVE-2024-41110 in library dependencies. This vulnerability could not be actively exploited in Smeagol, though.

## 1.7.3-1

**The release fixes a [DoS attack vector](https://security.snyk.io/vuln/SNYK-JAVA-COMFASTERXMLJACKSONCORE-7569538). An update is therefore recommended.**

We have only made technical changes. You can find more details in the changelogs.

## 1.7.2-2

We have only made technical changes. You can find more details in the changelogs.

## 1.7.2-1

**The release fixes a critical security vulnerability ([CVE-2022-31129](https://nvd.nist.gov/vuln/detail/CVE-2022-31129)). An update is therefore recommended.**

We have only made technical changes. You can find more details in the changelogs.
