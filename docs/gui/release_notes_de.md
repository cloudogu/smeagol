# Release Notes

Im Folgenden finden Sie die Release Notes für Smeagol. 

Technische Details zu einem Release finden Sie im zugehörigen [Changelog](https://docs.cloudogu.com/de/docs/dogus/smeagol/CHANGELOG/).

## [Unreleased]
### Changed
- Migration des Editors von tui-editor v1 zu @toast-ui/editor v3
### Fixed
- Aufgabenlisten (`* [ ] ...`) zeigen echte Checkboxen an
- PlantUML-Diagramme (mit `@startuml/@enduml`-Tags und der älteren Code-Block-Syntax) werden gerendert
- Farben können im Editor ausgewählt werden

## [v1.7.8-6] - 2026-03-11
- Wir haben nur technische Änderungen vorgenommen. Näheres finden Sie in den Changelogs.

## [v1.7.8-5] - 2026-02-18
- Wir haben nur technische Änderungen vorgenommen. Näheres finden Sie in den Changelogs.

## [v1.7.8-4] - 2026-02-13
- Wir haben nur technische Änderungen vorgenommen. Näheres finden Sie in den Changelogs.

## [v1.7.8-3] - 2026-01-29
- Wir haben nur technische Änderungen vorgenommen. Näheres finden Sie in den Changelogs.

## [v1.7.8-2] - 2025-04-28
### Changed
- Die Verwendung von Speicher und CPU wurden für die Kubernetes-Multinode-Umgebung optimiert.

## [v1.7.8-1] - 2025-04-08
- Wir haben nur technische Änderungen vorgenommen. Näheres finden Sie in den Changelogs.

## [v1.7.7-1] - 2025-03-13
- Das Design der Fehlerseiten wurde überarbeitet
- Es wurde ein Fehler behoben, der aufgetreten ist, wenn ein ungültiges CAS-Service-Ticket verwendet wurde

## [v1.7.6-2] - 2025-02-12
- Wir haben nur technische Änderungen vorgenommen. Näheres finden Sie in den Changelogs.

## [v1.7.6-1] - 2025-01-10[release_notes_en.md](release_notes_en.md)
- Wir haben nur technische Änderungen vorgenommen. Näheres finden Sie in den Changelogs.

## 1.7.5-1
- Wir haben nur technische Änderungen vorgenommen. Näheres finden Sie in den Changelogs.

## 1.7.4-1
Teile der Applikation haben Anfragen an Google-Analytics geschickt. Diese Anfragen wurden dauerhaft entfernt.
Weitere Informationen dazu sind auf der Seite der Bibliothek zu finden, die bisher diese Anfragen verschickt hat: https://github.com/nhn/tui.editor/tree/v1.4.0?tab=readme-ov-file#collect-statistics-on-the-use-of-open-source

## 1.7.3-4
Wir haben nur technische Änderungen vorgenommen. Näheres finden Sie in den Changelogs.

## 1.7.3-3
- Die Cloudogu-eigenen Quellen werden von der MIT-Lizenz auf die AGPL-3.0-only relizensiert.

## 1.7.3-2
* Behebung eines kritischen CVEs CVE-2024-41110 in Bibliotheksabhängigkeiten. Diese Schwachstelle konnte in Smeagol jedoch nicht aktiv ausgenutzt werden.

## 1.7.3-1

**Das Release behebt einen [DoS-Angriffsvektor](https://security.snyk.io/vuln/SNYK-JAVA-COMFASTERXMLJACKSONCORE-7569538). Ein Update ist daher empfohlen.**

Wir haben nur technische Änderungen vorgenommen. Näheres finden Sie in den Changelogs.

## 1.7.2-2

Wir haben nur technische Änderungen vorgenommen. Näheres finden Sie in den Changelogs.

## 1.7.2-1

**Das Release behebt eine kritische Sicherheitslücke ([CVE-2022-31129](https://nvd.nist.gov/vuln/detail/CVE-2022-31129)). Ein Update ist daher empfohlen.**

Wir haben nur technische Änderungen vorgenommen. Näheres finden Sie in den Changelogs.
