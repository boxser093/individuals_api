## individuals_api
 Необходимо реализовать individuals api (оркестратор), который будет отвечать за взаимодействие с внешним миром и оркестрацию вызовов “внутренних” сервисов.

Функционал:
Регистрация и логин пользователей
Получение данных по пользователю
Данные о пользователе хранятся в БД сервиса person-service и KeyCloak (данные для аутентификации).
Technology stack:
Java 21
Spring WebFlux
Spring Security
KeyCloak
TestContainers
Junit 5
Mockito
Docker
