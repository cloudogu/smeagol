---
title: "Setup für die Integrationstests"
---

# Setup für die Integrationstests

In diesem Abschnitt werden die benötigten Schritte beschrieben um die Integrationstests für Smeagol korrekt ausführen zu können.

## Voraussetzungen

* Es ist notwendig [yarn](https://classic.yarnpkg.com/en/docs/install#debian-stable) zu installieren:
    *  `npm install --global yarn`

## Konfiguration

Damit alle Integrationstests auch einwandfrei funktionieren, müssen vorher einige Daten konfiguriert werden.

**integrationTests/cypress.json** [[Link zur Datei](../../integrationTests/cypress.json)]

In dieser Datei muss die base-URL auf das Hostsystem angepasst werden.
Dafür muss das Feld `baseUrl` auf die Host-FQDN angepasst werden (`https://local.cloudogu.com`)

**integrationTests/cypress/fixtures/ces_admin_data.json** [[Link zur Datei](../../integrationTests/cypress/fixtures/ces_admin_data.json)]

In der `userdata.json` müssen die Login-Information eines CES-Admin in den Feldern `username` und `password` eingetragen werden.

## Starten der Integrationstests

Die Integrationstests können auf zwei Arten gestartet werden:

1. Mit `yarn cypress run` starten die Tests nur in der Konsole ohne visuelles Feedback.
   Dieser Modus ist hilfreich, wenn die Ausführung im Vordergrund steht.
   Beispielsweise bei einer Jenkins-Pipeline.

1. Mit `yarn cypress open` startet ein interaktives Fenster, wo man die Tests ausführen, visuell beobachten und debuggen kann.
   Dieser Modus ist besonders hilfreich bei der Entwicklung neuer Tests und beim Finden von Fehlern.
