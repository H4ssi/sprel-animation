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
        size(1280, 480);
    }

    @Override
    public void setup() {
        bigFont = loadFont("FiraSans-Regular-48.vlw");
        smallFont = loadFont("FiraSans-Regular-20.vlw");
        tinyFont = loadFont("FiraSans-Regular-16.vlw");

        //scenes.add(new Opening());
        //scenes.add(new LightIntro());
        scenes.add(new Stationary());
        scenes.add(new Moving());
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
        private ArrayList<Runnable> runs = new ArrayList<>();

        public void draw() {
            if (start == null) {
                start = millis();
            }

            runs.forEach(Runnable::run);
        }

        private void show(int offset, int duration, Consumer<Integer> run) {
            runs.add(() -> {
                if (start + offset <= millis() && (duration == 0 || millis() <= start + offset + duration)) {
                    run.accept(millis() - start - offset);
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

            public Builder wait(Builder previous) {
                return wait(previous.start);
            }

            public Builder chain(Builder next) {
                return new Builder(start, next.sceneSlice);
            }

            public Builder duration(int millis) {
                Scene.this.show(start, millis, sceneSlice);
                return new Builder(start + millis);
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

            b
                    .then(() -> {
                        background(0);
                        fill(255, 255, 0);
                        ellipse(xLight, height * 0.5f, 20, 20);
                    })
                    .when()

                    .chain(small)
                    .then(() -> text("This is light", xText, height * 0.2f)).when(500)

                    .chain(tiny)
                    .then(() -> text("Hello!", xLight, height * 0.45f)).when(250, 2000).wait(-2000)

                    .chain(small)
                    .then(() -> text("Light does not give a single sh*t", xText, height * 0.6f)).when(750)
                    .then(() -> text("about nothing whatsoever", xText, height * 0.66f)).when(750)
                    .then(() -> text("its speed is always the same", xText, height * 0.8f)).when(750)
                    .then(() -> text("ALWAYS!!!111one", xText, height * 0.86f)).when(750)

                    .chain(tiny)
                    .then(() -> text("Gotta go fast!", xLight, height * 0.45f)).when(250, 500)
                    .end().when();
        }
    }

    private static final float C = 75f / 1000f;

    public static final float SHIP_SIZE = 300;
    public static final float MARGIN = 10;
    private static final float WALL_OFFSET = 30;
    private static final float MIRROR_WIDTH = 50;
    private static final float PHOTON_SIZE = MIRROR_WIDTH / 2;
    private static final float DIFF_MIRROR_TO_PHOTON = (MIRROR_WIDTH - PHOTON_SIZE) / 2;

    private class Ship {
        private void drawBorder() {
            stroke(0, 255, 0);
            strokeWeight(4);
            fill(0);
            rect(MARGIN, MARGIN, SHIP_SIZE, SHIP_SIZE);
        }

        private void drawMirrors() {
            stroke(100, 100, 100);
            strokeWeight(8);
            line(MARGIN + WALL_OFFSET, MARGIN + WALL_OFFSET, MARGIN + WALL_OFFSET + MIRROR_WIDTH, MARGIN + WALL_OFFSET);
            line(MARGIN + SHIP_SIZE - WALL_OFFSET, MARGIN + SHIP_SIZE - WALL_OFFSET, MARGIN + SHIP_SIZE - WALL_OFFSET, MARGIN + SHIP_SIZE - WALL_OFFSET - MIRROR_WIDTH);
        }

        private void drawLightSources() {
            strokeWeight(0);
            stroke(255, 0, 0);
            fill(255, 0, 0);

            // upwards source
            triangle(MARGIN + WALL_OFFSET + MIRROR_WIDTH / 2,
                    MARGIN + SHIP_SIZE,

                    MARGIN + WALL_OFFSET + DIFF_MIRROR_TO_PHOTON,
                    MARGIN + SHIP_SIZE - WALL_OFFSET - DIFF_MIRROR_TO_PHOTON,

                    MARGIN + WALL_OFFSET + MIRROR_WIDTH - DIFF_MIRROR_TO_PHOTON,
                    MARGIN + SHIP_SIZE - WALL_OFFSET - DIFF_MIRROR_TO_PHOTON);

            // rightwards source
            triangle(MARGIN,
                    MARGIN + SHIP_SIZE - WALL_OFFSET - MIRROR_WIDTH / 2,

                    MARGIN + WALL_OFFSET + DIFF_MIRROR_TO_PHOTON,
                    MARGIN + SHIP_SIZE - WALL_OFFSET - MIRROR_WIDTH + DIFF_MIRROR_TO_PHOTON,

                    MARGIN + WALL_OFFSET + DIFF_MIRROR_TO_PHOTON,
                    MARGIN + SHIP_SIZE - WALL_OFFSET - DIFF_MIRROR_TO_PHOTON);
        }

        private void draw() {
            drawBorder();
            drawMirrors();
            drawLightSources();
        }
    }

    private class Stationary extends Scene {

        public Stationary() {
            Ship ship = new Ship();
            Builder prev = b
                    .then(() -> background(0))
                    .when(0)
                    .then(() -> {
                        ship.drawBorder();
                    })
                    .then(() -> {
                        fill(0, 255, 0);
                        text("This is a spaceship", width / 2, 2 * MARGIN + SHIP_SIZE + 30);
                    }).when(250)
                    .then(() -> {
                        ship.drawMirrors();
                    }).when(250)
                    .then(() -> {
                        fill(100, 100, 100);
                        text("These are mirrors", width / 2, 2 * MARGIN + SHIP_SIZE + 60);
                    }).when(250)
                    .then(() -> {
                        ship.drawLightSources();
                    }).when(250)
                    .then(() -> {
                        fill(255, 0, 0);
                        text("These are light sources", width / 2, 2 * MARGIN + SHIP_SIZE + 90);
                    }).when(250)
                    .then(() -> {
                        fill(255, 255, 0);
                        text("No let us fire a photon each", width / 2, 2 * MARGIN + SHIP_SIZE + 120);
                    }).when(250);

            float t = (SHIP_SIZE - 2 * WALL_OFFSET - PHOTON_SIZE - DIFF_MIRROR_TO_PHOTON) / C;

            // light to right
            b
                    .wait(prev)
                    .then((i) -> {
                        fill(255, 255, 0);
                        stroke(255, 255, 0);
                        ellipse(MARGIN + WALL_OFFSET + MIRROR_WIDTH / 2 + i * C,
                                MARGIN + SHIP_SIZE - WALL_OFFSET - MIRROR_WIDTH / 2,
                                PHOTON_SIZE,
                                PHOTON_SIZE);
                    })

                    // light up
                    .then((i) -> {
                        fill(255, 255, 0);
                        stroke(255, 255, 0);
                        ellipse(MARGIN + WALL_OFFSET + MIRROR_WIDTH / 2,
                                MARGIN + SHIP_SIZE - WALL_OFFSET - MIRROR_WIDTH / 2 - i * C,
                                PHOTON_SIZE,
                                PHOTON_SIZE);
                    }).duration((int) t)

                    // light left
                    .then((i) -> {
                        fill(255, 255, 0);
                        stroke(255, 255, 0);
                        ellipse(MARGIN + SHIP_SIZE - WALL_OFFSET - PHOTON_SIZE / 2 - i * C,
                                MARGIN + SHIP_SIZE - WALL_OFFSET - MIRROR_WIDTH / 2,
                                PHOTON_SIZE,
                                PHOTON_SIZE);
                    })

                    // light down
                    .then((i) -> {
                        fill(255, 255, 0);
                        stroke(255, 255, 0);
                        ellipse(MARGIN + WALL_OFFSET + MIRROR_WIDTH / 2,
                                MARGIN + WALL_OFFSET + PHOTON_SIZE / 2 + i * C,
                                PHOTON_SIZE,
                                PHOTON_SIZE);
                    }).duration((int) t)

                    // light back at start
                    .then(() -> {
                        fill(255, 255, 0);
                        stroke(255, 255, 0);
                        ellipse(MARGIN + WALL_OFFSET + MIRROR_WIDTH / 2,
                                MARGIN + SHIP_SIZE - WALL_OFFSET - MIRROR_WIDTH / 2,
                                PHOTON_SIZE,
                                PHOTON_SIZE);
                    }).duration(100)
                    .end().when();
        }
    }

    private class Moving extends Scene {
        public Moving() {
            Ship ship = new Ship();

            float v = C * 0.8f;
            float t = 3 * SHIP_SIZE / v;
            b
                    .then(() -> background(0))
                    .then((i) -> {
                        float p = i * v;

                        pushMatrix();
                        translate(p, 0);
                        ship.draw();
                        popMatrix();
                    }).duration((int) t);


            float yDistance = SHIP_SIZE - WALL_OFFSET * 2 - PHOTON_SIZE - DIFF_MIRROR_TO_PHOTON;
            float tUpDown = (float) Math.sqrt(yDistance * yDistance / (C * C - v * v));
            float xDistance = v * tUpDown;

            float bogusForth = yDistance / (C - v);
            float bogusBack = yDistance / (C + v);

            b
                    // light to right
                    .then((i) -> {
                        fill(255, 255, 0);
                        stroke(255, 255, 0);
                        ellipse(MARGIN + WALL_OFFSET + MIRROR_WIDTH / 2 + i * C,
                                MARGIN + SHIP_SIZE - WALL_OFFSET - MIRROR_WIDTH / 2,
                                PHOTON_SIZE,
                                PHOTON_SIZE);
                    })
                    .duration((int) bogusForth)
                    // light left
                    .then((i) -> {
                        fill(255, 255, 0);
                        stroke(255, 255, 0);
                        ellipse(MARGIN + SHIP_SIZE - WALL_OFFSET - MIRROR_WIDTH / 2 + DIFF_MIRROR_TO_PHOTON + v * bogusForth - i * C,
                                MARGIN + SHIP_SIZE - WALL_OFFSET - MIRROR_WIDTH / 2,
                                PHOTON_SIZE,
                                PHOTON_SIZE);
                    })
                    .duration((int) bogusBack);

            b
                    // light up
                    .then((i) -> {
                        float iLeft = tUpDown - i;
                        float weightStart = iLeft / tUpDown;
                        float weightDest = i / tUpDown;
                        fill(255, 255, 0);
                        stroke(255, 255, 0);
                        ellipse(MARGIN + WALL_OFFSET + MIRROR_WIDTH / 2 + weightStart * 0 + weightDest * xDistance,
                                MARGIN + SHIP_SIZE - WALL_OFFSET - MIRROR_WIDTH / 2 - weightStart * 0 - weightDest * yDistance,
                                PHOTON_SIZE,
                                PHOTON_SIZE);
                    })
                    .duration((int) tUpDown)
                    // light down
                    .then((i) -> {
                        float iLeft = tUpDown - i;
                        float weightStart = iLeft / tUpDown;
                        float weightDest = i / tUpDown;
                        fill(255, 255, 0);
                        stroke(255, 255, 0);
                        ellipse(MARGIN + WALL_OFFSET + MIRROR_WIDTH / 2 + xDistance + weightStart * 0 + weightDest * xDistance,
                                MARGIN + SHIP_SIZE - WALL_OFFSET - MIRROR_WIDTH / 2 - weightStart * yDistance - weightDest * 0,
                                PHOTON_SIZE,
                                PHOTON_SIZE);
                    })
                    .duration((int) tUpDown)

                    .wait((int) t)
                    .end().when();
        }
    }
}
