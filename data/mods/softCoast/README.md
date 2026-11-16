# softCoast

Experimental graphics mod: softer, more irregular beach masks so coastal
water looks a bit less blocky against land.

Does **not** remove the isometric grid stair-step; it only widens and
roughens the sand/water blend inside each ocean tile that touches land.

## Enable

Add `softCoast` to your mods list (after `hdGraphics` if you use it).

With the repo launcher:

```bash
./bin/run-mods.sh
```

(`softCoast` is included in the default mods of `bin/run-mods.sh`.)

## Disable

Remove `softCoast` from the mods list, or run without it:

```bash
MODS=libertadores,hdGraphics ./bin/run-mods.sh
```
