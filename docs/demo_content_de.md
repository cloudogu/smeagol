# Demo-Inhalte

Diese Seite enthält Strukturen und Inhalte, die für die Vorbereitung von Benutzerhandbuch-Screenshots verwendet werden können. 

## SCM-Manager

Ein Repository anlegen:
- Namespace: `documentation`
- Name: `my_repo`
- Repo mit Readme initialisieren

Inhalte anlegen:
- `.smeagol.yml` ohne Inhalt
- 2 weitere Branches anlegen
  - `develop`
  - `feature/add_feature`

## Smeagol-Inhalte

### Home-Seite anlegen

Unter documentation -> my_repo eine `Home`-Seite in 2-3 Commits befüllen: 

```
# Meine Dokumentation

Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate

## Kapitelüberschrift

Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

- Lorem ipsum dolor sit amet
- consectetur adipiscing elit

## Eine andere Kapitelüberschrift

Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
```

### 2. Seite anlegen

Unter documentation -> my_repo eine **neue** `PlantUML`-Seite befüllen (die Backslashes aus den inneren Code-Fences entfernen, sonst klappt das PlantUML nicht):

```
# Hello World!

Willkommen in einem Smeagol-Demo. Hier ist etwas PlantUML:

\```uml
class Car

Driver - Car : drives >
Car *- Wheel : have 4 >
Car -- Person : < owns
\```

# Mehr Dokumentation

Mehr Dokumentation
```
