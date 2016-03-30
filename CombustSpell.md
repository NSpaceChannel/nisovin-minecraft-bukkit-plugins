# Combust #

Instant spell. This spell sets your target on fire.

## Configuration ##

| **Option** | **Description** | **Default** |
|:-----------|:----------------|:------------|
| target-players | Whether this spell can target players. | false       |
| fire-ticks | The duration the target is on fire, in ticks (20 ticks per second). | 100         |
| obey-los   | Whether to obey line-of-sight. | true        |
| check-plugins | Whether to check plugins for allowed PvP damage. Only applies if target-players is true. | true        |
| str-no-target | The message that shows when there is no target to set on fire. | (empty)     |