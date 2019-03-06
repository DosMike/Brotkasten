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
```

### This plugin has no metrics or other connections to third party services