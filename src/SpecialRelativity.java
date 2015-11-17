import processing.core.PApplet;
import processing.core.PFont;

import java.util.ArrayList;

public class SpecialRelativity extends PApplet {
    private ArrayList<Scene> scenes = new ArrayList<>();

    private PFont big;
    private PFont small;
    private PFont tiny;

    @Override
    public void settings() {
        super.settings();
        size(480, 480);
    }

    @Override
    public void setup() {
        big = loadFont("FiraSans-Regular-48.vlw");
        small = loadFont("FiraSans-Regular-20.vlw");
        tiny = loadFont("FiraSans-Regular-16.vlw");

        scenes.add(new Opening());
        scenes.add(new LightIntro());
    }

    @Override
    public void draw() {
        textFont(big);
        textAlign(CENTER);

        for (Scene scene : scenes) {
            if (!scene.isEnd()) {
                scene.draw();
                if (!scene.isEnd()) {
                    break;
                }
            }
        }
    }

    private abstract class Scene {
        private boolean end;
        private Integer start;
        private int cur;
        private ArrayList<Runnable> runs = new ArrayList<>();

        public void draw() {
            if (start == null) {
                start = millis();
            }

            runs.forEach(Runnable::run);
        }

        public void show(final int delay, final int length, final Runnable run) {
            cur += delay;
            final int off = cur;
            runs.add(() -> {
                if (start + off <= millis() && (length == 0 || millis() <= start + off + length)) {
                    run.run();
                }
            });
        }

        public void show(int delay, Runnable run) {
            show(delay, 0, run);
        }

        protected void end() {
            end = true;
        }

        public boolean isEnd() {
            return end;
        }
    }

    private class Opening extends Scene {
        public Opening() {
            background(0);

            show(0, () -> text("Special Relativity", width / 2f, height / 2f));

            show(1000, () -> text("is hard", width / 2f, height / 1.5f));

            show(1000, this::end);
        }
    }

    private class LightIntro extends Scene {
        public LightIntro() {
            final int xLight = 100;
            final int xText = width / 2;

            show(0, () -> {
                background(0);
                fill(255, 255, 0);
                ellipse(xLight, height * 0.5f, 20, 20);
            });

            show(500, () -> text("This is light", xText, height * 0.2f));

            show(250, 2000, () -> {
                textFont(tiny);

                text("Hello!", xLight, height * 0.45f);
            });

            show(750, () -> {
                textFont(small);

                text("Light does not give a single sh*t", xText, height * 0.6f);
            });
            show(750, () -> text("about nothing whatsoever", xText, height * 0.66f));
            show(750, () -> text("its speed is always the same", xText, height * 0.8f));
            show(750, () -> text("ALWAYS!!!111one", xText, height * 0.86f));

            show(250, 500, () -> {
                textFont(tiny);

                text("Gotta go fast!", xLight, height * 0.45f);
            });

            show(0, this::end);
        }
    }
}
