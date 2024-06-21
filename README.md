# SCR Rich Presence

SCR RichPresence is an unofficial Discord RPC created specifically to be used while playing [Stepford County Railway](https://www.roblox.com/groups/3620943/Stepford-County-Railway#!/about). This RPC displays basic information such as the role you're playing (or if you're idling in the main menu), as well as more specific information such as in which station you're dispatching or what's your destination. You can find everything that's displayed by this RPC below [here](https://github.com/Movs22/SCR-RichPresence/tree/Movs22-patch-1?tab=readme-ov-file#information-displayed).
This program works by analysing your current Roblox window (by taking screenshots and processing them) and then getting information such as the current role and associated information by extracting text and detecting patterns in the game's UI. You should keep in mind that, because of this approach, the results will never be 100% correct. You should also keep in mind that the program sees just like a human so, if something is covering the UI, it won't be able to read it as reliably as it would if nothing was covering it (like a human)!
This program utilizes some 3rd party libraries, such as [Tess4J](https://github.com/nguyenq/tess4j) and [Discord-RPC](https://github.com/Vatuu/discord-rpc). These libraries come bundled with the application and don't have to be downloaded separately.
**This program does not interact directly with Roblox in any way, nor with any other application except Discord (which is necessary to update your custom activity) and, therefore, won't get you banned off Roblox.**

**This is an unofficial program made by a 3rd party and is in no way related to SCR and/or BanTech Systems. Any bugs should be reported to the specific developer ([Movies22](https://discord.com/users/896732255534338078)) and not to the SCR development team.**

## Limitations & Requirements
In order to run this program, you must meet the following requirements:
 - You must be using either Windows 10 or 11 (you may try to use older OSes, as long as they're able to run Java, however it's not guaranteed to work)
 - Roblox must be running as a **fullscreen** application, in your **primary monitor** and must be set to a **1920 by 1080** resolution. This is something that'll be changed in future versions to support more resolutions
 - Your driver HUD must be set to "small".

The program presents some limitations, such as:
 - This program will most likely break if there are any major changes to the UI (i.e. in 2.0) and, therefore, may be unusable for some days until everything is adjusted to the newer changes.
 - Although the text recognition models have been optimized to the maximum to provide the most accurate results, they're not 100% perfect and may produce inaccurate results, especially if the text in question that's being read is small and/or pixelated (i.e. train headcodes).
 - Although the program is able to recognize all activities within the game, it's recommended to start this program while idling in the main menu. If you start it while in a role it might not be able to detect your activity and will prompt you to go back to the main menu (through a push notification). This is especially the case if you start the application while driving/selecting a role, while signalling or while guarding a train (for the latter you can simply leave the train and re-request for the status to detect).
 - As stated above, the program isn't able to text and UIs that are overlapped, therefore you should avoid situations like that for the most accurate results (although, closing the members tab while guarding/dispatching and leaving the chat unfocused while selecting a route should suffice).

## Instalation & Running
>You should begin by heading over to the [releases](https://github.com/Movs22/SCR-RichPresence/releases) page and downloading either the .exe file or the .zip file. It's recommended to download the .exe unless you're planning on modifying the Tesseract data files (if you don't know what those are then you should probably download the .exe), or if you can't run .exe files.
### Running the .exe:
> If you chose to download the .exe then you can simply start the application by double clicking it! It'll start a command prompt window which also acts as the program's logger window. To stop this program simply do **control+c**.
> When you run the application for the first time, it'll create a **.SCR-RichPresence** folder in the same directory as the .exe (so if the application is under Downloads, this folder will also be created under the Downloads folder). This folder contains the trained data required by the text recognition part of the program. It also contains a version.txt file to keep track of the app's version.
  
### Running the .zip folder:
> The zip folder contains 3 files: the application itself (SCR-RichPresence.jar), a Java 18 JDK and a run.bat script. The JDK is used to run the .jar file and makes it possible to run the application even if you don't have java installed! You may, however, change the JDK and/or the run.bat file to your liking! 
> This should be as easy as running the .exe. You should first unzip the folder and then, to run the application, simply double click the **run.bat** file. Just like in the .exe, it'll open a command prompt window that acts as the program's logger. To stop the application simply do **control+c**.
> Also like the .exe, this program will create a **.SCR-RichPresence** folder, with the exception that both this folder, the SCR RichPresence app and the auxiliary files will all be located inside one folder.

### Uninstalling
> To uninstall the SCR RichPresence application you'll simply have to delete the .exe file and the respective .SCR-RichPresence folder or, if you downloaded the .zip file, the folder that contains the .jar file (and the other auxiliary files), its as simple as that!

## FaQ

**Q: I've got a prompt from Microsoft Defender**
> A: You can simply disregard it (by clicking "More info" and the "Run anyway"). Windows gives you this prompt when you run this application for the first time because the .exe file isn't signed (something that I can't do since I'd need a license and fill out paperwork, stuff that wouldn't be worth it for a small coding project like this). This application is safe to run and you're always free to inspect its source code or simply run the "portable" .zip installation instead!

**Q: Will this application be ported for Mac/Linux?**
> A: Currently I have no plans to port the application to other OSes, mainly due to the fact that MacOS is much stricter in terms of security and permissions and because something like this probably wouldn't be possible in other OSes.

**Q: SCR version X.X has been released and this doesn't work anymore. How long will it take for it to work again?**
> A: I can't give you an exact timeframe since it depends a lot on what was changed, however, I'm probably already rewriting this application to work on the new UI.

**Q: I've got a suggestion for this program. Where could I suggest it?**
> A: You can suggest it by either DMing me (@Movies22) or by sending it to my [discord server](https://discord.gg/YhVqqc2BsN)!

**Q: I've found a bug/the program doesn't work for me**
> A: You can DM me to get 1-1 help in DMs and/or to report bugs or you can report any bugs in my [discord server](https://discord.gg/YhVqqc2BsN).

**Anything else?**
> Feel free to ask in the [discord server](https://discord.gg/YhVqqc2BsN) or just drop me a DM!

## Information displayed
### While driving
 - Next station
 - Operator
 - Estimated time of arrival
 - Headcode
 - Destination
 - Delay
### While dispatching (Idle)
 - Current station
 - Public/Private server
### While dispatching a train
 - Current station
 - Train's headcode & current platform
### While guarding
 - Name & rank of the person driving the train
 - The train's next stop.
### While signalling
 - Current desk
 - Amount of trains in the zone covered by the current desk
 - Public/Private server
### While signalling (viewing a camera)
 - Current desk
 - ID of the camera being viewed
### Other scenarios picked up by the RPC
 - Idling in the main menu
 - Selecting a role
 - Exploring the map (as a passenger)
 - Exploring the map (in a supervisor+ role)

## Contributing
This is an open-source project and is open to contributions from anyone, as long as they're constructive and actually useful to the project!
To begin contributing you can simply clone this repository (recommended to use Github Desktop for that) and then import it in Eclipse. All of the necessary external jars can be found under the [jars folder](https://github.com/Movs22/SCR-RichPresence/tree/main/jars), although the .classpath file already links to them.
You can also find both the english traineddata and the headcodes traineddata under `src/com/movies22/scr/rpc/tessdata`. The first one is used globally to recognize anything from headcodes to numbers to the next station. The latter is a testing model specialized in train headcodes (although it isn't used at the moment).
**Make sure that your contributions don't affect the program's performance:** try to aim to keep the RAM usage below 250MBs.
Once you're done with your code you can simply create a pull request and it'll eventually be merged (or not)!
