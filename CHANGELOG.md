# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Changed
* Update version of the Java base image to version 8u252-1 (#96)
* Update version of the OpenJDK image for building Smeagol to version 8u252-jdk (#96)
* Update maven dependencies to fix potential security vulnerabilities. (#96)

## [v0.5.6-1] - 2020-12-14
### Added
* The ability to configure the memory limit for the container via `cesapp edit-conf` (#93)
* The ability to configure the `MaxRamPercentage` and `MinRamPercentage` for the Smeagol process inside the container via `cesapp edit-conf`  (#93)
* NAME and current VERSION to Dockerfile