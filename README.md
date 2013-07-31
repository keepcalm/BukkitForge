BukkitForge - a Minecraft mod that ports the Bukkit plugin API to minecraft.
===================================

This is very WIP. Since i started doing this, i was mostly aiming for a server that didn't crash.
It can load plugins, commands work, lots of things work, permissions *should* work, etc.

Most of this mod is a fairly straight port from Craftbukkit, but I've had to modify parts
(and i anticipate modifiying more).

This is ported to 1.6.2.

BukkitForge Team Members:
================
<a href="https://github.com/keepcalm">keepcalm</a>

keepbot (keepcalm's bot)

<a href="https://github.com/alexbegt">alexbegt</a>  

<a href="https://github.com/SpoonsJTD">SpoonsJTD</a>


IRC
===============
We have a irc, you can find it on irc.esper.net, #bukkitforge.

Jenkins? Jenkins? JENKINS!
==========================

That's right, now we have an automated jenkins build-thingy.
You may find it [here](http://build.technicpack.net/job/BukkitForge)

[![Build status](http://build.technicpack.net/job/BukkitForge/badge/icon)](http://build.technicpack.net/job/BukkitForge/)

GETTING IT WORKING IN MCP:
=========================

1. Download the MCP and Extract it.
2. Download Forge
3. Extract the minecraftforge-src-1.5.2-VersionHere.zip into your MCP folder and you will get a folder called Forge. If you see a install.cmd/install.sh your at the right place.
4. Make a folder called accesstransformers inside the Forge Folder. You will need to make this.
5. Put the bukkit_at.cfg into the Folder Accesstransformer inside forge.
6. Copy Everything from the Lib Folder into the Lib Folder in MCP.
7. Then run the install.cmd/install.sh in forge.
8. When its done decompiling, Open to the Src Folder from bukkitforge Copy everything into the folder src/minecraft.
9. Open Eclipse.
10. Open to Where you downloaded mcp/eclipse in Eclipse.
11. Right Click the Minecraft Porject and hit Properties.
12. Then go to the Java Build Path.
13. Click Libraries.
14. Then click Add Jars.
15. Then click on the lib folder.
16. And add the files: commons-lang-2.3, argo-2.25, ebean-2.8.1, gson-2.1-cb, guava10-renamed, jansi-1.9, snakeyaml-1.9, SpecialSource-1.3-SNAPSHOT, and sqlite-jdbc-3.7.2 to it.
17. You should have no errors at this time.


COMPILING (to get all that good stuff)
=======================

You need Apache Ant and the JDK. To build, run 'git submodule init' and 'git submodule update' in the root folder.
cd back up one level and simply type 'ant' in the folder with build.xml inside it.

INSTALLATION
=======================
This is a core mod, and the jar should live in the 'mods' directory of your server.

FOR BUKKIT DEVS WHO WANT TO ENSURE COMPATIBILITY
===============================================

If your mod does NOT use guava, you're fine, it should work.

If your mod DOES use guava, you need to add 'guava10' before all of your guava imports.
The reason this is required is FML uses guava 12 which is not compatible with guava 10.
So BukkitForge ships with guava 10 in 'guava10.com.google.xxx' rather than 'com.google.xxx'.

LICENSE:
========
 
This mod is licensed under the LGPL, the same as Craftbukkit.
 
