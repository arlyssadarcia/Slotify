\# Slotify



Campus facility booking web system - \*\*v1.0.0\*\*



Student request room/facility slots; admins approve or reject. Built as a personal software engineering project (Java, Spring Boot, MySQL, Thymeleaf).



\## Features (v1.0.0)

\- Register / login (session auth, BCrypt)

\- Browse ACTIVE resources and request bookings

\- Admin approve / reject (reject reason required)

\- Student cancel PENDING bookings

\- Overlap check on APPROVED bookings

\- Layered architecture: Controller → Service → Repository



\## Stack

Java 21 • Spring Boot • MySQL • Thymeleaf • Maven



\## Run locally

1\. Start MySQL (e.g. XAMPP)

2\. Confirm `src/main/resources/application.properties` points to `slotify.db`

3\. From this folder:

```bash

.\\mvnw.cmd spring-boot:run

4\. Open http://localhost:8080/login



\## Versioning

* v1.0.0 - web
* v1.1.0 - REST API

