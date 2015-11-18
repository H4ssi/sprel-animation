import processing.core.PApplet;
import processing.core.PFont;

import java.util.ArrayList;

public class SpecialRelativity extends PApplet {
    private ArrayList<Scene> scenes = new ArrayList<>();

    private PFont bigFont;
    private PFont smallFont;
    private PFont tinyFont;

    @Override
    public void settings() {
        super.settings();
        size(480, 480);
    }

    @Override
    public void setup() {
        bigFont = loadFont("FiraSans-Regular-48.vlw");
        smallFont = loadFont("FiraSans-Regular-20.vlw");
        tinyFont = loadFont("FiraSans-Regular-16.vlw");

        scenes.add(new Opening());
        scenes.add(new LightIntro());
    }

    @Override
    public void draw() {
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

        private void show(final int delay, final int length, final Runnable run) {
            cur += delay;
            final int off = cur;
            runs.add(() -> {
                if (start + off <= millis() && (length == 0 || millis() <= start + off + length)) {
                    run.run();
                }
            });
        }

        public boolean isEnd() {
            return end;
        }

        protected class Builder {
            private final Runnable runnable;

            private Builder() {
                runnable = () -> {
                };
            }

            private Builder(Runnable runnable) {
                this.runnable = runnable;
            }

            public Builder then(Runnable runnable) {
                return new Builder(() -> {
                    Builder.this.runnable.run();
                    runnable.run();
                });
            }

            public Builder end() {
                return then(() -> end = true);
            }

            public void when(int delay, int length) {
                Scene.this.show(delay, length, runnable);
            }

            public void when(int delay) {
                Scene.this.show(delay, 0, runnable);
            }
        }

        protected final Builder b = new Builder();
    }

    private class Opening extends Scene {
        public Opening() {
            b.then(() -> {
                background(0);
                textFont(bigFont);
                textAlign(CENTER);
            }).when(0);
            b.then(() -> text("Special Relativity", width / 2f, height / 2f)).when(0);
            b.then(() -> text("is hard", width / 2f, height / 1.5f)).when(1000);
            b.end().when(1000);
        }
    }

    private class LightIntro extends Scene {
        public LightIntro() {
            final int xLight = 100;
            final int xText = width / 2;

            Builder small = b.then(() -> textFont(smallFont));
            Builder tiny = b.then(() -> textFont(tinyFont));

            b.then(() -> {
                background(0);
                fill(255, 255, 0);
                ellipse(xLight, height * 0.5f, 20, 20);
            }).when(0);

            small.then(() -> text("This is light", xText, height * 0.2f)).when(500);

            tiny.then(() -> text("Hello!", xLight, height * 0.45f)).when(250, 2000);

            small.then(() -> text("Light does not give a single sh*t", xText, height * 0.6f)).when(750);
            small.then(() -> text("about nothing whatsoever", xText, height * 0.66f)).when(750);
            small.then(() -> text("its speed is always the same", xText, height * 0.8f)).when(750);
            small.then(() -> text("ALWAYS!!!111one", xText, height * 0.86f)).when(750);

            tiny.then(() -> text("Gotta go fast!", xLight, height * 0.45f)).when(250, 500);

            b.end().when(250);
        }
    }
}
