/**
 *  Copyright (C) 2002-2024   The FreeCol Team
 *
 *  This file is part of FreeCol.
 *
 *  FreeCol is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  FreeCol is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with FreeCol.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.freecol.common.option;


/**
 * Selectable play styles for European AI players.
 *
 * {@link #CLASSIC} preserves the historical FreeCol AI behaviour
 * (nation-type driven aggression, surplus troops wander hostile, no
 * independence declaration).  Other styles override that baseline.
 *
 * Styles are meant to pursue a goal while still competing for needed
 * land and wartime objectives.  Aimless map harassment
 * ({@link #allowsWanderHostile()}) is what peaceful styles suppress,
 * not self-defence or clearing blockers of chosen colony sites.
 */
public enum AIPlayStyle {

    /** Current FreeCol AI behaviour. */
    CLASSIC(0),

    /** Prefer trade, missions and developed colonies; avoid needless wars. */
    TRADER(1),

    /** Prefer military expansion and attacking rivals and natives. */
    CONQUEROR(2),

    /** Build toward declaring independence and fighting the REF. */
    INDEPENDENCE(3),

    /** Prefer developing existing colonies over founding and needless wars. */
    BUILDER(4);


    private final int value;


    AIPlayStyle(int value) {
        this.value = value;
    }


    /**
     * The integer value stored in {@link GameOptions#AI_PLAY_STYLE}.
     *
     * @return The option value.
     */
    public int getValue() {
        return value;
    }

    /**
     * Resolve a stored option value to a play style.
     *
     * @param value The integer from the game options.
     * @return The matching style, or {@link #CLASSIC} if unknown.
     */
    public static AIPlayStyle fromValue(int value) {
        for (AIPlayStyle style : values()) {
            if (style.value == value) return style;
        }
        return CLASSIC;
    }

    /**
     * Whether surplus military units should wander looking for trouble
     * with no specific objective.
     *
     * @return True if wander-hostile missions are allowed.
     */
    public boolean allowsWanderHostile() {
        switch (this) {
        case TRADER: case BUILDER: case INDEPENDENCE:
            return false;
        case CONQUEROR: case CLASSIC: default:
            return true;
        }
    }

    /**
     * Whether the AI should opportunistically attack settlements / units
     * beyond defence, counter-attack and goal-driven expansion contests.
     *
     * @return True if broad seek-and-destroy missions are preferred.
     */
    public boolean allowsSeekAndDestroy() {
        switch (this) {
        case TRADER: case BUILDER:
            return false;
        case CONQUEROR: case INDEPENDENCE: case CLASSIC: default:
            return true;
        }
    }

    /**
     * Whether the AI should still compete for land and fight wars that
     * matter to its goal (blocked colony sites, existing wars, REF).
     *
     * All styles compete; they differ in how often they start needless fights.
     *
     * @return True if goal-driven military competition is allowed.
     */
    public boolean allowsCompetitiveExpansion() {
        return true;
    }

    /**
     * Whether privateering should be preferred for naval units.
     *
     * @return True if privateer missions are preferred.
     */
    public boolean allowsPrivateer() {
        switch (this) {
        case BUILDER: case INDEPENDENCE:
            return false;
        case TRADER: case CONQUEROR: case CLASSIC: default:
            return true;
        }
    }

    /**
     * Whether founding additional colonies should be constrained once
     * a small viable colonial base exists.
     *
     * @return True if expansion should be limited in favour of development.
     */
    public boolean prefersFewerColonies() {
        switch (this) {
        case TRADER: case BUILDER: case INDEPENDENCE:
            return true;
        case CONQUEROR: case CLASSIC: default:
            return false;
        }
    }

    /**
     * Soft cap on colony count for styles that prefer development.
     *
     * @return Maximum colonies before builders stop, or a large value
     *     when uncapped.
     */
    public int preferredColonyCap() {
        switch (this) {
        case BUILDER:
            return 4;
        case TRADER:
            return 5;
        case INDEPENDENCE:
            // Needs a stronger base before facing the REF.
            return 6;
        case CONQUEROR: case CLASSIC: default:
            return Integer.MAX_VALUE;
        }
    }
}
