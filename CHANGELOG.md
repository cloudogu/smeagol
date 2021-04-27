# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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
