import processing.core.PApplet;
import processing.core.PFont;

import java.util.ArrayList;
import java.util.function.Consumer;

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

        //scenes.add(new Opening());
        //scenes.add(new LightIntro());
        scenes.add(new Stationary());
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

        private void show(int delay, int length, Consumer<Integer> run) {
            cur += delay;
            int off = cur;
            runs.add(() -> {
                if (start + off <= millis() && (length == 0 || millis() <= start + off + length)) {
                    run.accept(millis() - start - off);
                }
            });
        }

        public boolean isEnd() {
            return end;
        }

        protected class Builder {
            private final Consumer<Integer> sceneSlice;

            private Builder() {
                sceneSlice = (i) -> {
                };
            }

            private Builder(Consumer<Integer> sceneSlice) {
                this.sceneSlice = sceneSlice;
            }

            public Builder then(Consumer<Integer> sceneSlice) {
                return new Builder((i) -> {
                    Builder.this.sceneSlice.accept(i);
                    sceneSlice.accept(i);
                });
            }

            public Builder then(Runnable sceneSlice) {
                return then((i) -> sceneSlice.run());
            }

            public Builder end() {
                return then(() -> end = true);
            }

            public void when(int delay, int length) {
                Scene.this.show(delay, length, sceneSlice);
            }

            public void when(int delay) {
                Scene.this.show(delay, 0, sceneSlice);
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
            int xLight = 100;
            int xText = width / 2;

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

    private class Stationary extends Scene {
        public Stationary() {
            b.then(() -> background(0)).when(0);
            b.then(() -> {
                stroke(0, 255, 0);
                strokeWeight(4);
                fill(0);
                rect(10, 10, 300, 300);
            }).when(0);

            b.then(() -> {
                fill(0, 255, 0);
                text("This is a spaceship", width / 2, 350);
            }).when(250);

            int wallOffset = 30;
            int mirrorWidth = 50;

            b.then(() -> {
                stroke(100, 100, 100);
                strokeWeight(8);
                line(10 + wallOffset, 10 + wallOffset, 10 + wallOffset + mirrorWidth, 10 + wallOffset);
                line(10 + 300 - wallOffset, 10 + 300 - wallOffset, 10 + 300 - wallOffset, 10 + 300 - wallOffset - mirrorWidth);
            }).when(250);

            b.then(() -> {
                fill(100, 100, 100);
                text("These are mirrors", width / 2, 400);
            }).when(250);

            b.then(() -> {
                strokeWeight(0);
                stroke(255, 0, 0);
                fill(255, 0, 0);

                triangle(10 + wallOffset + mirrorWidth / 2, 10 + 300, 10 + wallOffset, 10 + 300 - wallOffset, 10 + wallOffset + mirrorWidth, 10 + 300 - wallOffset);
                triangle(10, 10 + 300 - wallOffset - mirrorWidth / 2, 10 + wallOffset, 10 + 300 - wallOffset - mirrorWidth, 10 + wallOffset, 10 + 300 - wallOffset);
            }).when(250);

            b.then(() -> {
                fill(255, 0, 0);
                text("These are light sources", width / 2, 450);
            }).when(250);

            float c = 75f / 1000f;

            float t = (300 - 2 * wallOffset - mirrorWidth) / c;

            b.then((i) -> {
                fill(255, 255, 0);
                stroke(255, 255, 0);
                ellipse(10 + wallOffset + mirrorWidth / 2 + i * c, 10 + 300 - wallOffset - mirrorWidth / 2, mirrorWidth, mirrorWidth);
            }).when(1000, (int) t);

            b.then((i) -> {
                fill(255, 255, 0);
                stroke(255, 255, 0);
                ellipse(10 + wallOffset + mirrorWidth / 2, 10 + 300 - wallOffset - mirrorWidth / 2 - i * c, mirrorWidth, mirrorWidth);
            }).when(0, (int) t);

            b.then((i) -> {
                fill(255, 255, 0);
                stroke(255, 255, 0);
                ellipse(10 + 300 - wallOffset - mirrorWidth / 2 - i * c, 10 + 300 - wallOffset - mirrorWidth / 2, mirrorWidth, mirrorWidth);
            }).when((int) t, (int) t);

            b.then((i) -> {
                fill(255, 255, 0);
                stroke(255, 255, 0);
                ellipse(10 + wallOffset + mirrorWidth / 2, 10 + wallOffset + mirrorWidth / 2 + i * c, mirrorWidth, mirrorWidth);
            }).when(0, (int) t);
        }
    }
}
