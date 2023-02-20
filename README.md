# RESTfull web service for Anti-Fraud System, with basic authentication and authorization, for handling money transactions, block stolen cards and suspicious IPs.

3-layered API structure: rest, domain and persistence.

#### Rest layer:
* controlers:
user: 7 endpoint to handle user creation, listing all users, deleting user, change user role, change user access, login and list all users with the given access;
transaction: 4 enpoints to handle making money transaction, giving feedback to transaction, get all transactions' history(id, amount of money, ip adress, card number, world region, local date & time, transaction result & feedback) and get all transactions' history by a specific card number;
card: 3 endpoints for saving stolen card, deleting stolen card and listing all cards;
suspicious IP: 3 endpoints for saving suspicious IP, deleting IP and listing all IPs.

* dto:
instead of RequestEntity and ResponseEntity classes for serialization and deserialization, the API uses Java record classes with Jakarta, Jackson and custom annotations for validations.

#### Domain layer:
* model:
all the entity classes used for DB persistence with Jakarta persistence annotations. Every entity class uses Factory class for object creation.
* service:
5 interface services (CustomUser, RegularCard, StolenCard, SuspiciousIP, Transaction) with their concrete implementation.

#### Persistence layer:
* repository:
5 repositories(one for each service) extending the JpaRepository.

API uses embedded H2 database and the DB's persistence is done through Spring Data JPA.

The API's using Base64 Spring Security authentication. The authorization's done at the Security Filter Chain level, and at the controllers' level through @PreAuthorize annotations.

API has a Global Exception Handler to handle the custom, authentication, cotroller and service level exceptions.

API uses springdoc OpenAPI specification for its documentation.

Unit and integration tests are done at the 3 levels of the architecture using JUnit5, AssertJ and Mockito, and all are written with the MockitoBDD syntax.
