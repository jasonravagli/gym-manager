# Gym Administration System

[![CI with Maven, Coveralls, and PIT on Linux](https://github.com/jasonravagli/gym-manager/actions/workflows/linux-coveralls.yml/badge.svg)](https://github.com/jasonravagli/gym-manager/actions/workflows/linux-coveralls.yml)
[![CI with Maven and Java 8 on Linux](https://github.com/jasonravagli/gym-manager/actions/workflows/other-platforms.yml/badge.svg)](https://github.com/jasonravagli/gym-manager/actions/workflows/other-platforms.yml)


[![Coverage Status](https://coveralls.io/repos/github/jasonravagli/gym-manager/badge.svg?branch=master)](https://coveralls.io/github/jasonravagli/gym-manager?branch=master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=jasonravagli_gym-manager&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=jasonravagli_gym-manager)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=jasonravagli_gym-manager&metric=bugs)](https://sonarcloud.io/summary/new_code?id=jasonravagli_gym-manager)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=jasonravagli_gym-manager&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=jasonravagli_gym-manager)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=jasonravagli_gym-manager&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=jasonravagli_gym-manager)

The final project for the course of **Advanced Programming Techniques** taught by professor Bettini at the Università degli Studi di Firenze.

It is a simplified adiministration system for a gym, **developed in Java using TDD, AssertJ, PIT, JaCoCo, Coveralls, Docker and GitHub Actions**. The system allows to manage gym courses, gym members and member subscriptions to courses.

## Features and compatibility
The project provides a GUI application that can connect to two different types of databases: MongoDB and MySQL. See sections _Build replication_ and _Usage_ for instruction of how build and run the desired version.

The following table show the compatibility with the considered OSes and Java versions:
| OS | Java Version | Compatible |
|--|--|--|
| Ubuntu 20.04 | Java 8 and 11 | YES |
| Mac OS 11 | Java 8 and 11 | YES (*) |
| Windows 10 | Java 8 and 11 | Partially (**) |

_(*) The project was developed and tested locally on a MacOS 11 system. However when building remotely on a MacOS 11 virtual system with GitHub Actions the integration tests were not able to connect to the MySQL Docker container. After some research, we decided to exclude MacOS from the CI build._

_(**) On the early stages of development the project was successfully tested on a Windows 10 system. However the MongoDB Docker image required by the application is not available for Windows. MongoDB transactions require a cluster with at least 3 nodes that support replicas. Manually creating such a cluster was beyond the scope of the project, hence we used an already-built Docker image available only for Unix._

## Build replication
The project can be completely tested and built by running the following Maven commands from the _aggregator_ directory:

`mvn clean verify -Pjacoco,pit`

`jacoco` and `pit` profiles are optional. When activated will generate the code coverage and mutation testing reports inside each module folder. With the `jacoco` profile activated an aggregated report about code coverage will be generated inside the _report_ module.

The above command will also generate the executable FatJARs for the two versions of the application, respectively inside the `app-mysql/target` and `app-mongo/target` folders.

## Usage
### app-mongo-1.0-jar-with-dependencies.jar
Located inside `app-mongo/target`, it is the version that connects to a MongoDB database.

For a quick demo execution you can start and set up the MongoDB Docker container by running the following command inside the `app-mongo` folder:

`mvn docker:start`

Then if you simply run the jar the application will start and connect to the MongoDB container.

If you already have your MongoDB database you can tell the application to connect to it by specifying additional arguments from the command line.
A complete view of the available arguments is provided specifying `--help` at the moment of execution:

| Argument | Description |
| -- | -- |
| --mongo-host | MongoDB server address |
| --mongo-port | MongoDB server port |
| --db-name | Name of the database to connect to |

Keep in mind that the application uses Mongo transactions. In case of custom databases, they must be configured with a Mongo cluster with replicas to support transactions, otherwise the application will raise errors.

---

### app-mysql-1.0-jar-with-dependencies.jar
Located inside `app-mysql/target`, it is the version that connects to a MySQL database.

For a quick demo execution you can start and set up the MySQL Docker container by running the following command inside the `app-mysql` folder:

`mvn docker:start`

Then if you simply run the jar the application will start and connect to the MySQL container.

If you already have your MySQL database you can tell the application to connect to it by specifying additional arguments from the command line.
A complete view of the available arguments is provided specifying `--help` at the moment of execution:

| Argument | Description |
| -- | -- |
| --mysql-host | MySQL server address |
| --mysql-port | MySQL server port |
| --mysql-user | Username used to access to the database |
| --mysql-pwd | Boolean flag. When true the user will be asked to input the db user password on startup, otherwise the default password '_password_' will be used (intended only for testing and demo purposes) |
| --db-name | Name of the database to connect to |

The database to connect to must already have the schema required the application. The required schema can be found inside the `docker/mysql-scripts/mysql-db-schema.sql`.
