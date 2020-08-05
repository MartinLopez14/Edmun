# Edmun SENG302
-----
Outdoor activites tracker, where users can register, sign-in, view profile and edit profile.
Custom activity types can also be created and assainged to users.


### Basic Project Structure
- client/src Frontend source code (JS - Vue)
- client/public publicly accesable web assets
- client/node_modules Assets for node plugins
- client/src/pages Location for all Vue webpages

- server/src Backend source code (Java - Spring)
- server/gradle Backend gradle source code

### How to run
##### Setup Environment Variables
`cd server/src/main/resources`
- Navigate to the application.properties file

1. ###### Database
    - Insert the database configuration variables  
    `server.port=9499`  
    `spring.jpa.hibernate.ddl-auto=update`  
    `spring.datasource.url=jdbc:mariadb://db2.csse.canterbury.ac.nz:3306/seng302-2020-team700-prod`  
    `spring.datasource.driver-class-name=org.mariadb.jdbc.Driver`  
    - Insert and fill in your database credentials  
    `spring.datasource.username=`  
    `spring.datasource.password=`  

2. ###### Default Admin  
    - Insert the following variables and fill in the default admin credentials  
    `ADMIN_EMAIL=admin@email.com`  
    `ADMIN_PASSWORD=Password1`


##### Client (Frotnend/GUI)
`cd client`
`npm install`
`npm run serve`

Running on: http://localhost:9500/

##### Jest Test 
`cd client`
`npm install`
`npm run test`

##### Server (Backend/API)
`cd server`
`./gradlew bootRun`

Running on: http://localhost:9499/

### Dedicated Server Instances
##### Staged test instance (dev branch)
https://csse-s302g7.canterbury.ac.nz/test/
##### Production server instance (master branch)
https://csse-s302g7.canterbury.ac.nz/prod/

### Quality Assurance
##### SonarQube

For Client:
`cd client`
`npm run test`
`npm run sonarqube`

For Server:
`cd server`
`./gradlew bootRun`
`gradle sonarqube`

To view sonarqube analysis on a browser use: https://csse-s302g7.canterbury.ac.nz/sonarqube/account/security/
- use VM credentials for login.


