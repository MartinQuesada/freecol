# Cacao

Grow **cacao** on savannah, tropical forest, scrub forest and plains, then refine it into **chocolate** for Europe.

| Step | What |
|------|------|
| Farm | Cacao on warm tiles (bonus from **Cacao grove** resource) |
| Process | Chocolatier's house (**32 hammers**) → shop → factory (Adam Smith) |
| Experts | Expert Cacao Planter, Master Chocolatier |
| Local use | **Creole salon** (64 hammers, population 3): 3 chocolate → 2 bells per turn |

Aztec, Inca and Tupi settlements may teach cacao planting.

## Sell or conspire?

The creole salon works with no colonists: every turn it takes 3 chocolate
from the warehouse and produces 2 liberty bells. With at least 50% rebels in
the colony it produces 1 extra bell (2 extra at 100%) without using more
chocolate. If you would rather sell the chocolate, load it onto a wagon or
ship before the end of the turn.

The rebel bonus needs the `model.ability.rebelBonusUnattended` support in
this repository's `FreeCol.jar`; on a stock FreeCol build the salon still
works, just without the bonus.

## Native experts

With the **Conversos** mod and Bartolome de las Casas in the Congress,
converts working cacao fields can become **Native Cacao Planters**.

## Enable

Preferences → Mods → enable **Cacao**, then start a **new game**.

Or use `./bin/run-mods.sh`.
