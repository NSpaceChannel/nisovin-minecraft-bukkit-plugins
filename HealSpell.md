# Heal #

Instant spell. This spell heals a target player for a configured amount.

## Configuration ##

| **Option** | **Description** | **Default** |
|:-----------|:----------------|:------------|
| heal-amount | The amount to heal, in half-hearts. | 10          |
| obey-los   | Whether to obey line-of-sight. | true        |
| str-no-target | Message that appears if there is no target to heal. | No target to heal. |
| str-max-health | Message that appears if the target player is already at full health. | %t is already at full health. |
| str-cast-target | Message sent to targeted player upon being healed. | %a healed you. |