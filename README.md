# Receipt Processor Application
## Overview
This is a Spring Boot application that processes receipts to calculate points based on specific rules. The points can be calculated based on various factors such as retailer name, total amount, item descriptions, and purchase date and time.

### Prerequisites
* Java 17 (or compatible JDK)
* Maven (for building the project)
* Docker (optional, for containerization)

### Getting Started
1. Clone the Repository
   ```bash
   git clone https://github.com/yourusername/receiptprocessor.git
   cd receiptprocessor
   ```
2. Build the Application
   This command will generate a JAR file in the target/ directory (e.g., target/receiptprocessor-1.0.0-SNAPSHOT.jar).
   Make sure you have Java 17 and Maven installed.
    ```bash
    mvn clean package
    ```

3. Running the Application
   * Option 1: Running Locally:
      - After building the JAR file, you can run the application using Java:
        ```bash
        java -jar target/receiptprocessor-1.0.0-SNAPSHOT.jar
        ```
     The application will start on port 8080 by default. You can access it at http://localhost:8080.

   * Option 2: Running with Docker:
     Build the Docker Image.
     Make sure Docker is installed and running. Then, build the Docker image:
     ```bash
     docker build -t receiptprocessor:latest .
     ```
     Run the Docker Container:
     Start the container and map port 8080:
     ```bash
     docker run -p 8080:8080 receiptprocessor:latest
     ```
     The application will now be accessible at http://localhost:8080.

4. Access the Application
   Once the application is running (either locally or inside Docker), you can interact with it via HTTP requests. Here are some sample requests:

    Process a Receipt:
    
    Send a POST request to process a receipt:
    
    ```http
    POST http://localhost:8080/receipts/process
    ```
    Get Points for a Receipt:
    
    Send a GET request to retrieve points for a processed receipt:
    
    ```http
    GET http://localhost:8080/receipts/{id}/points
    ```

5. Running Tests
   To run the unit tests:

```bash
mvn test
```
This will execute all unit tests to ensure the application is functioning correctly.