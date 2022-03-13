# Rabo_project
Rabobank Customer Statement Processor:

Import the project in your favourite IDE
Give the prefered folder path in properties file to the "container" to store the validated report.
Run the file src/main/java/com/rabobank/statementprocessor/CustomerStatementProcessorApplication.java
Once server is started, open the postman and hit the post url [POST]http://localhost:8080/customer/api/process-statement
Note: While sending request select the file in form-data and file name should be "file".
