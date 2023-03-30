# Start Smeagol locally
The following describes how to run Smeagol locally.
However, this requires a local EcoSystem with a running SCM manager.
In addition, the CAS must be brought into the development stage.
To do this, execute the following: `etcdctl set config/_global/stage development && docker restart cas`.


## 1. start frontend
* Make sure that the installed node version is lower than 18. With Node 16 it was tested successfully.
* From the root folder of the repository execute: `node src/main/scripts/start.js`.


## 2. start backend
* Create an API token at the SCM manager: `https://192.168.56.2/scm/me/settings/apiKeys`.
* Copy the API key and add it to the application.yml under `src/main/resources`.

Example:
```
scm:
  url: https://192.168.56.2/scm
  accessKey: eyJhcGlLZXlJZCI6IjVFVFp5ZUNlckEiLCJ1c2VyIjoiYWRtaW4iLCJwYXNzcGhyYXNlIjoieDFodXliWkhIaWpuTnNoNFlqQngifQ
```

* Start Smeagol with `./mvwnw spring-boot:run` or `mvn spring-boot:run` - application is now running.
* Alternatively, Smeagol can also be started simply via the IDE. The configuration for this should have been created automatically.
