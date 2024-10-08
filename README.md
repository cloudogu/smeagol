[![GitHub license](https://img.shields.io/github/license/cloudogu/smeagol.svg)](https://github.com/cloudogu/smeagol/blob/master/LICENSE)
[![GitHub release](https://img.shields.io/github/release/cloudogu/smeagol.svg)](https://github.com/cloudogu/smeagol/releases)

# Smeagol Dogu
[https://cloudogu.com](https://cloudogu.com/?mtm_campaign=smeagol&mtm_kwd=website&mtm_source=github&mtm_medium=link)

This repository contains smeagol, an wiki server which stores its files in a git repository.

## Development

### Environment
> **Note**: If you intend to use your local ecosystem, which will also reside on `192.168.56.2` by default, you have to set an etcd key first.
> This can be done in vagrant with the command `etcdctl set /config/_global/stage "development"` and restarting CAS in vagrant via `docker restart cas`.

#### Setting up a development ecosystem via groovy
Smeagol requires a proper configured Cloudogu EcoSystem in order to work properly.
This can be achieved by using the provided groovy execution:

```bash 
./mvnw groovy:execute@up
```

This command will clone the ecosystem repository into the `.workspace` folder, creates a `setup.json` with a proper
configuration and starts the vm. After the vm has started, open the setup in your browser at
`http://192.168.56.2:8080`, register the instance and finish the setup.

Then you can access SCM-Manager at: `https://192.168.56.2/scm/`.

Username: `admin`, Password: `adminpw`

With those credentials you can also enter the VM via SSH:

* `ssh admin@192.168.56.2`
* or `cd .workspace/ecosystem; vagrant ssh`

Hint: Make sure your SCM-Manager has the Smeagol Plugin installed!

To stop the vm use the down execution:

```bash
./mvnw groovy:execute@down
```

If the vm is not longer required, it can be removed with the destroy execution:

```bash
./mvnw groovy:execute@destroy
```

### REST API

The server for the REST API can be started with the run goal of the spring-boot maven plugin:
```bash
./mvnw clean spring-boot:run
```
To access the development instance open `http://192.168.56.1:8080` in your browser.
For example, you can get all repositories from `http://192.168.56.1:8080/smeagol/api/v1/repositories`.

### UI

Make sure your SCM-Manager has the Smeagol Plugin installed!
- https://192.168.56.2/scm/admin/plugins/available/

If Smeagol runs on your local host and you use CAS and SCM-Manger inside a CES instance please make sure the SCM-Manager CAS setting accepts any proxy like so:
- Administration -> Settings -> CAS -> Accept any proxy (enabled)

To start the UI development server, you should use yarn. First install the required dependencies:

```bash
yarn install
```

After the dependency installation has finished, start the development server:

```bash
yarn run start
```

After the RESTApi and the UI are successfully started, we can open Smeagol in our browser at `http://192.168.56.1:8080`.
The spring-boot server from the RESTApi will proxy every ui request to the UI server.

### Hot Reload

Hot reload should work out of the box for the ui.

To enable hot reload for java classes, the application must be started with the maven goal "spring-boot:run". For
Intellij we have to recompile the project or enable "Make project automatically", but this requires some sort of hack.
Please have a look at the description of the issue [IDEA-141638](https://youtrack.jetbrains.com/issue/IDEA-141638).

---
## What is the Cloudogu EcoSystem?
The Cloudogu EcoSystem is an open platform, which lets you choose how and where your team creates great software. Each service or tool is delivered as a Dogu, a Docker container. Each Dogu can easily be integrated in your environment just by pulling it from our registry.

We have a growing number of ready-to-use Dogus, e.g. SCM-Manager, Jenkins, Nexus Repository, SonarQube, Redmine and many more. Every Dogu can be tailored to your specific needs. Take advantage of a central authentication service, a dynamic navigation, that lets you easily switch between the web UIs and a smart configuration magic, which automatically detects and responds to dependencies between Dogus.

The Cloudogu EcoSystem is open source and it runs either on-premises or in the cloud. The Cloudogu EcoSystem is developed by Cloudogu GmbH under [AGPL-3.0-only](https://spdx.org/licenses/AGPL-3.0-only.html).

## License
Copyright © 2020 - present Cloudogu GmbH
This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, version 3.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
You should have received a copy of the GNU Affero General Public License along with this program. If not, see https://www.gnu.org/licenses/.
See [LICENSE](LICENSE) for details.


---
MADE WITH :heart:&nbsp;FOR DEV ADDICTS. [Legal notice / Imprint](https://cloudogu.com/en/imprint/?mtm_campaign=ecosystem&mtm_kwd=imprint&mtm_source=github&mtm_medium=link)
