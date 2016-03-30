# Flamewalk #

Buff spell. While the spell is active, any nearby enemies will be set on fire.

## Configuration ##

| **Option** | **Description** | **Default** |
|:-----------|:----------------|:------------|
| range      | The range, in blocks, of the spell's effect. | 8           |
| fire-ticks | The duration of the fire, in server ticks (20 ticks per second). | 80          |
| tick-interval | How often the spell sets nearby enemies on fire, in server ticks. | 100         |
| target-players | Whether the spell should target players. | false       |
| check-plugins | Whether the spell should check other plugins for PvP restrictions. Only applies if target-players is true. | true        |