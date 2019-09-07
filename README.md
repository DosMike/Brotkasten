# Brotkasten

A simple plugin for BossBar and Chat broadcasts.

## Commands

#### Reload the coonfiguration with
`/brotkasten reload`  
`/brot reload`  
**Permission:** brotkasten.command.brotkasten (base command), brotkasten.command.brotkasten.reload (reload)

#### Set a custom boss bar (not in config)
`/bossbar set --color COLOR --time TIME  MESSAGE`  
`/bb set MESSAGE`  
**Permission:** brotkasten.command.bossbar (base command), brotkasten.command.bossbar.set (replace bossbar)  
**Arguments:**
* Color (Optional): One of White, Green, Blue, Red, Yellow, Pink, Purple
* Time (Optional): Countdown in seconds or 'infinite'
* Message: The text to display, supports &-Format codes

#### Resume the boss bar schedule
`/bossbar skip`  
`/bossbar next`  
`/bossbar continue`  
**Permission:** brotkasten.command.bossbar (base command), brotkasten.command.bossbar.skip (continue schedule)  

#### Mute and unmute the boss bar cycle
`/bossbar mute`   
`/bossbar hide`   
`/bossbar off`   
`/bossbar disable`   
`/bossbar unmute`   
`/bossbar show`   
`/bossbar on`   
`/bossbar enable`   
**Permission:** brotkasten.command.bossbar (base command), brotkasten.command.bossbar.mute (mute and unmute the bossbar)   
*This does not hide bossbars that are set by admins or commandblocks (or bosses)*

### Config
```
# Messages that will be displayed as boss bar
BossBar {
    # Messages, one message per entry
    # Message format: {Flags}Message
    # Flags:
    #  <from>% - where this bar starts
    #  <to>% - where this bar ends
    #  <time>sec or <time>min - how long to display the message
    #  <color> - color of the boss bar, white, green, red, blue, yellow, pink or purple
    #  <divs>ticks - the amount of subdivisions the bar shall have 0, 6, 10, 12 or 20
    # Message:
    #  Can use &-Formatting codes
    Messages=[
        "{100%, 0%, 10sec, green}&aThank you for using &rBrotkasten"
    ]
    # Delay between messages, if a boss bar finishes earlier the next one won't
    # play until this time has passed. If the bar is displayed for more than
    # the specified amount of seconds it will not be interrupted!
    MinDelay=30
}
# Messages that will be displayed in chat
Chat {
    # Time between messages
    Delay=30
    # Messages, one message per entry
    # You can create Markdown-Like links with (Text)[Action]
    # Action can be:
    #  - Command to execute
    #  - Link to open in external browser
    Messages=[
        "&9Cool broadcasting plugin for [Sponge](https://spongepowered.org)",
        "&eLost? Click [here](/spawn) &eto get back to spawn"
    ]
}
# It's strongly recommended to enable automatic version checking,
# This will also inform you about changes in dependencies.
# Set this value to true to allow this Plugin to check for Updates on Ore
VersionChecker=true
```

### External Connections

**[Version Checker](https://github.com/DosMike/SpongePluginVersionChecker)**  
This plugin uses a version checker to notify you about available updates.  
This updater is **disabled by default** and can be enabled in `config/brotkasten.conf`
by setting the value `VersionChecker` to `true`.  
If enabled it will asynchronously check (once per server start) if the Ore repository has any updates.  
This will *only print update notes into the server log*, no files are being downlaoded!