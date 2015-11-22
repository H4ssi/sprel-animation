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

        scenes.add(new Opening());
        scenes.add(new LightIntro());
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
            private final int start;
            private final Consumer<Integer> sceneSlice;

            private Builder() {
                this(0);
            }

            private Builder(int start) {
                this(start, (i) -> {
                });
            }

            private Builder(int start, Consumer<Integer> sceneSlice) {
                this.start = start;
                this.sceneSlice = sceneSlice;
            }

            public Builder then(Consumer<Integer> sceneSlice) {
                return new Builder(start, (i) -> {
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

            public Builder wait(int millis) {
                return new Builder(start + millis, sceneSlice);
            }

            public Builder duration(int millis) {
                Scene.this.show(start, millis, sceneSlice);
                return new Builder(millis);
            }

            public Builder when(int delay, int length) {
                return wait(delay).duration(length);
            }

            public Builder when(int delay) {
                return when(delay, 0);
            }

            public Builder when() {
                return duration(0);
            }
        }

        protected final Builder b = new Builder();
    }

    private class Opening extends Scene {
        public Opening() {
            b
                    .then(() -> {
                        background(0);
                        textFont(bigFont);
                        textAlign(CENTER);
                    })
                    .then(() -> text("Special Relativity", width / 2f, height / 2f)).when()
                    .then(() -> text("is hard", width / 2f, height / 1.5f)).when(1000)
                    .end().when(1000);
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
            }).when();

            small.then(() -> text("This is light", xText, height * 0.2f)).when(500);

            tiny.then(() -> text("Hello!", xLight, height * 0.45f)).when(250, 2000);

            small
                    .then(() -> text("Light does not give a single sh*t", xText, height * 0.6f)).when(750)
                    .then(() -> text("about nothing whatsoever", xText, height * 0.66f)).when(750)
                    .then(() -> text("its speed is always the same", xText, height * 0.8f)).when(750)
                    .then(() -> text("ALWAYS!!!111one", xText, height * 0.86f)).when(750);

            tiny
                    .then(() -> text("Gotta go fast!", xLight, height * 0.45f)).when(250, 500)
                    .end().when();
        }
    }

    private class Stationary extends Scene {
        public Stationary() {
            int wallOffset = 30;
            int mirrorWidth = 50;

            b.then(() -> background(0)).when(0)
                    .then(() -> {
                        stroke(0, 255, 0);
                        strokeWeight(4);
                        fill(0);
                        rect(10, 10, 300, 300);
                    })
                    .then(() -> {
                        fill(0, 255, 0);
                        text("This is a spaceship", width / 2, 350);
                    }).when(250)
                    .then(() -> {
                        stroke(100, 100, 100);
                        strokeWeight(8);
                        line(10 + wallOffset, 10 + wallOffset, 10 + wallOffset + mirrorWidth, 10 + wallOffset);
                        line(10 + 300 - wallOffset, 10 + 300 - wallOffset, 10 + 300 - wallOffset, 10 + 300 - wallOffset - mirrorWidth);
                    }).when(250)
                    .then(() -> {
                        fill(100, 100, 100);
                        text("These are mirrors", width / 2, 380);
                    }).when(250)
                    .then(() -> {
                        strokeWeight(0);
                        stroke(255, 0, 0);
                        fill(255, 0, 0);

                        triangle(10 + wallOffset + mirrorWidth / 2, 10 + 300, 10 + wallOffset, 10 + 300 - wallOffset, 10 + wallOffset + mirrorWidth, 10 + 300 - wallOffset);
                        triangle(10, 10 + 300 - wallOffset - mirrorWidth / 2, 10 + wallOffset, 10 + 300 - wallOffset - mirrorWidth, 10 + wallOffset, 10 + 300 - wallOffset);
                    }).when(250)
                    .then(() -> {
                        fill(255, 0, 0);
                        text("These are light sources", width / 2, 410);
                    }).when(250)
                    .then(() -> {
                        fill(255, 255, 0);
                        text("No let us fire a photon each", width / 2, 440);
                    }).when(250);

            float c = 75f / 1000f;
            float t = (300 - 2 * wallOffset - mirrorWidth) / c;

            // light to right
            b
                    .wait(1000)
                    .then((i) -> {
                        fill(255, 255, 0);
                        stroke(255, 255, 0);
                        ellipse(10 + wallOffset + mirrorWidth / 2 + i * c, 10 + 300 - wallOffset - mirrorWidth / 2, mirrorWidth, mirrorWidth);
                    })

                    // light up
                    .then((i) -> {
                        fill(255, 255, 0);
                        stroke(255, 255, 0);
                        ellipse(10 + wallOffset + mirrorWidth / 2, 10 + 300 - wallOffset - mirrorWidth / 2 - i * c, mirrorWidth, mirrorWidth);
                    }).duration((int) t)

                    // light left
                    .then((i) -> {
                        fill(255, 255, 0);
                        stroke(255, 255, 0);
                        ellipse(10 + 300 - wallOffset - mirrorWidth / 2 - i * c, 10 + 300 - wallOffset - mirrorWidth / 2, mirrorWidth, mirrorWidth);
                    })

                    // light down
                    .then((i) -> {
                        fill(255, 255, 0);
                        stroke(255, 255, 0);
                        ellipse(10 + wallOffset + mirrorWidth / 2, 10 + wallOffset + mirrorWidth / 2 + i * c, mirrorWidth, mirrorWidth);
                    }).duration((int) t)

                    // light back at start
                    .then(() -> {
                        fill(255, 255, 0);
                        stroke(255, 255, 0);
                        ellipse(10 + wallOffset + mirrorWidth / 2, 10 + 300 - wallOffset - mirrorWidth / 2, mirrorWidth, mirrorWidth);
                    }).duration(100)
                    .end().when();
        }
    }
}
