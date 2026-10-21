# HD Graphics

Graphics only: no rule changes.

| | |
|--|--|
| Units | All 64 base unit images (colonists, experts, soldiers, dragoons, scouts, pioneers, missionaries, native and royal units, ships, artillery and wagons) redrawn as detailed paintings; Libertadores mod experts too (cacao, livestock, tasajo, vanilla, conversos, apprentices) |
| Goods | All base goods icons plus Libertadores mod goods (cacao, chocolate, vanilla, milk, cheese, wool, cows, sheep, salt, salt meat) redrawn in the same painted style |
| Buildings | All base colony building icons plus Deeper Buildings, Cacao, Livestock, Taverns and Tasajo, in a painted colonial diorama style; stockade/fort/fortress/citadel outside backgrounds too |
| Settlements | Sharper native settlements |
| Founding fathers | New oil-painting portraits for all 50 fathers, including the Libertadores ones |
| Terrain | Wider, irregular transitions between terrain types instead of a hard edge |

Each image keeps the size and position of the original on the map, and
comes with larger versions (`.size2`, `.size4`…) that the game uses when
zoomed in or on high-resolution screens. The larger versions need FreeCol to
run with at least 2 GB of memory (`-Xmx2G`, as `./bin/run-mods.sh` does).

Libertadores mod expert portraits (cacao, chocolate, dairy, shepherd, cheese,
hunter, salt, vanilla, conversos native experts, and Apprentices European
specialty portraits) are included. Attack animations use the base FreeCol
sprites for now.

## Enable

Preferences → Mods → enable **HD Graphics**. Put it **last** in the list so
its images replace those of the other mods. `./bin/run-mods.sh` already
does.

## Credits

The art was generated with AI image models, using the GPL FreeCol sprites
as reference for the units and settlements. Released under the GPL like the
rest of FreeCol.
