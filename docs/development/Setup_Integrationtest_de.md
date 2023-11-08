# Setup für die Integrationstests

In diesem Abschnitt werden die benötigten Schritte beschrieben um die Integrationstests korrekt ausführen zu können.

## Voraussetzungen

* Es ist notwendig das Program `yarn` zu installieren

## Konfiguration

Damit alle Integrationstests auch einwandfrei funktionieren, müssen vorher einige Daten konfiguriert werden.

<!-- markdown-link-check-disable-next-line -->
**integrationTests/cypress.config.json** [[Link zur Datei](../../integrationTests/cypress.config.js)]

1) Es muss die base-URL auf das Hostsystem angepasst werden.
   Dafür muss das Feld `baseUrl` auf die Host-FQDN angepasst werden (`https://local.cloudogu.com`)
2) Es müssen noch weitere Aspekte konfiguriert werden.
   Diese werdeb als Umgebungsvariablen in der `cypress.config.json` gesetzt:
- `DoguName` - Bestimmt den Namen des jetzigen Dogus und wir beim Routing benutzt.
- `MaxLoginRetries` - Bestimmt die Anzahl der Loginversuche, bevor ein Test fehlschlägt.
- `AdminUsername` - Der Benutzername des CES-Admins.
- `AdminPassword` - Das Passwort des CES-Admins.
- `AdminGroup` - Die Benutzergruppe für CES-Administratoren.

Eine Beispiel-`cypress.json` sieht folgendermaßen aus:
```json
{
  "baseUrl": "https://192.168.56.2",
  "env": {
    "DoguName": "redmine",
    "MaxLoginRetries": 3,
    "AdminUsername":  "ces-admin",
    "AdminPassword":  "ecosystem2016",
    "AdminGroup":  "CesAdministrators"
  }
}
```

## Starten der Integrationstests

Die Integrationstests können auf zwei Arten gestartet werden:

1. Mit `yarn cypress run` starten die Tests nur in der Konsole ohne visuelles Feedback.
   Dieser Modus ist hilfreich, wenn die Ausführung im Vordergrund steht.
   Beispielsweise bei einer Jenkins-Pipeline.

1. Mit `yarn cypress open` startet ein interaktives Fenster, wo man die Tests ausführen, visuell beobachten und debuggen kann.
   Dieser Modus ist besonders hilfreich bei der Entwicklung neuer Tests und beim Finden von Fehlern.
