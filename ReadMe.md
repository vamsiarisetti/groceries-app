# groceries-app

Generates report with grocery list for a user and sends it to user's telegram account

mvn clean install

mvn spring-boot:run

### Local swagger url
`https://localhost/swagger-ui/index.html`

### Steps to Enable HTTPS

Run below command to generate PKCS12 keystore

`keytool -genkey -alias tomcat -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650`

Add below text in `application.yml`

```
server:
    port: 443
    ssl:
        key-store: keystore.p12
        key-store-password: springboot
        keyStoreType: PKCS12
        keyAlias: tomcat
```

`mvn clean install`

`mvn spring-boot:run`


### STEPS TO CONFIGURE CRONTAB IN LINUX MACHINE

crontab -e

Runs 08:30 AM on 1st of every month

`30 08 1 * * sh /Users/vamsiarisetti/MyDisk/grocery-start-app.sh`

list of cron jobs

`crontab -l`


### Bulk insert into grocery_list table
place your file in path mentioned in the command and run below command in mysql terminal


```
LOAD DATA INFILE '<PATH>/groceries-app/src/main/resources/bulk_insert_scripts/groceryList_insert_scripts.csv' INTO TABLE grocery_list  FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n' IGNORE 1 ROWS;
```

If you find some error while running LOAD DATA command try to check secure_file_prev and max_allowed_packet using below commands
#### update secure_file_priv

If below query returned null or different path, update the path

```SHOW VARIABLES LIKE "secure_file_priv";```

/opt/homebrew/etc/my.cnf
secure_file_priv=<path>/groceries-app/src/main/resources/bulk_insert_scripts/

#### update max_allowed_packet

If below query empty result set, update it

```SHOW VARIABLES LIKE "max_allowed_packet";```

update query

```SET GLOBAL max_allowed_packet=524288000;```
