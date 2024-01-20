# MavenDocker-NullProject :whale:
## NullTeam's maven project, presenting docker monitoring.
This project monitors a Docker Cluster and keeps track of all measurements made inside the Cluster    
by storing them in a database. It also provides a simple Graphical User Interface through which the user can   
see the Docker Cluster and interact with it with several actions on both Docker Instances and Docker Images:  
✔️ **start - stop - rename - restart - pause - unpause - kill - remove - show logs - show subnet**  
✔️ **pull - implement - remove**
## How to use our application
### Prerequisites
* Make sure Docker Desktop is downloaded and running in the background
* Have at least two images and two containers in your Docker Cluster
* Internet connection
### Building
1. Clone the repository:
```sh
git@github.com:konstantopoulosk/MavenDocker-NullProject.git
```
2. Build the app inside `dockermonitor`:
```sh
mvn clean install
```
### Running
1. Inside `MavenDocker-NullProject`:
```sh
copy c [path]\MavenDocker-NullProject\dockermonitor\target
```
2. Run the app inside `dockermonitor/target`:
```sh
java -jar nullteamproject-3.3.4-shaded.jar
```
### Inside the App
Containers Menu:  
![This is a simple manual for the Containers Menu](dockermonitor%2Fsrc%2Fmain%2Fresources%2FManualPhotos%2FMenuManual.jpg)  

Examples:  
* You can see how to start a container [here](dockermonitor/src/main/resources/ManualPhotos/StartManual.jpg).  
* You can see how to rename a container [here](dockermonitor/src/main/resources/ManualPhotos/RenameManual.jpg).
## UML
[Our UML Diagram](dockermonitor/src/main/resources/UML.png)
## Data Structures & Algorithms
- Lists
- Arrays 
- Threads
- Loops
- SQL coding
- JavaFX graphics
- JavaDoc comments

## Promotion
Check our [promotional video](https://www.youtube.com/watch?v=FigAYyGkeN8) 😉
