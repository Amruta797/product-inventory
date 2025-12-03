# Product Inventory Management

* This is a microservice to manage product inventory
* This has below functionalities :
  * Add new product
  * Get all products (With Pagination)
  * Update products quantity
  * Search product by name
  * Get inventory summary
* This a dockerized service which uses postgresSQL as database
* This project also contains Unit Tests and integration Tests

# How to Build Services

## Docker installation
  * Docker must be installed on your local machine since this is a dockerized build
  * Please refer https://www.docker.com/get-started/ link for docker installation

## Get project from Git
  * Clone git project on your local machine
      * With git installed on your local machine, hit below command on terminal 
        * git clone https://github.com/Amruta797/product-inventory.git
      * Without git, you can also download zip folder for the code
        * Go to https://github.com/Amruta797/product-inventory.git
        * click on green button <> Code
        * Hit Download zip button
        * Unzip this folder

## Build Project using docker
  * Go to project directory on Terminal or Git Bash on your machine
  * Execute below command to build the project
    * docker compose build
    * This will download maven and java (version specified in Dockerfile)
    * Also all the required maven dependencies will be downloaded
    * This will further generate the "inventory-0.0.1-SNAPSHOT.jar" in target folder

## Run Project using docker
  * Once project is built, hit below command in terminal to run the project
    * docker compose up
  * This will start postgreSQL service and product-inventory service
  * Both services will be executed in same network created by docker so that they can communicate with each other
  * You can see "Inventory Application has been started" message on console log when application starts running successfully


# REST endpoints
  * Get all products : http://localhost:8080/products
  * Add new product : http://localhost:8080/products with Request parameter
  * Update product quantity : http://localhost:8080/products/${id}/quantity?quantity=${newValue}
  * Search by Name : http://localhost:8080/products/search?name=xyz
  * Get inventory summary : http://localhost:8080/products/summary

# OpenAPI and Swagger API links
  * http://localhost:8080/api-docs
  * http://localhost:8080/swagger-ui/index.html

# Test Using swagger-ui
  * Go to the link http://localhost:8080/swagger-ui/index.html
  * Here you can find all the REST endpoints mentioned above
  * You can click on every endpoint and try it out on your own

# Test using curl commands on Terminal

## Get all products
  * command : curl -X GET "http://localhost:8080/products"
  * response : Json response containing list of all products

## Add new product
  * command : curl -X POST http://localhost:8080/products \
                    -H "Content-Type: application/json" \
                    -d '{
                            "name": "Laptop",
                            "price": 19.99,
                            "quantity": 2
                        }'
  * response : Json response which contains newly added product

## Update product quantity
  * command : curl -X PUT "http://localhost:8080/products/${id}/quantity?quantity=${value}"
  * where id : product id, value : changed quantity
  * response : Json response with updated product

## Search by Name
  * command : curl -X GET "http://localhost:8080/products/search?name=${name}"
  * where name : full name or some part of name of the product that you want to search
  * response : Json response containing list of all matching products

## Get inventory summary
  * command : curl -X GET "http://localhost:8080/products/summary"
  * response : {
                    "totalProducts": 5,
                    "totalQuantity": 78,
                    "averagePrice": 219.99,
                    "outOfStock": [
                            { "id": 3, "name": "Monitor" },
                            { "id": 5, "name": "Keyboard" }
                    ]
                }