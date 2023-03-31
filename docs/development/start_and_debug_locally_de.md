# Smeagol lokal starten
Im folgenden wird beschrieben, wie Smeagol lokal ausgeführt werden kann.
Dennoch ist dafür ein lokales EcoSystem mit einem lauffähigen SCM-Manager notwendig.
Außerdem muss der CAS in die development-Stage gebracht werden.
Dafür folgendes ausführen: `etcdctl set config/_global/stage development && docker restart cas`


## 1. Frontend starten
* Sicherstellen dass die installierte Node-Version kleiner als 18 ist. Mit Node 16 wurde es erfolgreich getestet.
* Aus dem Root-Ordner des Repositories folgendes ausführen: `node src/main/scripts/start.js`


## 2. Backend starten
* Beim SCM-Manager ein API-Token erstellen: `https://192.168.56.2/scm/me/settings/apiKeys`
* Den API-Key kopieren und in die application.yml unter `src/main/resources` eintragen

Beispiel:
```
scm:
  url: https://192.168.56.2/scm
  accessKey: eyJhcGlLZXlJZCI6IjVFVFp5ZUNlckEiLCJ1c2VyIjoiYWRtaW4iLCJwYXNzcGhyYXNlIjoieDFodXliWkhIaWpuTnNoNFlqQngifQ
```

* Mit `./mvwnw spring-boot:run` bzw `mvn spring-boot:run` Smeagol starten - Applikation läuft nun.
* Alternativ kann Smeagol auch einfach über die IDE gestartet werden. Die Konfiguration dafür sollte automatisch angelegt worden sein.

