# TicketApp
TicketApp was built as assignment for CloudBees

Read the [Given Problem statement](Question.md)


##### Tech Stack used

Groovy, Springboot, Gradle, Spock, H2, Swagger2.

### Running the app locally
To run the application:
* Download the project. Navigate to the project folder and run `./gradlew bootRun`
* Read the API documentation in local **Swagger2** (run http://localhost:8080/swagger-ui.html on the browser)

* The application runs on in mem **H2 Database**.
* **JaCoCo** is used for code coverage.
* **CodeNarc** is used which analyzes code for defects and bad practices.
* The code initializes a data for test
* Assumptions made:
  * There are 10 seats in each section
  * We create or use the existing user and don't error out if the same user creates more ticket.
  * One user can book more than one ticket with one request, which means if the user needs more ticket there must be multiple requests made.
  


