# Smeagol Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Changed
- [#231] Update spring-boot-starter-parent to 3.3.5 to prevent CVE-2024-52316

## [v1.7.4-1] - 2024-10-22
### Fixed
- [#229] Disable Google Analytics in Markdown-Editor

## [v1.7.3-4] - 2024-09-25
### Changed
- Switch to new CAS service account structure in dogu.json

## [v1.7.3-3] - 2024-09-18
### Changed
- Relicense to AGPL-3.0-only

## [v1.7.3-2] - 2024-08-06
### Changed
- update OpenJDK to 21.0.4
- update Alpine base image to 3.20.2-1

### Security
- fix CVE-2024-41110 (#222)

## [v1.7.3-1] - 2024-08-05

### Changed
- [#220] Update base image to java:21.0.3-4
- [#220] Update Sprint Boot Starter to 3.3.2
- [#220] Update Spring to 6.1.11
- [#220] Update CAS-Client to 4.0.4
- [#220] Update Maven to 3.9.8
- [#220] Update JAXB to 2.3.1
- [#220] Update Snakeyaml to 2.2
- [#220] Update Guava to 33.2.1-jre
- [#220] Update slf4j to 2.0.13
- [#220] Update Logback to 1.5.6
- [#220] Update cloudogu/VersionName to 2.1.0
- [#220] Update jakarta.servlet-api to 6.1.0
- [#220] Update httpclient5 to 5.3.1
- [#220] Update NodeJs dev-server to 22.5.1
- [#220] Update Yarn to 1.22.22
- [#220] Update Jacoco to 0.8.12

### Fixed
- [#220] use pinned version of jetbrains annotations 24.1.0
- [#220] fix [Fasterxml DoS vulnerability](https://security.snyk.io/vuln/SNYK-JAVA-COMFASTERXMLJACKSONCORE-7569538)

## [v1.7.2-2] - 2024-07-01
### Changed
- Update base image to java:17.0.11-3 to use doguctl v0.12.0 (#92)

## [v1.7.2-1] - 2023-11-10
### Changed
- [#211] Target javascript version to es6
- Update used version of nodejs to 18.7.0
- Update used version of yarn to 1.22.19
- Update java base image to v17.0.9-1

### Fixed
- [#211] Fix integration tests after upgrading CAS
- [#213] Eliminate CVEs by switching from the deprecated lib momentjs to dayjs

## [v1.7.1-1] - 2023-03-31
### Fixed
- [#209] Fix execution of tests in build process

## [v1.7.0-1] - 2023-03-31
### Fixed
- [#207] Eliminate CVEs by upgrading various packages:
  - Update to Java 17
  - Spring Boot Starter to 3.0.5
  - Snakeyaml to 2.0.0
  - JGit to 5.1.16
  - Jasig (now Apereo) CAS Client to 4.0.1
  - SLF4J to 2.0.5
  - logback-classic to 1.4.6

## [v1.6.2-3] - 2023-01-23
### Changed
- Move to development apps in warp menu (#203)

## [v1.6.2-2] - 2022-04-11
### Changed
- Upgrade spring boot to version 2.6.5 to fix CVE-2022-22950 (#186)
- Upgrade packages to fix CVE-2018-25032; #185

## [v1.6.2-1] - 2022-01-05
### Changed
- Show interactive elements (new page, rename page, history, settings, branch dropdown) in the same place (#173)
  - this renders a more continuous display of interaction elements
- Update images in UI guide documentation

## [v1.6.1-1] - 2021-11-30
### Changed
- Use word-break and hyphens in toc and pageviewer so that long words get wrapped #169

## [v1.6.0-1] - 2021-08-17
### Changed
* Use proxy tickets instead of clearpass #157

## [v1.5.0-1] - 2021-07-08
### Changed
- Changed position of the table of contents to the left side (sticky) (#161)

## [v1.4.0-1] - 2021-06-29
### Added
- Log level can be configured now. Default log level is ```WARN```. (#159)

## [v1.3.5-1] - 2021-06-09
### Fixed
- wrong encoding of credentials by using the `smeagol` user-agent with the scm that enforces UTF-8 encoding (#144)

## [v1.3.4-1] - 2021-06-01
### Fixed
- Occasional wrong caching of translations by integrating the translations into the webpack bundle (#151)

## [v1.3.3-1] - 2021-05-28
### Fixed
* Prevent saving changes when commit message is invalid (#139)
* Commit gets reverted in case of a failed push (#142)
* Users with special characters in their passwords are now able to see repositories (#144)
* Fix a search error that can occur when searching long texts (#145)
* Improved pagination of the branch selection box. (#140)

## [v1.3.2-1] - 2021-05-17
### Changed
* Complement user docu with more recent screenshots and documentation of new features (#131)

### Fixed
* Prevent page refresh when creating a new page (#137)

## [v1.3.1-1] - 2021-04-27
### Changed
* Validate configuration in repository settings (#129)

## [v1.3.0-1] - 2021-04-22
### Added
* Option to init smeagol wiki #3
* Settings menu to change wiki paths

## [v1.2.0-1] - 2021-04-14
### Added
* Option to restore unsaved changes (#125)
* Support for `main` branches

## [v1.1.0-1] - 2021-04-08
### Added
* On every doc-page the user can now change the branch (#123).

### Fixed
* The test coverage is now displayed correctly in SonarQube. (#116)

## [v1.0.1-1] - 2021-03-25
### Changed
* Updates the appearance of tables inside the markdown editor (#117)

### Fixed
* Invalid pagename in new and rename page dialogue (#120)

## [v1.0.0-1] - 2021-03-22
### Added
* Generate a table of contents on each wiki page (#113)
  * ToC entries are generated over markdown headlines per wiki page
* Integration tests for CAS (#110)

### Changed
* Display only repositories with enabled wiki (`.smeagol.yml` file) in the repository overview (#112)
  * the remaining repositories can be displayed by clicking a checkbox
  * this requires that the [Smeagol Integration plugin](https://www.scm-manager.org/plugins/scm-smeagol-plugin/) is installed in the SCM Manager

## [v0.7.0-1] - 2021-03-11
### Added
* Add XSS attack mitigation by updating the WYSIWYG editor component (#106)
* Add repository name to the breadcrumb for easier orientation (#25)

### Changed
* Introduce a lighter menu UI styling, so the user can focus even more on the documentation tasks (#25)
  * support the user for an intuitive navigation with a clickable page breadcrumbs
  * add icons to the main buttons for easier visual recognition 
  * move the search bar to the top to support focussing on the wiki page content
* Change structure of Smeagol API results
  * `/api/v1/repositories` now returns `{"_embedded":{"repositories":[<repositories>]}` instead of `[<repositories>]`
  * `/api/v1/repositories/{repositoryId}/branches/{branch}/search` now returns `{"_embedded":{"searchResults":[<searchResults>]}` instead of `[<searchResults>]`
* Encode search results to prevent rendering of HTML in search results (#106)
* Update favicons (#108)
* Update to Java 11

## [v0.6.0-1] - 2021-03-03
### Changed
* Use React Query instead of Redux to manage server-state (#100)
* Use TypeScript instead of Flow for type checking

## [v0.5.7-1] - 2021-02-12
### Added
* Health check in Dockerfile (#96)
* Timestamps in the log of Jenkins job (#96)
* Goss Spec to validate server configuration (#96)
* Lint of the dockerfile and checking of shell scripts (#96)
* Automatic release possibility (#96)
* Test option for Dogu upgrades in Jenkins job. (#96)

### Changed
* Update version of the Java base image to version 8u252-1 (#96)
* Update version of the OpenJDK image for building Smeagol to version 8u252-jdk (#96)
* Update Java script libraries and maven dependencies to fix potential security vulnerabilities (#96)
* Jenkins builds the complete Dogu and not only the smeagol.war. Building the source code and building the Docker image are done in parallel. (#96)

## [v0.5.6-1] - 2020-12-14
### Added
* The ability to configure the memory limit for the container via `cesapp edit-conf` (#93)
* The ability to configure the `MaxRamPercentage` and `MinRamPercentage` for the Smeagol process inside the container via `cesapp edit-conf`  (#93)
* NAME and current VERSION to Dockerfile
