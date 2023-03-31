# Architektur

## Bounded Contexts

Smeagol besteht aus Bounded Contexts (BC) im Sinne des Domain-Driven Design (DDD), die sich im Idealfall nichts teilen.
Dies verspricht die Trennung von fachlichen und technischen Belangen. Es unterteilt die Komplexität in verschiedene Bounded Contexts.

Da das Konto in allen BCs aus der Session gelesen werden muss, vermeiden wir eine Duplizierung, indem wir einen Shared Kernel verwenden.

![Bounded Contexts](assets/boundedcontexts.svg)

### Shared Kernel

Der Shared Kernel hostet Klassen, die von jedem BC benötigt werden.

### Authc

Dieser BC ist für den gesamten Prozess der Authentifizierung zuständig.

### Repository

Der Repository BC ist für die Auflistung der verfügbaren Repositories und deren Zweige zuständig.

### Wiki

Der Wiki BC ist das Herzstück von Smeagol und ist für das Wiki selbst zuständig. Dieser Teil wurde bereits mit [Gollum](https://github.com/gollum/gollum) realisiert.

## Hexagonale Architektur

In jedem Bounded Context verwenden wir unsere Interpretation einer hexagonalen Architektur (auch bekannt als [Clean Architecture, Onion Architecture, etc.](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html)) mit den folgenden Schichten:

* Domain: Enthält nur Domänenlogik (keine technischen Dinge). Definiert Wertobjekte, Entitäten, Repository *interfaces* und Dienste.
* Anwendungsfälle: Stellt Aktionen (Schreiboperationen) dar, die auf der Domäne möglich sind.
  Wir verwenden hier das Command Bus-Muster. Vorteil: Befehle bieten einen Überblick über die vom System erlaubten Domänenoperationen.
  Sie werden mit einem konkreten Muster implementiert und sind nicht einfach nur ein weiterer "Dienst".
* Infrastruktur: Enthält die ganzen technischen Dinge: REST-Controller, Repository *Implementierungen*.

Der Zugriff ist nur in der folgenden Richtung erlaubt: Infrastructure -> Use cases -> Domain

## UI-Architektur

Die UI verwendet eine [Flux-basierte Architektur] (https://facebookarchive.github.io/flux/), die mit [Redux] (https://redux.js.org/) aufgebaut ist.
Der Code ist in einen Shared Kernel und die BC's analog zur REST API aufgeteilt. Der gemeinsam genutzte Kernel besteht nur aus
Komponenten, die in jedem BC benötigt werden, etwas Infrastruktur und Bootstrap-Logik. Jeder BC ist in Komponenten unterteilt,
Container und Module.

* Komponenten sollten zustandslos sein (oder nur einen lokalen Zustand haben). Das bedeutet, jede benötigte Eigenschaft
  muss von einer übergeordneten intelligenten Komponente übergeben werden.
* Container sind übergeordnete Komponenten und sind die Komponenten, die mit dem Speicher verbunden sind.
* __Module__ sind das letzte Teil des Puzzles. Module sind für den Zustand der Anwendung verantwortlich. Sie verwalten die
  Aktionen und Reduzierer, die zur Änderung des Zustands erforderlich sind.

Der Zugriff ist nur in der folgenden Richtung erlaubt: Containers -> Components -> Modules

## Mapping auf Code

### Java (src/main/java)

* Shared Kernel: Basispaket `com.cloudogu.smeagol`
  Das Basispaket enthält auch den Einstiegspunkt unserer Anwendung.
* Bounded Contexts bilden auf Unterpakete des Basispakets ab, z.B.
  * `com.cloudogu.smeagol.authc`
  * `com.cloudogu.smeagol.repository`
* Die hexagonalen Ebenen entsprechen den Unterpaketen der einzelnen Bounded Context-Pakete, z. B.
  * `com.cloudogu.smeagol.repository.infrastructure`
  * `com.cloudogu.smeagol.repository.domain`

### JavaScript (src/main/js)

* Shared Kernel: Die .js-Dateien direkt unter js
* Bounded Contexts bilden auf Ordner ab, z.B.:
  * `repository`
  * `wiki`
* Container, Komponenten und __Module__ werden auf Unterordner jedes Bounded Contexts abgebildet.

### Fachbegriffe

* Repository: Repository im Sinne von DDD für den Zugriff -> Hinweis: Wir haben auch ein Domain-Objekt namens Repository, das auf ein Git- oder SCM-Manager-Repository verweist
* Controller: REST-Controller, d.h. Endpunkt (gibt Ressourcen zurück).
* Ressource: DTO, das die Entität auf die REST-Schnittstelle abbildet (wird von einem Controller zurückgegeben)


Übersetzt mit www.DeepL.com/Translator (kostenlose Version)
