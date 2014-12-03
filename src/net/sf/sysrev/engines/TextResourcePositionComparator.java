/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sf.sysrev.engines;

import java.util.Comparator;

/**
 * This class is a comparator for TextPosition operators.  It handles
 * pages with text in different directions by grouping the text based
 * on direction and sorting in that direction. This allows continuous text
 * in a given direction to be more easily grouped together.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.7 $
 */
public class TextResourcePositionComparator implements Comparator<Object>
{
    /**
     * {@inheritDoc}
     */
    public int compare(Object o1, Object o2)
    {
        int retval = 0;
        TextResourcePosition pos1 = null;
        TextResourcePosition pos2 = null;

        if (o1 instanceof TextResource) {
        	pos1 = (TextResourcePosition) ((TextResource) o1).getPosition();
        	pos2 = (TextResourcePosition) ((TextResource) o2).getPosition();
        } else if (o1 instanceof TextResourcePosition) {
        	pos1 = (TextResourcePosition) o1;
        	pos2 = (TextResourcePosition) o2;
        } else {
        	throw new IllegalArgumentException("Cannot compare objects (incompatible data type)");
        }

        if (pos1 == null) {
        	return -1;
        }

        if (pos2 == null) {
        	return 1;
        }

        /* Only compare text that is in the same direction. */
        if (pos1.getDirection() < pos2.getDirection())
        	return -1;
        else if (pos1.getDirection() > pos2.getDirection())
        	return 1;

        // Get the text direction adjusted coordinates
        float x1 = pos1.getAdjustedX();
        float x2 = pos2.getAdjustedX();

        float pos1YBottom = pos1.getAdjustedY();
        float pos2YBottom = pos2.getAdjustedY();
        // note that the coordinates have been adjusted so 0,0 is in upper left
        float pos1YTop = pos1YBottom - pos1.getAdjustedHeight();
        float pos2YTop = pos2YBottom - pos2.getAdjustedHeight();

        float yDifference = Math.abs( pos1YBottom-pos2YBottom);
        //we will do a simple tolerance comparison.
        if( yDifference < .1 ||
            (pos2YBottom >= pos1YTop && pos2YBottom <= pos1YBottom) ||
            (pos1YBottom >= pos2YTop && pos1YBottom <= pos2YBottom))
        {
            if( x1 < x2 )
            {
                retval = -1;
            }
            else if( x1 > x2 )
            {
                retval = 1;
            }
            else
            {
                retval = 0;
            }
        }
        else if( pos1YBottom < pos2YBottom )
        {
            retval = -1;
        }
        else
        {
            return 1;
        }
        return retval;
    }
}
