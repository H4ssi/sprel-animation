import processing.core.PApplet;
import processing.core.PFont;

import java.util.ArrayList;

public class SpecialRelativity extends PApplet {

    ArrayList<Scene> scenes = new ArrayList<>();

    PFont big;
    PFont small;
    PFont tiny;

    @Override
    public void settings() {
        super.settings();
        size(480, 480);
    }

    public void setup() {
        big = loadFont("FiraSans-Regular-48.vlw");
        small = loadFont("FiraSans-Regular-20.vlw");
        tiny = loadFont("FiraSans-Regular-16.vlw");

        scenes.add(new Opening());
        scenes.add(new LightIntro());
    }

    public void draw() {
        textFont(big);
        textAlign(CENTER);

        long m = millis();
        for (Scene scene : scenes) {
            if (!scene.end) {
                scene.draw();
                if (!scene.end) {
                    break;
                }
            }
        }
    }

    private abstract class Scene {
        boolean end;
        int m;

        Integer start;

        public void draw() {
            if (start == null) {
                start = millis();
            }

            runs.forEach(Runnable::run);
        }

        int cur = 0;
        ArrayList<Runnable> runs = new ArrayList<>();

        public void show(final int delay, final int length, final Runnable run) {
            cur += delay;
            final int off = cur;
            runs.add(() -> {
                        if (start + off <= millis() && (length == 0 || millis() <= start + off + length)) {
                            run.run();
                        }
                    }
            );
        }
    }

    private class Opening extends Scene {

        public Opening() {
            background(0);

            show(0, 0, () -> text("Special Relativity", width / 2f, height / 2f)
            );

            show(1000, 0, () -> text("is hard", width / 2f, height / 1.5f)
            );

            show(1000, 0, () -> end = true
            );
        }
    }

    private class LightIntro extends Scene {
        public LightIntro() {

            final int xLight = 100;
            final int xText = width / 2;

            show(0, 0, () -> {
                        background(0);
                        fill(255, 255, 0);
                        ellipse(xLight, height * 0.5f, 20, 20);
                    }
            );

            show(500, 0, () -> text("This is light", xText, height * 0.2f)
            );

            show(250, 2000, () -> {
                        textFont(tiny);

                        text("Hello!", xLight, height * 0.45f);
                    }
            );

            show(750, 0, () -> {
                        textFont(small);

                        text("Light does not give a single sh*t", xText, height * 0.6f);
                    }
            );
            show(750, 0, () -> text("about nothing whatsoever", xText, height * 0.66f)
            );
            show(750, 0, () -> text("its speed is always the same", xText, height * 0.8f)
            );
            show(750, 0, () -> text("ALWAYS!!!111one", xText, height * 0.86f)
            );
        }
    }
}
