# Настройки Idea

Пока воспользуемся настройкой форматирования, для этого нужно импортировать `./codestyle.xml` в Idea. 

# База данных

Для создания базы используется docker:
```
docker run -p 5432:5432 -e POSTGRES_USER=booking -e POSTGRES_PASSWORD=booking -e POSTGRES_DB=booking -d postgres:15-alpine
```

Все изменения в структуре базы оформляем через миграции, при старте приложения они автоматически применятся.

Пример миграции:
```
./src/main/resources/db/migration/V0_1_0__create_test_table.sql
```
Имя файла должно следовать соглашению:
* Сначала префикс `V0_1_X`, где `V0_1` это номер версии приложения, `X` номер миграции.
* Потом краткое описание, что там за изменения `create_test_table`
* Префикс отделяется от описания двойным подчеркиванием `__`

# Сборка проекта

Для сборки
```
mvn -DDB_URL=jdbc:postgresql://localhost:5432/booking -DDB_USER=booking -DDB_PASS=booking package
```

Для проверки и запуска тестов

```
mvn -DDB_URL=jdbc:postgresql://localhost:5432/booking -DDB_USER=booking -DDB_PASS=booking verify
```
