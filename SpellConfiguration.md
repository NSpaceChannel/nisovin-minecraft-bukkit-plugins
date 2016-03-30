# Spell Configuration #

Each spell has three different types of configuration options.
  1. General configuration options that all spells have.
  1. Configuration options that all spells of the spell type have (instant, buff).
  1. Specific configuration options unique to the spell.


## General Spell Configuration Options ##

The following configuration options are available for all spells.

| **Option** | **Description** | **Default** |
|:-----------|:----------------|:------------|
| enabled    | Whether to enable this spell. If you don't want to use this spell, set this to false to save system resources. | true        |
| name       | The name used in-game for the spell, if you don't like the default name | (the default name) |
| description | A description for this spell, which will appear when using the help spell. | (empty)     |
| cast-item  | The wand item used to cast the spell. | 280         |
| cooldown   | The spell cooldown, in seconds. | 0           |
| cost       | A list of reagent costs. Each item in the list must have an item id, a quantity, and optionally a data value, in this format: `<itemid[:data]> <quantity>`. Health and mana costs can also be specified by using "health" or "mana" in place of the itemid. For example: `mana 10`. | (none)      |
| str-cost   | A string that describes the cost, for easy reading. Appears when using the help spell. | (empty)     |
| str-cast-self | The message sent to the player casting the spell. | (empty)     |
| str-cast-others | The message sent to nearby players when a player casts a spell. | (empty)     |
| can-cast-with-item | Whether the spell can be cast with a wand item. Always false for command spells. | true        |
| can-cast-by-command | Whether the spell can be cast by using the `/cast` command. Always true for command spells. | true        |

## Instant Spell Options ##

The following options apply to instant spells only.

| **Option** | **Description** | **Default** |
|:-----------|:----------------|:------------|
| range      | The range this spell can be used, in blocks. Can also be a radius of effect, depending on the spell. Some spells have a capped range, no matter what this value is. | 0 (infinite range) |

## Buff Spell Options ##

The following options apply to buff spells only.

| **Options** | **Description** | **Default** |
|:------------|:----------------|:------------|
| num-uses    | How many uses this spell has before it expires. | 0 (infinite) |
| use-cost    | The reagent cost that is regularly consumed while this spell is active. Follows the same pattern as the cost option. | (none)      |
| use-cost-interval | The number of uses between each time the use-cost is charged to the player. | 0           |
| duration    | The duration of the spell, in seconds. | 0 (infinte) |
| str-fade    | The string that appears to the player when the spell effect wears off, either by using all the uses or when the duration is up, or when deactivated manually by the player. | (empty)     |

## Individual Spell Options ##

Each spell also has individual options. See the [spell list](SpellList.md) for more information.