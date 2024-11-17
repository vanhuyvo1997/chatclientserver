#chatclientserver
THE CONSOLE CHAT USING JAVA SOCKET


I. INTRODUCTION
The application include two sperate parts:
 - The server takes responsibility to hear messages sent by clients.
 - The clients take responsibility to send messages to the server.
Many clients connected to the same server can chat with each other.

II. HOW TO RUN
 a. On server
   1. Get year computer's ip address
   2. Replace IP address in src/server/Server.java, line 16 to your one.
   3. Run the main function in /src/server/ServerApp.java
   4. Input port number in console window. (Port value must be greater or equal than 1024 and lower or equal 65535). Press Enter.

  After doing all above steps, the server is ready to hear connections from clients, as well as messages sent from them.

 b. On clients
   1. Run the main function in /src/client/ClientApp.java
   2. In console window, enter your nickname, press enter.
   3. Input the IP address of the server you would like to connect, press enter.
   4. Type the port number of the server you would like to connect, press enter.
   5. And the client is ready now. type any message and press Enter to send. Any client connected to the same will see the messages you sent.

 Many clients can connect to the same server and chat with each other in LAN.

