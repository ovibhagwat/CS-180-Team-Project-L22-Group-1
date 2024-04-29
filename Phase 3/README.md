# Project Phase 2

## Compilation and Execution Instructions
1. **Compile the Java files:**
   To compile, ensure you have JUnit configured in your project.
   To compile the test classes, ensuring you include the JUnit library in the classpath.
2. **Execute the Main Program:**
   There are two types of test, one is tested by Junit with test files, another is tested manually for NetworkIO.
   1. Tested by Junit
   - For testing the Test files with Junit, compile the test classes, ensuring you include the JUnit library in the 
     classpath run the main method in each test files, ensure you have JUnit configured in your project.
   - Here is the test file by Junit: TestClientHandler.java, TestRequest.java, TestResponse.java
   - For these three test files, simply run the main method, and it will show in the console "All tests passed!"
   2. Test manually
   - For manually test, there are two NetworkIO tests will be done manually.
   - To test single connection with one client, run the Server main method to start server first, then run the Client 
     main method to connect to server, and it will send and receive five requests and response and print the data of 
     response in the screen.
   - The console of Client will be as follows:
   - Connected to server.
   - test 0
   - test 1
   - test 2
   - test 3
   - test 4
   - Connection closed.
   - It shows that single connection and send and receive IO successfully.
   - To test multiple connections using thread with multiple clients, run the Server main method and the run the main 
     method of TestMultipleClients. It will simulate five threads running simultaneously. It will print the connect 
     information of each client to the console.
   - The console of Server will be as follows:
   - ClientHandler started for client #5
   - ClientHandler started for client #2
   - ClientHandler started for client #3
   - ClientHandler started for client #1
   - ClientHandler started for client #4
   - Client #3 has closed the connection.
   - Client #5 has closed the connection.
   - Client #4 has closed the connection.
   - Client #2 has closed the connection.
   - Client #1 has closed the connection.
   - When you repeat same steps and run again, it will give different order of client starts, which shows that it 
     implements the real thread successfully.

## Class Descriptions

### Client

**Functionality:**
- Connects to the server via a socket.
- Sends serialized request objects and receives serialized response objects.
- Handles basic user interactions for testing purposes.

**Testing:**
- Test single connect by running the main method
- Simulated network communication in `TestMultipleClients` to verify correct handling of multiple clients.
- Each method tested to ensure serialization and network transmission integrity.

**Relationships:**
- Communicates with `Server` through socket connections.
- Utilizes `RequestResponseProtocol` for formatting messages.

### Server

**Functionality:**
- Listens for client connections and handles them in separate threads.
- Uses `ServerSocket` to manage incoming connections.

**Testing:**
- Tested through running main method
- Functionality verified through client-server interaction tests.

**Relationships:**
- Creates new `ClientHandler` instances for each connected client to handle communication.

### ClientHandler

**Functionality:**
- ClientHandler is a part of Server side and implements Runnable to connect multiple clients.
- Handles all server-side communication with a single client.
- Processes requests and sends responses back to the client.
- Has access to database.

**Testing:**
- Tested constructor through `TestMultipleClients`
- Tested through `TestClientHandler`, ensuring all request types are processed correctly.

**Relationships:**
- Instantiated by `Server` for each client connection.
- Uses `RequestResponseProtocol` to interpret and respond to client requests.

### RequestResponseProtocol

**Functionality:**
- Defines the format for requests and responses between clients and server.
- Includes enumerations for request types, response types, and error codes.

**Testing:**
- Functionality verified through unit tests in `TestRequest` and `TestResponse`.

**Relationships:**
- Used by `Client`, `ClientHandler`, and indirectly by `Server` to standardize communication.

### Test Classes (TestClientHandler, TestRequest, TestResponse, TestMultipleClients)

**Functionality:**
- Provide unit testing and integration testing for the components they are designed to test.
- First three files use JUnit framework for assertions and test management.

**Testing:**
- Each test class is responsible for ensuring the robustness and correctness of its respective component.

**Relationships:**
- `TestClientHandler` tests `ClientHandler`.
- `TestRequest` and `TestResponse` test the serialization and integrity of the `RequestResponseProtocol`.
- `TestMultipleClients` simulates multiple clients connecting to the server simultaneously to test concurrency and network handling.

For class implemented in Phase 1, you can refer to the README for phase 1
Below is the README for Phase 1:

# Project Phase 1

## Compilation and Execution Instructions

1. **Compile the Java files:**
   To compile, ensure you have JUnit configured in your project.
   To compile the test classes, ensuring you include the JUnit library in the classpath.
2. **Execute the Main Program:**
    - All main program is in the test java files, there are five test files in total, each tests one java class.
    - To run the test cases, ensure you have JUnit configured in your project.
    Compile the test classes, ensuring you include the JUnit library in the classpath.
    - To run TestUser.java, TestMessage.java and TestConversation.java, you don't need any extra files and no files 
      will 
   create after running.
    - To run TestPhotoMessage.java, you will need a photo file which has filename of "testPic.png", or you can change 
   the TEST_IMAGE_PATH in the test code. And no file will create after running.
   - To run TestDatabaseManage.java, after running, there will be two files "loginUser.txt" and "Amy123_Bob456.txt" 
   create, that's normal, please delete them after running.
   When you run each test file, there will be a "All tests passed!" output to indicate test successfully.

## Class Descriptions

### `User`

**Functionality:** Represents a user in the system, handling operations such as username and profile management, friend and block lists, and storing conversation history.

**Testing:** `TestUser` verifies all functionalities, focusing on constructor behavior, getters and setters, profile changes, and reading/writing user data from/to files.

**Relationships:** Interacts with `Message`, `PhotoMessage`, and `Conversation` classes for managing conversations and messages.

### `Message`

**Functionality:** Models a text or photo message, encapsulating details like sender, receiver, content, and timestamp.

**Testing:** `TestMessage` covers construction, serialization/deserialization, and equality checks to ensure message integrity and functionality.

**Relationships:** Used by `Conversation` for storing individual messages. `PhotoMessage` extends `Message` to add support for images.

### `PhotoMessage`

**Functionality:** Extends `Message` to support photo messages, including loading and saving images.

**Testing:** `TestPhotoMessage` assesses image loading, saving, and the correct generation of filenames based on message metadata.

**Relationships:** Inherits from `Message` and utilized within `Conversation` for photo-based communication.

### `Conversation`

**Functionality:** Manages a conversation between two users, including message history and reading/writing conversation data.

**Testing:** `TestConversation` ensures correct conversation file naming, message addition and deletion, and data persistence through read/write operations.

**Relationships:** Aggregates `Message` and `PhotoMessage` instances. Relies on `User` to identify conversation participants.

### `DatabaseManage`

**Functionality:** Provides static methods for user account management (creation, login) and conversation initiation between users.

**Testing:** `TestDatabaseManage` evaluates account creation, login procedures, and the establishment of conversations, including error handling and edge cases.

**Relationships:** Works closely with `User` and `Conversation` classes to orchestrate higher-level operations like account and conversation management.
