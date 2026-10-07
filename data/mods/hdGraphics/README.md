# HD Graphics

Graphics only: no rule changes.

| | |
|--|--|
| Units | All 64 base unit images (colonists, experts, soldiers, dragoons, scouts, pioneers, missionaries, native and royal units, ships, artillery and wagons) redrawn as detailed paintings |
| Settlements | Sharper native settlements |
| Founding fathers | New oil-painting portraits for all 48 fathers, including the Libertadores ones |
| Terrain | Wider, irregular transitions between terrain types instead of a hard edge |

Each image keeps the size and position of the original on the map, and
comes with larger versions (`.size2`, `.size4`…) that the game uses when
zoomed in or on high-resolution screens. The larger versions need FreeCol to
run with at least 2 GB of memory (`-Xmx2G`, as `./bin/run-mods.sh` does).

Units added by other mods (cacao planters, hunters, salt miners…) and attack
animations keep their original art.

## Enable

Preferences → Mods → enable **HD Graphics**. Put it **last** in the list so
its images replace those of the other mods. `./bin/run-mods.sh` already
does.

## Credits

The art was generated with AI image models, using the GPL FreeCol sprites
as reference for the units and settlements. Released under the GPL like the
rest of FreeCol.
