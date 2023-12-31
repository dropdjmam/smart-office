# Настройки Idea

Пока воспользуемся настройкой форматирования, для этого нужно импортировать `./codestyle.xml` в Idea. 


# Сборка проекта

Для очистки от файлов сборки
```
./mvnw clean
```

Для сборки 
```
./mvnw package
```

Для проверки и запуска тестов

```
./mvnw verify
```

# База данных

Для создания базы и пользователя под нее, нужно выполнить в pgsql следующие запросы:
```
create database invest;
create user invest with encrypted password 'invest';
grant all privileges on database invest to invest;
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
