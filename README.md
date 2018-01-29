#![Cloudogu logo](https://cloudogu.com/images/logo.png)
# Smeagol
https://cloudogu.com

This repository contains smeagol, an wiki server which stores its files in a git repository. Smeagol wraps the famous 
[Gollum](https://github.com/gollum/gollum) Wiki and adds some functions to it:

* Support for multiple Wikis in one Server
* Remote Repositories ( and )
  * Smegol clones the repository on first access
  * pulls changes regularly
  * push changes back to remote repository after commit
* SCM-Manager support
* CAS Authentication

## Configuration

Smeagol is configured with environment variables. The following configuration options are available:

| Name | Default value | Description |
| ---- | ------------- |-------------|
| SMEAGOL_PORT | 8080 | Port for Smeagol webserver |
| SMEAGOL_CONTEXT_PATH | /smeagol | Context path for the web application |
| SMEAGOL_SERVICE_URL | | Full qualified url to smeagol (with context path) |
| SMEAGOL_CAS_URL | | Url to CAS |
| SMEAGOL_HOME | | path to home directory, smeagol will clone the repositories to this directory |
| SMEAGOL_STAGE | PRODUCTION | Stage DEVELOPMENT OR PRODUCTION, never use DEVELOPMENT for production servers |
| SMEAGOL_STATIC_PATH | src/main/webapp | path to static pages |
| SMEAGOL_GEM_PATH | target/rubygems | path to ruby gems |
| SCM_INSTANCE_URL | | Url to SCM-Manager repository server |

---

## Hot Reload

To enable hot reload for java classes, the application must be started with the maven goal "spring-boot:run". For 
Intellij we have to enable "Make project automatically" and this requires some sort of hack. Please have a look at 
the description of the issue [IDEA-141638](https://youtrack.jetbrains.com/issue/IDEA-141638). 

### What is Cloudogu?
Cloudogu is an open platform, which lets you choose how and where your team creates great software. Each service or tool is delivered as a [Dōgu](https://translate.google.com/?text=D%26%23x014d%3Bgu#ja/en/%E9%81%93%E5%85%B7), a Docker container, that can be easily integrated in your environment just by pulling it from our registry. We have a growing number of ready-to-use Dōgus, e.g. SCM-Manager, Jenkins, Nexus, SonarQube, Redmine and many more. Every Dōgu can be tailored to your specific needs. You can even bring along your own Dōgus! Take advantage of a central authentication service, a dynamic navigation, that lets you easily switch between the web UIs and a smart configuration magic, which automatically detects and responds to dependencies between Dōgus. Cloudogu is open source and it runs either on-premise or in the cloud. Cloudogu is developed by Cloudogu GmbH under [MIT License](https://cloudogu.com/license.html) and it runs either on-premise or in the cloud.

### How to get in touch?
Want to talk to the Cloudogu team? Need help or support? There are several ways to get in touch with us:

* [Website](https://cloudogu.com)
* [Mailing list](https://groups.google.com/forum/#!forum/cloudogu)
* [Email hello@cloudogu.com](mailto:hello@cloudogu.com)

---
&copy; 2016 Cloudogu GmbH - MADE WITH :heart: FOR DEV ADDICTS. [Legal notice / Impressum](https://cloudogu.com/imprint.html)
