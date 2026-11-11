/**
 *  Copyright (C) 2002-2024  The FreeCol Team
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

import junit.framework.TestCase;


public class AIPlayStyleTest extends TestCase {

    public void testFromValueDefaultsToClassic() {
        assertEquals(AIPlayStyle.CLASSIC, AIPlayStyle.fromValue(0));
        assertEquals(AIPlayStyle.TRADER, AIPlayStyle.fromValue(1));
        assertEquals(AIPlayStyle.CONQUEROR, AIPlayStyle.fromValue(2));
        assertEquals(AIPlayStyle.INDEPENDENCE, AIPlayStyle.fromValue(3));
        assertEquals(AIPlayStyle.BUILDER, AIPlayStyle.fromValue(4));
        assertEquals(AIPlayStyle.CLASSIC, AIPlayStyle.fromValue(-1));
        assertEquals(AIPlayStyle.CLASSIC, AIPlayStyle.fromValue(99));
    }

    public void testClassicKeepsHarassmentBehaviour() {
        assertTrue(AIPlayStyle.CLASSIC.allowsWanderHostile());
        assertTrue(AIPlayStyle.CLASSIC.allowsSeekAndDestroy());
        assertFalse(AIPlayStyle.CLASSIC.prefersFewerColonies());
    }

    public void testPeacefulStylesAvoidHarassment() {
        assertFalse(AIPlayStyle.TRADER.allowsWanderHostile());
        assertFalse(AIPlayStyle.BUILDER.allowsWanderHostile());
        assertFalse(AIPlayStyle.INDEPENDENCE.allowsWanderHostile());
        assertFalse(AIPlayStyle.TRADER.allowsSeekAndDestroy());
        assertFalse(AIPlayStyle.BUILDER.allowsSeekAndDestroy());
        assertTrue(AIPlayStyle.TRADER.prefersFewerColonies());
        assertTrue(AIPlayStyle.BUILDER.prefersFewerColonies());
    }

    public void testAllStylesStillCompeteForNeededLand() {
        for (AIPlayStyle style : AIPlayStyle.values()) {
            assertTrue(style.allowsCompetitiveExpansion());
        }
        assertEquals(4, AIPlayStyle.BUILDER.preferredColonyCap());
        assertEquals(6, AIPlayStyle.INDEPENDENCE.preferredColonyCap());
    }

    public void testConquerorIsOffensive() {
        assertTrue(AIPlayStyle.CONQUEROR.allowsWanderHostile());
        assertTrue(AIPlayStyle.CONQUEROR.allowsSeekAndDestroy());
        assertFalse(AIPlayStyle.CONQUEROR.prefersFewerColonies());
    }
}
