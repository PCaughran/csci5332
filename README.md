# csci5332
Georgia Southern Fall 2022 Data Communications and Networking Final Project

To start a server on your machine:
1. Compile Server.java as you would any other java program. I use the command javac Server.java
2. Run Server.java as you would any other java program. On my laptop, the command 'java Server.java' works, but on my desktop, it requires the command 'java Server'.
3. The program will ask you to provide a password for your Server, do so.
4. It will ask you to confirm the password, enter y for yes, any other character for no.
5. Once password is confirmed, server will start.
6. The IP Address and port number that are tied to the server will be displayed. Share this with all your friends who you want to talk to

To start a Client session on your machine:
1. Compile Client.java as you would any other java program. I use the command 'javac Client.java'
2. Run as Client.java as you would any other java program. See the above section for more details
3. The program will ask you to provide the IP Address/Port number of the server, respond in the form XXX.XXX.XXX.XXX./XXXX, any other form will cause the program to crash.
4. The program will ask you to provide a username, this cannot be changed and is visible to everyone in the server 
5. The program will then verify the password for the server. it is case sensitive.
6. If the password cannot be verified, your session will be terminated
7. Once verified, you will be able to communicate to everyone in the server. Keep in mind, any message you send is visible to the entire server. 


