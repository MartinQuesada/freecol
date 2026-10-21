# Conversos

In the base game Indian converts never improve, and Bartolome de las Casas
turns the converts you have at that moment into free colonists. This mod
replaces that one-off conversion with something closer to history: after
Las Casas joins the Congress, converts learn by experience (and school),
but they stay converts.

| | |
|--|--|
| Las Casas | Converts gain experience in outdoor jobs **and** guild crafts |
| Outdoor | Grain, fish, furs, lumber, ore, silver, sugar, cotton, tobacco → **native expert** (experience or school) |
| Guild | Hammers, rum, cloth, cigars, coats, tools, muskets → **native apprentice** (ceiling; experience or school) |
| Chance | Outdoor up to 4% / guild up to 3% per turn with full experience |
| Lost skill | Native experts and apprentices who lose skill go back to convert |

## What a native expert / apprentice can and cannot do

- Outdoor experts keep the convert's outdoor bonus and add the matching
  European expert's bonus on top: on 3 grain a convert makes 4, an expert
  farmer 5 and a native expert farmer 6.
- Guild apprentices get a modest indoor bonus (+2) that offsets part of the
  convert's building penalty; they do **not** become European masters.
- Still cannot found colonies, still lose production in buildings they are
  not trained for, and cannot teach in the schoolhouse.

With the **Cacao**, **Vanilla** and **Tasajo** mods there are also native
cacao and vanilla planters, native hunters (meat) and native salt miners.

Converts you already have when Las Casas joins do **not** become free
colonists any more.

## Requirements

Needs this repository's `FreeCol.jar`: experience looks for the expert a
unit can actually become, and education respects per-change scopes (Las
Casas). On a stock FreeCol build converts gain no experience.

With **Apprentices**, load **Apprentices before Conversos** (as
`./bin/run-mods.sh` does). If Conversos is listed first, Apprentices
wipes convert school paths and converts cannot be assigned to teachers.

Do not enable it together with the **convertUpgrade** mod: both use the Las
Casas ability.

## School

Converts do not enter the school building (that seat is for the teacher).
They stay on a tile or in another building and are linked to the teacher.

1. Elect **Las Casas** (otherwise convert school paths are locked).
2. Enable **Allow student selection** (Preferences → Game options → Colony),
   or start a new game with this mod (it turns that option on by default).
3. Right-click the convert → **Assign to teacher**.
4. Match the teacher to the skill: a fisherman teaches fishing (native expert
   fisherman), a farmer farming, a master carpenter the native carpenter
   apprentice, and so on.

## Enable

Preferences → Mods → Conversos, or `./bin/run-mods.sh`.
