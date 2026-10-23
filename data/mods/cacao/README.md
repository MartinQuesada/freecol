# Cacao

Grow **cacao** on savannah, tropical forest, scrub forest and plains, then
refine it with **milk** into **chocolate** for Europe. Milk comes from the
**Livestock** mod (enable both).

| Step | What |
|------|------|
| Farm | Cacao on warm tiles (bonus from **Cacao grove** resource) |
| Process | Chocolatier's house (**32 hammers**) → shop → factory (Adam Smith) |
| Experts | Expert Cacao Planter, Master Chocolatier. In the colony a free colonist becomes an apprentice first, then the expert (3 + 4 turns). |
| Chocolate | Chocolatier house/shop/factory: cacao + milk → chocolate (1:1 inputs) |
| Local use | **Creole salon** (64 hammers, population 3): 3 chocolate → 2 bells per turn |

Aztec and Maya settlements may teach cacao planting and the master chocolatier, to a free colonist or an indentured servant. Tupi settlements may teach cacao planting only. That, together with experience in the workshop, is where the first master comes from. Once a colony has one, the school trains the next (apprentice, then master). He does not appear on the Europe dock. Inca settlements keep their other planting and teach neither cacao nor chocolate.

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
