# Teach #

This spell teaches another player a spell that you know. You cannot teach a spell you do not know. Additionally, if the target player cannot learn the spell (due to having Permissions enabled and not having the magicspells.learn.spell permission), you will not be able to teach the spell. Usage: `/cast teach <player> <spell>`.

## Configuration ##

| **Option** | **Description** | **Default** |
|:-----------|:----------------|:------------|
| str-usage  | The string that shows if the player casts the spell incorrectly. | Usage: /cast teach `<target> <spell>` |
| str-no-target | The string that shows if the caster types an invalid player name. | No such player |
| str-no-spell | The string that shows if the cast does not know the spell they typed. | You do not know a spell by that name. |
| str-cant-learn | The string that shows if the target cannot learn the spell. | That person cannot learn that spell. |
| str-already-known | The string that show if the target player already knows the spell. | That person already knows that spell. |
| str-cast-target | The message sent to the target upon learning the spell. | %a has taught you the %s spell. |

### String Formatting ###

| %a | The actor (the person casting the spell) |
|:---|:-----------------------------------------|
| %s | The spell name being taught              |