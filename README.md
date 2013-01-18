BukkitForge - a Minecraft mod that ports the Bukkit plugin API to minecraft.
===================================

 
This is very WIP. Since i started doing this, i was mostly aiming for a server that didn't crash.
It can load plugins, commands work, lots of things work, permissions *should* work, etc.

Most of this mod is a fairly straight port from Craftbukkit, but I've had to modify parts
(and i anticipate modifiying more).

This is ported to 1.4, and works fine on 1.4.6/7


Jenkins? Jenkins? JENKINS!
==========================

That's right, now we have an automated jenkins build-thingy.
You may find it [here](http://build.technicpack.net/job/BukkitForge)

[![Build status](http://build.technicpack.net/job/BukkitForge/badge/icon)](http://build.technicpack.net/job/BukkitForge/)

Any errors with plugins go [here](https://github.com/Bukkit-Forge-Plugins/Bukkit-Forge-Plugin-Errors)

GETTING IT WORKING IN MCP:
=========================
 
After unzipping the forge source, copy bukkit\_at.cfg to forge/accesstransformers (create the folder if it's not there), and copy
all the files in 'lib/' (excluding bukkitAPI.jar) into the lib folder in mcp.
decompile as usual. 
You will also need to add the files in the 'lib' folder (excluding bukkitAPI.jar) into the projects in eclipse.

COMPILING (to get all that good stuff)
=======================

You need Apache Ant and the JDK. To build, run 'git submodule sync' in the root folder, and then cd to the 'blockbreak' folder and type 'git pull origin'.
cd back up one level and simply type 'ant' in the folder with build.xml inside it.

FOR BUKKIT DEVS WHO WANT TO ENSURE COMPATIBILITY
===============================================

If your mod does NOT use guava, you're fine, it should work.

If your mod DOES use guava, you need to add 'guava10' before all of your guava imports.
The reason this is required is FML uses guava 12 which is not compatible with guava 10.
So BukkitForge ships with guava 10 in 'guava10.com.google.xxx' rather than 'com.google.xxx'.

OMG, LIKE, MAH PLGINS DNT LOAD!!!!!1111one!1eleven
==========================================

Speak english, and please, report it in issues!
Please review the new [Issues Posting Guidelines](https://github.com/keepcalm/BukkitForge/wiki/Issue-Posting-Guidelines)

LICENSE:
========
 
This mod is licensed under the LGPL, the same as Craftbukkit.
 
