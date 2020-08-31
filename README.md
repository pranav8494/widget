# Widgets
A REST API project for Miro Widget. The project is built with following:
* Java 8
* Build tool: Maven 3.2+
* Backend framework: Sprint-boot
    * OpenAPI for API UI.

## Howto's:

### Build the project: 
* Checkout the project.
* Build the project with maven: 
    
    `mvn clean install`

### Launching the server:
* After the build is done launch the server with following:

    `mvn spring-boot:run`

* Server is launched on 8080 port. Target on http://localhost:8080/widget
* To access the interactive API (openAPI) on: http://localhost:8080/api 

## REST API Exposed: 

Following rest APIs are exposed. For all the below mentioned API, we can test them with swagger exposed API or use curl as given under usage. 

#### Store new widget
POST api to save a new widget or update existing one. 
* If no ID is given, a new widget with new unique ID is created.
* If an ID is given:
  * If widget exists in the store with given ID, update is triggered.
  * If no widget with given ID exists in store, then ID is reset with system generated unique ID and stored in store.
  
#####curl example
`curl -d '{"xCoOrdinate":0,"yCoOrdinate":0,"widgetWidth":20,"widgetHeight":20}' -H "Content-Type: application/json" http://localhost:8080/widget`

#### Update existing widget
PUT api to update an existing widget in store. If the widget doesnt exist, then returns 400. 

#####curl example
`curl -d '{"xCoOrdinate":0,"yCoOrdinate":0,"widgetWidth":20,"widgetHeight":20}' -H "Content-Type: application/json" http://localhost:8080/widget`

### Retrieve widget by ID
GET WS to retrieve a widget by ID. If no widget with ID exists, then returns `404 : Not Found`.

#####curl example
`curl http://localhost:8080/widget/{id}`


#### To retrieve all widgets:
GET WS to get all the widgets which exist in the store.

#####curl example
`curl http://localhost:8080/widget/all`

#### To filter widgets by X Y coordinate range:
GET WS to filter and get all the widgets which fall within the given X & Y CoOrdinate range.

#####curl example
`curl GET http://localhost:8080/widget/filter/0/0/5/5`

#### To Delete a widget: 
DELETE WS to delete the widget with given ID. If no widget with ID exists, then returns `404 : Not Found`.

#####curl example
`curl -X "DELETE" http://localhost:8080/widget/{id}`

## Test Coverage

Classes: 100% 
Lines: 84%

Full report screenshot below:

![Intellij Test Coverage Report](testCoverage.JPG)
