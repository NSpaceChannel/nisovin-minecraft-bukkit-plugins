# General Configuration #

There are several configuration options under the "general" header in the config file that will change the way the plugin functions.

| **Option** | **Description** | **Default** |
|:-----------|:----------------|:------------|
| str-cast-usage | The message that appears when someone uses the /cast command without any arguments. | Usage: /cast 

&lt;spell&gt;

. Use /cast list to see a list of spells. |
| str-unknown-spell | Message that appears when a player is trying to cast a spell they don't know or doesn't exit. | You do not know a spell with that name. |
| str-spell-change | String that appears when a player right clicks to cycle through spells assigned to the wand they are holding. | You are now using the %s spell. |
| str-on-cooldown | String that appears when a player tries to cast a spell on cooldown. | That spell is on cooldown (%c seconds remaining). |
| str-missing-reagents | Message when a player is trying to cast a spell without reagents. | You do not have the reagents for that spell. |
| str-cant-cast | Message when Permissions has prevented a player from casting a spell. Caused by a missing magicspells.cast.spellname permission node. | You can't cast that spell right now. |
| str-console-name | The name used when casting teach or forget from the console. | Admin       |
| text-color | The color number code for all text used by the plugin, found here: ![http://www.minecraftwiki.net/images/4/4c/Colors.png](http://www.minecraftwiki.net/images/4/4c/Colors.png). | 3           |
| broadcast-range | The default distance that spell strings are broadcast from the casting player to other players nearby. | 20          |
| enable-permissions | Whether to use the Permissions plugin. If this is true, all Permissions options apply. | false       |
| cast-for-free | A list of players who can cast spells without reagents. | (some random fake names) |
| freecast-no-cooldown | Whether the list of players in cast-for-free list can also cast without cooldown. | true        |
| ignore-default-bindings | Whether to ignore the cast-item settings for all spells. Useful if you just want players to use the bind spell to set up cast items. | false       |
| los-transparent-blocks | The list of blocks to use as transparent when checking for line-of-sight. | (some transparent block ids) |

## Mana System ##

The following options are under the "mana" subheading under "general" and apply to the mana system.

| **Option** | **Description** | **Default** |
|:-----------|:----------------|:------------|
| enable-mana-bars | Whether to enable the mana system. | true        |
| max-mana   | The maximum mana points. | 100         |
| mana-bar-prefix | The text that precedes the mana bar when viewing mana. | Mana:       |
| mana-bar-size | The number of characters in the mana bar. | 35          |
| color-full | The color of the filled portion of the mana bar. | 10 (green)  |
| color-empty | The color of the empty portion of the mana bar. | 0 (black)   |
| regen-tick-seconds | How often mana regenerates for players, in seconds. | 5           |
| regen-percent | How much mana regenerates per tick, as a percentage of max mana. | 5           |
| show-mana-on-use | Whether to show the mana bar when mana is used on a spell. | false       |
| show-mana-on-regen | Whether to show the mana bar when mana is regenerated. | false       |
| show-mana-on-wood-tool | If this is enabled, the current mana percentage will be shown by updating the durability on a wooden tool in the inventory. | true        |
| tool-slot  | The inventory slot of the tool to use to show mana, if the above is enabled. | 8 (last hotbar slot) |

## Spell Options ##

The spell options can be seen on the [spell configuration](SpellConfiguration.md) page, as well as configuration options for individual spells on their own pages, which can be accessed through the [spell list](SpellList.md).