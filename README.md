![Cloudogu logo](https://cloudogu.com/images/logo.png)
# Smeagol
https://cloudogu.com

This repository contains smeagol, an wiki server which stores its files in a git repository.

## Development

### Environment

Smeagol requires a proper configured Cloudogu EcoSystem in order to work properly. 
This can be achieved by using the provided groovy execution:

```bash 
./mvnw groovy:execute@up
```

This command will clone the ecosystem repository into the `.workspace` folder, creates a `setup.json` with a proper 
configuration and starts the vm. After the vm has started, open the setup in your browser at 
[192.168.56.2:8080](http://192.168.56.2:8080), register the instance and finish the setup.

Then you can access SCM-Manager at: [https://192.168.56.2/scm/](https://192.168.56.2/scm/).

Username: `admin`, Password: `adminpw`

With those credentials you can also enter the VM via SSH:

* `ssh admin@192.168.56.2`
* or `cd .workspace/ecosystem; vagrant ssh`  


To stop the vm use the down execution:

```bash
./mvnw groovy:execute@down
```

If the vm is not longer required, it can be removed with the destroy execution:

```bash
./mvnw groovy:execute@destroy
```

### RESTApi

The server for the RESTApi can be started with the run goal of the spring-boot maven plugin:
```bash
./mvnw clean spring-boot:run
```
To open the development instance open [192.168.56.1:8080](http://192.168.56.1:8080) in your browser.
For example, you can get all repositories from [http://192.168.56.1:8080/smeagol/api/v1/repositories](http://192.168.56.1:8080/smeagol/api/v1/repositories).

### UI

To start the UI development server, you should use yarn. First install the required dependencies:

```bash
yarn install
```

After the dependency installation has finished, start the development server:

```bash
yarn run start
```

The command above should automatically open a browser at [192.168.56.1:3000](http://192.168.56.1:3000)

### Hot Reload

Hot reload should work out of the box for the ui. 

To enable hot reload for java classes, the application must be started with the maven goal "spring-boot:run". For 
Intellij we have to recompile the project or enable "Make project automatically", but this requires some sort of hack. 
Please have a look at the description of the issue [IDEA-141638](https://youtrack.jetbrains.com/issue/IDEA-141638). 

### What is Cloudogu?

Cloudogu is an open platform, which lets you choose how and where your team creates great software. Each service or tool 
is delivered as a [Dōgu](https://translate.google.com/?text=D%26%23x014d%3Bgu#ja/en/%E9%81%93%E5%85%B7), a Docker 
container, that can be easily integrated in your environment just by pulling it from our registry. We have a growing 
number of ready-to-use Dōgus, e.g. SCM-Manager, Jenkins, Nexus, SonarQube, Redmine and many more. Every Dōgu can be 
tailored to your specific needs. You can even bring along your own Dōgus! Take advantage of a central authentication 
service, a dynamic navigation, that lets you easily switch between the web UIs and a smart configuration magic, which 
automatically detects and responds to dependencies between Dōgus. Cloudogu is open source and it runs either on-premise 
or in the cloud. Cloudogu is developed by Cloudogu GmbH under [MIT License](https://cloudogu.com/license.html) and it 
runs either on-premise or in the cloud.

### How to get in touch?

Want to talk to the Cloudogu team? Need help or support? There are several ways to get in touch with us:

* [Website](https://cloudogu.com)
* [Mailing list](https://groups.google.com/forum/#!forum/cloudogu)
* [Email hello@cloudogu.com](mailto:hello@cloudogu.com)

---
&copy; 2016 Cloudogu GmbH - MADE WITH :heart: FOR DEV ADDICTS. [Legal notice / Impressum](https://cloudogu.com/imprint.html)
