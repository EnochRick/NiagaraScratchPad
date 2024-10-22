# How to SFTP into a busted jace 8000 that wont boot to grab a backup of the station

*please note that this is just advice and not meant to be technical support.  if you use this information at all, you assume all risk assocaited with your actions.  I hope this helps you, but it is not a promise of success.  if you're having issues, please contact your local rep or tridium for support.*

1) Follow the serial shell instructions in the "Niagara JACE-XXXX Install and Startup Guide.pdf"

2) Authenticate and log into the shell

3) Configure your Laptop to be able to access the JACE at its current IP:
    **Example:** If your jace is at `10.0.0.20` with a subnet mask of `255.255.255.0`, then set your laptop up to use a static ip of `10.0.0.21` with the same subnet mask.  Then direct connect the jace to your laptop ethernet port. 

5) Enable the SFTP, use the default port of 22 in the serial shell of the jace.  it'll warn you to 

6) Open a windows command prompt once SFTP is enabled on the JACE

7) in the windows command prompt, type `sftp admin@10.0.0.20` (this assumes your platform account is named "admin" and you have its password)

8) Accept the fingerprint message by typing "yes" when prompted

9) You should be looking at a `sftp` prompt at this point.  You can use the command `ls` to list directory contents (the equivalent dos command is `dir`, but this is linux so you'll use `ls` from here on out).  change directorys by using `cd` like you would in DOS.  Navigate to the `/home/niagara/stations` folder

10) use the `get -R XXXX` command where `XXXX` is the name of your station directory.  the `-R` parameter for the `get` command allows you to copy everything recursively from that directory and all its subsequent subdirectories and files.

11) if you started your command prompt in windows, the default launch directory for the command prompt is your "C:\user\ *your user name here* \ " This is where you'll find a copy of the station you just made. 

# Please note:

Though i've not done this yet with a JACE 9000 - it looks like they have a backup command right from the shell!  Thats awesome! 

The new shell has these options:


`---------------------------------------------------
1   Update System Time
2   Update Network Settings
3   Ping Host
4   System Diagnostic Options
5   Change Current User Password
6   Change System Passphrase
7   Create SD Backup
8   Restore SD Backup
9   Reboot
L   Logout`

`Enter Choice :
`
