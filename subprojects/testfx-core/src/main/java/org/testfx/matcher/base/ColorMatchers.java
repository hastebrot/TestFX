package org.testfx.matcher.base;

import javafx.scene.paint.Color;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.testfx.api.annotation.Unstable;
import static org.testfx.matcher.base.GeneralMatchers.typeSafeMatcher;

public class ColorMatchers {

    //---------------------------------------------------------------------------------------------
    // STATIC METHODS.
    //---------------------------------------------------------------------------------------------

    @Factory
    @Unstable(reason = "is missing apidocs")
    public static Matcher<Color> hasColor(Color color) {
        String descriptionText = "has color (" + color.toString() + ")";
        return typeSafeMatcher(Color.class, descriptionText,
                actualColor -> hasColor(actualColor, color));
    }

    //---------------------------------------------------------------------------------------------
    // PRIVATE STATIC METHODS.
    //---------------------------------------------------------------------------------------------

    private static boolean hasColor(Color actualColor,
                                    Color color) {
        return actualColor.equals(color);
    }

}
