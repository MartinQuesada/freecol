# Conversos

In the base game Indian converts never improve, and Bartolome de las Casas
turns the converts you have at that moment into free colonists. This mod
replaces that one-off conversion with something closer to history: converts
learn the outdoor work they do every day, but they stay converts.

| | |
|--|--|
| Las Casas | Converts gain experience in outdoor jobs |
| Jobs | Grain, fish, furs, lumber, ore, silver, sugar, cotton, tobacco |
| Result | **Native expert** of that job (e.g. Native Expert Farmer) |
| Chance | Same as colonists: up to 4% per turn with full experience |
| Lost skill | A native expert who loses his skill goes back to being a convert |

## What a native expert can and cannot do

- Keeps the convert's outdoor bonus and adds the matching European expert's
  bonus on top: on 3 grain a convert makes 4, an expert farmer 5 and a native
  expert farmer 6.
- Still cannot found colonies, still loses 2 per turn in any building, and
  cannot teach in the schoolhouse.
- Converts do not learn crafts: no experience in buildings.

With the **Cacao**, **Vanilla** and **Tasajo** mods there are also native
cacao and vanilla planters, native hunters (meat) and native salt miners.

Converts you already have when Las Casas joins do **not** become free
colonists any more.

## Requirements

Needs this repository's `FreeCol.jar`: experience has to look for the expert a
unit can actually become, not only the European expert. On a stock FreeCol
build converts gain no experience.

Do not enable it together with the **convertUpgrade** mod: both use the Las
Casas ability.

## Enable

Preferences → Mods → enable **Conversos**, then start a **new game**.

Or use `./bin/run-mods.sh`.
