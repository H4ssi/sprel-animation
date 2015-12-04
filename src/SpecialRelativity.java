import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

        private void show(int offset, Integer duration, Consumer<Integer> run) {
            runs.add(() -> {
                if (start + offset <= millis() && (duration == null || millis() <= start + offset + duration)) {
                    run.accept(millis() - start - offset);
                }
            });
        }

        public boolean isEnd() {
            return end;
        }

        protected class Builder {
            private final float start;
            private final Float duration;
            private final Consumer<Integer> sceneSlice;

            private Builder() {
                this(0);
            }

            private Builder(float start) {
                this(start, (i) -> {
                });
            }

            private Builder(float start, Consumer<Integer> sceneSlice) {
                this(start, null, sceneSlice);
            }

            private Builder(float start, Float duration, Consumer<Integer> sceneSlice) {
                this.start = start;
                this.duration = duration;
                this.sceneSlice = sceneSlice;
            }

            public Builder show(Consumer<Integer> sceneSlice) {
                return new Builder(start, (i) -> {
                    Builder.this.sceneSlice.accept(i);
                    sceneSlice.accept(i);
                });
            }

            public Builder show(Runnable sceneSlice) {
                return show((i) -> sceneSlice.run());
            }

            public Builder end() {
                return show(() -> end = true);
            }

            public Builder delay(float millis) {
                return new Builder(start + millis, sceneSlice);
            }

            public Builder delay(Builder previous) {
                return delay(previous.start);
            }

            public Builder chain(Builder next) {
                return new Builder(start, next.sceneSlice);
            }

            public Builder duration(Float millis) {
                return new Builder(start, millis, sceneSlice);
            }

            public Builder then() {
                if (duration == null) {
                    throw new IllegalArgumentException("cannot show another slice after an non-ending slice");
                }
                Scene.this.show(Math.round(start), Math.round(duration), sceneSlice);
                return new Builder(start + duration);
            }

            public Builder parallel() {
                Scene.this.show(Math.round(start), duration == null ? null : Math.round(duration), sceneSlice);
                return new Builder(start);
            }

            public Builder when(float delay, Float length) {
                return delay(delay).duration(length).parallel();
            }

            public Builder when(float delay) {
                return when(delay, duration);
            }

            public Builder when() {
                return when(0);
            }
        }

        protected final Builder b = new Builder();
    }

    private class Opening extends Scene {
        public Opening() {
            b
                    .show(() -> {
                        background(0);
                        textFont(bigFont);
                        textAlign(CENTER);
                    })
                    .show(() -> text("Special Relativity", width / 2f, height / 2f)).when()
                    .show(() -> text("is hard", width / 2f, height / 1.5f)).when(1000)
                    .end().when(1000);
        }
    }

    private class LightIntro extends Scene {
        public LightIntro() {
            int xLight = 100;
            int xText = width / 2;

            Builder small = b.show(() -> textFont(smallFont));
            Builder tiny = b.show(() -> textFont(tinyFont));

            b
                    .show(() -> {
                        background(0);
                        fill(255, 255, 0);
                        ellipse(xLight, height * 0.5f, 20, 20);
                    })
                    .when()

                    .chain(small)
                    .show(() -> text("This is light", xText, height * 0.2f)).when(500)

                    .chain(tiny)
                    .show(() -> text("Hello!", xLight, height * 0.45f)).when(250, 2000f)

                    .chain(small)
                    .show(() -> text("Light does not give a single sh*t", xText, height * 0.6f)).when(500)
                    .show(() -> text("about nothing whatsoever", xText, height * 0.66f)).when(750)
                    .show(() -> text("its speed is always the same", xText, height * 0.8f)).when(750)
                    .show(() -> text("ALWAYS!!!111one", xText, height * 0.86f)).when(750)

                    .chain(tiny)
                    .show(() -> text("Gotta go fast!", xLight, height * 0.45f)).when(250, 500f)
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

    private void withMargin(Runnable r) {
        pushMatrix();
        translate(MARGIN, MARGIN);
        r.run();
        popMatrix();
    }

    private class Ship {
        private void drawBorder() {
            stroke(0, 255, 0);
            strokeWeight(4);
            fill(0);
            rect(0, 0, SHIP_SIZE, SHIP_SIZE);
        }

        private void drawMirrors() {
            stroke(100, 100, 100);
            strokeWeight(8);
            line(WALL_OFFSET, WALL_OFFSET, WALL_OFFSET + MIRROR_WIDTH, WALL_OFFSET);
            line(SHIP_SIZE - WALL_OFFSET, SHIP_SIZE - WALL_OFFSET, SHIP_SIZE - WALL_OFFSET, SHIP_SIZE - WALL_OFFSET - MIRROR_WIDTH);
        }

        private void drawLightSources() {
            strokeWeight(0);
            stroke(255, 0, 0);
            fill(255, 0, 0);

            // upwards source
            triangle(WALL_OFFSET + MIRROR_WIDTH / 2,
                    SHIP_SIZE,

                    WALL_OFFSET + DIFF_MIRROR_TO_PHOTON,
                    SHIP_SIZE - WALL_OFFSET - DIFF_MIRROR_TO_PHOTON,

                    WALL_OFFSET + MIRROR_WIDTH - DIFF_MIRROR_TO_PHOTON,
                    SHIP_SIZE - WALL_OFFSET - DIFF_MIRROR_TO_PHOTON);

            // rightwards source
            triangle(0,
                    SHIP_SIZE - WALL_OFFSET - MIRROR_WIDTH / 2,

                    WALL_OFFSET + DIFF_MIRROR_TO_PHOTON,
                    SHIP_SIZE - WALL_OFFSET - MIRROR_WIDTH + DIFF_MIRROR_TO_PHOTON,

                    WALL_OFFSET + DIFF_MIRROR_TO_PHOTON,
                    SHIP_SIZE - WALL_OFFSET - DIFF_MIRROR_TO_PHOTON);
        }

        private void draw() {
            drawBorder();
            drawMirrors();
            drawLightSources();
        }
    }

    private interface Movement {
        PVector at(float time);

        Float getDuration();

        static Movement chain(Movement... movements) {
            return new Movement() {
                @Override
                public PVector at(float time) {
                    for (Movement m : movements) {
                        if (m.getDuration() == null || time <= m.getDuration()) {
                            return m.at(time);
                        }
                        time -= m.getDuration();
                    }
                    return null;
                }

                @Override
                public Float getDuration() {
                    float d = 0;
                    for (Movement m : movements) {
                        if (m.getDuration() == null) {
                            return null;
                        }
                        d += m.getDuration();
                    }
                    return d;
                }
            };
        }
    }

    private static class LinearMovement implements Movement {
        private final PVector from;
        private final PVector to;
        private final float duration;


        private LinearMovement(PVector from, PVector to, float duration) {
            this.from = from;
            this.to = to;
            this.duration = duration;
        }

        @Override
        public PVector at(float time) {
            return PVector.lerp(from, to, Math.min(1f, Math.max(0f, time / duration)));
        }

        @Override
        public Float getDuration() {
            return duration;
        }

        public static LinearMovement withTarget(PVector from, PVector to, float duration) {
            return new LinearMovement(from, to, duration);
        }

        public static LinearMovement withDirection(PVector from, PVector direction, float duration) {
            return withTarget(from, PVector.add(from, PVector.mult(direction, duration)), duration);
        }

        public static LinearMovement stationary(PVector pos) {
            return withTarget(pos, pos, Float.MAX_VALUE);
        }
    }

    public class Tracer {
        private final Movement movement;

        public final static float TRACE_INTERVAL = 432;
        public final static float TRACE_DECAY = TRACE_INTERVAL * 3.333f;

        public Tracer(Movement movement) {
            this.movement = movement;
        }

        public Map<PVector, Float> traces(float time) {
            float last = time - time % TRACE_INTERVAL;

            Map<PVector, Float> m = new HashMap<>();

            for (float f = last; f >= 0f && time - f <= TRACE_DECAY; f -= TRACE_INTERVAL) {
                PVector pos = movement.at(f);
                if (pos != null) {
                    m.merge(pos, 1 - (time - f) / TRACE_DECAY, Math::max);
                }
            }

            return m;
        }
    }

    Consumer<Integer> drawPhoton(Movement movement) {
        return (i) -> {
            fill(255, 255, 0);
            stroke(255, 255, 0);
            PVector position = movement.at(i);
            if (position == null) {
                return;
            }
            ellipse(MARGIN + WALL_OFFSET + MIRROR_WIDTH / 2 + position.x,
                    MARGIN + SHIP_SIZE - WALL_OFFSET - MIRROR_WIDTH / 2 - position.y,
                    PHOTON_SIZE,
                    PHOTON_SIZE);
        };
    }

    Consumer<Integer> drawPhotonTraces(Movement movement) {
        return (i) -> {
            fill(255, 255, 255, 0f);
            stroke(255, 255, 0);

            Tracer tracer = new Tracer(movement);

            tracer.traces(i).forEach((pos, intensity) -> {
                ellipse(MARGIN + WALL_OFFSET + MIRROR_WIDTH / 2 + pos.x,
                        MARGIN + SHIP_SIZE - WALL_OFFSET - MIRROR_WIDTH / 2 - pos.y,
                        PHOTON_SIZE * 0.5f * intensity,
                        PHOTON_SIZE * 0.5f * intensity);
            });
        };
    }

    private class Stationary extends Scene {
        public Stationary() {
            Ship ship = new Ship();
            Builder prev = b
                    .show(() -> background(0))
                    .when()
                    .show(() -> {
                        withMargin(ship::drawBorder);
                    })
                    .show(() -> {
                        fill(0, 255, 0);
                        text("This is a spaceship", width / 2, 2 * MARGIN + SHIP_SIZE + 30);
                    }).when(250)
                    .show(() -> {
                        withMargin(ship::drawMirrors);
                    }).when(250)
                    .show(() -> {
                        fill(100, 100, 100);
                        text("These are mirrors", width / 2, 2 * MARGIN + SHIP_SIZE + 60);
                    }).when(250)
                    .show(() -> {
                        withMargin(ship::drawLightSources);
                    }).when(250)
                    .show(() -> {
                        fill(255, 0, 0);
                        text("These are light sources", width / 2, 2 * MARGIN + SHIP_SIZE + 90);
                    }).when(250)
                    .show(() -> {
                        fill(255, 255, 0);
                        text("No let us fire a photon each", width / 2, 2 * MARGIN + SHIP_SIZE + 120);
                    }).when(250);

            float t = (SHIP_SIZE - 2 * WALL_OFFSET - PHOTON_SIZE - DIFF_MIRROR_TO_PHOTON) / C;

            Movement rightLeft = Movement.chain(
                    LinearMovement.withDirection(new PVector(0f, 0f), new PVector(C, 0f), t),
                    LinearMovement.withDirection(new PVector(C * t, 0f), new PVector(-C, 0f), t));
            Movement upDown = Movement.chain(
                    LinearMovement.withDirection(new PVector(0f, 0f), new PVector(0f, C), t),
                    LinearMovement.withDirection(new PVector(0f, C * t), new PVector(0f, -C), t));

            b
                    .delay(prev)
                    .show(drawPhoton(rightLeft)).duration(2 * t)
                    .parallel().show(drawPhotonTraces(rightLeft)).duration(2 * t + Tracer.TRACE_DECAY)
                    .parallel().show(drawPhoton(upDown)).duration(2 * t)
                    .parallel().show(drawPhotonTraces(upDown)).duration(2 * t + Tracer.TRACE_DECAY)

                    .then().when(100).end().when();
        }
    }

    private class Moving extends Scene {
        public Moving() {
            Ship ship = new Ship();

            float v = C * 0.8f;
            float t = 3 * SHIP_SIZE / v;
            b
                    .show(() -> background(0)).when()
                    .show((i) -> {
                        withMargin(() -> {
                            pushMatrix();
                            translate(i * v, 0);
                            ship.draw();
                            popMatrix();
                        });
                    }).duration(t).then();


            float yDistance = SHIP_SIZE - WALL_OFFSET * 2 - PHOTON_SIZE - DIFF_MIRROR_TO_PHOTON;
            float tUpDown = (float) Math.sqrt(yDistance * yDistance / (C * C - v * v));
            float xDistance = v * tUpDown;

            float bogusForth = yDistance / (C - v);
            float bogusBack = yDistance / (C + v);

            Movement rightLeft = Movement.chain(
                    LinearMovement.withDirection(new PVector(0f, 0f), new PVector(C, 0f), bogusForth),
                    LinearMovement.withDirection(new PVector(C * bogusForth, 0f), new PVector(-C, 0f), bogusBack));
            Movement upDown = Movement.chain(
                    LinearMovement.withTarget(new PVector(0f, 0f), new PVector(xDistance, yDistance), tUpDown),
                    LinearMovement.withTarget(new PVector(xDistance, yDistance), new PVector(2 * xDistance, 0), tUpDown));

            b
                    .show(drawPhoton(rightLeft)).duration(bogusForth + bogusBack)
                    .parallel().show(drawPhotonTraces(rightLeft)).duration(bogusForth + bogusBack + Tracer.TRACE_DECAY)
                    .parallel().show(drawPhoton(upDown)).duration(tUpDown + tUpDown)
                    .parallel().show(drawPhotonTraces(upDown)).duration(tUpDown + tUpDown + Tracer.TRACE_DECAY)
                    .then().when(t).end().when();
        }
    }
}
