# Bind #

Command spell. This spell allows a player to override the default cast-item (wand) binding. To use, a player must be holding an item in their hand and type /cast bind 

&lt;spellname&gt;

. Custom bindings are stored in the player's file in the spellbooks folder.

## Configuration ##

| **Option** | **Description** | **Default** |
|:-----------|:----------------|:------------|
| str-usage  | The usage text that appears when the spell is not given any parameters. | You must specify a spell name and hold an item in your hand. |
| str-no-spell | Error message when player specifies a spell they don't have or doesn't exist. | You do not know a spell by that name. |
| str-cant-bind | Error message when a player specifies a spell that can't be bound to an item (can-cast-with-item is false). | That spell cannot be bound to an item. |