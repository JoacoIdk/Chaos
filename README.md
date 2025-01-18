# Chaos
 A Minecraft server management software

---

## Features
* Manage servers right from the command line (create, delete, start, shutdown, run commands, etc.)
* Make profiles to easily configure your servers
* Run commands in-game with [Helios](https://github.com/JoacoIdk/Helios)
* Allow plugins to easily store and sync data with [Helios](https://github.com/JoacoIdk/Helios)

---

## Installation

### Step 1

Compile the jar from source (clone repo and use maven to package)

```shell
git clone https://github.com/JoacoIdk/Chaos
cd Chaos
mvn package
```


### Step 2

Launch jar file with the following command:

```shell
java -jar chaos.jar
```

### Step 3

Enjoy! Use the `help` command to see a list of commands to try out.

---

## Filesystem structure

### Main

| Directory | Description               |
|-----------|---------------------------|
| servers   | Contains all servers.     |
| profiles  | Contains all profiles.    |
| storage   | Contains all Hermes data. |

### Server

| File        | Description                                                                                            |
|-------------|--------------------------------------------------------------------------------------------------------|
| secret.txt  | Used to authenticate with Hermes.                                                                      |
| starter.txt | Contains the command used to start the server, defaults to `java -Xms1G -Xmx2G -jar server.jar nogui`. |

### Profile

| File         | Description                                                |
|--------------|------------------------------------------------------------|
| platform.txt | Contains the platform that the profile is compatible with. |

---

## Extra

### Why?

This project aims to solve three problems:

* There aren't many server management softwares that are compatible with a wide range of operating systems
* Many plugins need to sync servers on a proxy, but if there is no one online on a server, the Plugin Messaging Channel cannot be used
* Each time you want to start another server you need a shell script (you can also type the command each time, but why bother)

With Chaos, you have a server management software that is compatible with pretty much all operating systems that are compatible with Java, a way to sync data between servers even if there is no one online, and it removes the need to make scripts for each server, with the added bonus of easily creating servers with the use of profiles.

### What port does chaos listen on?

Chaos listens to the helper plugin socket connection on port 30060 by default, as of right now this is hardcoded into the source code, in a future update a configuration file will be added.

### Why isn't this one big project instead of three?

Because they accomplish different things:

* Chaos is a server management software
* Hermes is a networking API
* Helios is a wrapper for the Velocity and Paper APIs

Of course, the three work together, but they are different enought so that merging them would make things more complicated.

### Note

If you are wondering where you can find Helios, congratulations, you have found this repo before it was done, it will be uploaded in a few days.