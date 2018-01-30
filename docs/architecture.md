# Architecture

## Bounded Contexts

Smeagol comprises of Bounded Contexts (BC) in terms of Domain-Driven Design (DDD) that ideally would share nothing.
This promises to separates domain from technical concerns. It divides complexity into different bounded contexts.

As the account must be read from the session in all BCs, we avoid duplication by using a Shared Kernel. 
 
![Bounded Contexts](assets/boundedcontexts.svg)

### Shared Kernel

### Authc

### Repository

### Wiki

## Hexagonal Architecture 

Within each Bounded Context we our interpretation of a hexagonal architecture (aka [Clean Architecutre, Onion Architecture, etc.](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html)) with the following layers:

* Domain: Contains Domain logic only (no technical stuff). Defines value objects, entities, repository *interfaces* and services
* UseCases: Exposes actions (write operations) that are possible on the domain.
  We use the Command Bus pattern here. Advantage: Commands provide an overview of domain operations allowed by the system. 
  They are implementing using a concrete pattern and are not just another thing called "service".   
* Infrastructure: Contains all the technical stuff: REST controllers and repository *implementations* and all the glue.

Access is only allowed in the following direction: Infrastructure -> Use cases -> Domain

## Mapping to Code

* Shared Kernel: Base Package `com.cloudogu.smeagol`
  The base package als contains the entry point of our application.
* Bounded Contexts map to subpackages of the base package, e.g.
   * `com.cloudogu.smeagol.authc`
   * `com.cloudogu.smeagol.repository`
* The hexagonal layers map to subpackages of the individual Bounded Context pacakgages, e.g.
   * `com.cloudogu.smeagol.repository.infrastructure`
   * `com.cloudogu.smeagol.repository.domain`
   
### Technical Terms

* Repository: repository in terms of DDD for accessing -> Note: We also have a domain object called repository referring to a Git or SCMManager repository
* Controller: REST Controller, i.e. endpoint (returns Resources).
* Resource: DTO that maps entity to the REST interface (returned by a Controller)