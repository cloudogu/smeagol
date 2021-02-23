# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed
* Use React Query instead of Redux to manage server-state #100

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