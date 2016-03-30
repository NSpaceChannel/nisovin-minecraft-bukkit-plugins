# Minion #

Buff spell. This spell summons a minion to fight for you. It currently does not work perfectly with all monster types. Skeletons and zombies work great. Spiders and Pigzombies will fight for you if you attack an enemy, but they will not follow you while they are passive. Spiders will follow you at night and underground, however.


## Configuration ##

| **Option** | **Description** | **Default** |
|:-----------|:----------------|:------------|
| mob-chances | The list of creature types and their chance to spawn when the spell is cast. | [75,Skeleton 25](Zombie.md) |
| prevent-sun-burn | Whether to prevent zombie and skeleton minions from catching fire in the sun. | true        |
| target-players | Whether to allow minions to target other players. | false       |