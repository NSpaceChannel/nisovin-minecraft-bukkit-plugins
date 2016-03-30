# Explode #

Instant spell. This spell causes an explosion at your targeted location. Please note that this explosion will not obey any area restrictions provided by other plugins. This functionality is planned, but is not yet implemented, so be careful giving this spell to your players.


## Configuration ##

[General Spell Configuration](SpellConfiguration.md)

| **Option** | **Description** | **Default** |
|:-----------|:----------------|:------------|
| check-plugins | Whether to check other plugins for allowed TNT explosion and PvP damage. | true        |
| explosion-size | The size of the explosion. Size 4 is a regular TNT explosion. | 4           |
| backfire-chance | The chance the spell will backfire and explode where you are standing. Chance is out of 10000. | 0           |
| check-plugins | Whether to check plugins for allowed TNT explosions. | true        |
| str-no-target | Message that appears if there is no valid target (looking at sky, too far away, etc) | Cannot explode there. |