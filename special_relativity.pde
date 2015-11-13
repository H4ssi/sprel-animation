
ArrayList<Scene> scenes = new ArrayList<Scene>();

PFont big; 
PFont small;
PFont tiny; 

void setup() {
  size(480, 480);

  big = loadFont("FiraSans-Regular-48.vlw");
  small = loadFont("FiraSans-Regular-20.vlw");
  tiny = loadFont("FiraSans-Regular-16.vlw");

  scenes.add(new Opening());
  scenes.add(new LightIntro());
}

void draw() {
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

abstract class Scene {
  boolean end;
  int m;

  Integer start;
  void draw() {
    if (start == null) {
      start = millis();
    }

    for (Runnable r : runs) {
      r.run();
    }
  }

  int cur = 0;
  ArrayList<Runnable> runs = new ArrayList<Runnable>();

  void show(final int delay, final int length, final Runnable run) {
    cur += delay;
    final int off = cur;
    runs.add(new Runnable() {
      public void run() {
        if (start + off <= millis() && (length == 0 || millis() <= start + off + length)) {
          run.run();
        }
      }
    }
    );
  }
}

class Opening extends Scene {  

  Opening() {
    background(0);

    show(0, 0, new Runnable() {
      public void run() {
        text("Special Relativity", width / 2, height / 2);
      }
    }
    );

    show(1000, 0, new Runnable() {
      public void run() {
        text("is hard", width / 2, height / 1.5);
      }
    }
    );

    show(1000, 0, new Runnable() {
      public void run() {
        end = true;
      }
    }
    );
  }
}

class LightIntro extends Scene {
  LightIntro() {

    final int xLight = 100;
    final int xText = width / 2;

    show(0, 0, new Runnable() {
      public void run() {
        background(0);
        fill(255, 255, 0);
        ellipse(xLight, height * 0.5, 20, 20);
      }
    }
    );

    show(500, 0, new Runnable() {
      public void run() {
        text("This is light", xText, height * 0.2);
      }
    }
    );

    show(250, 2000, new Runnable() {
      public void run() {
        textFont(tiny);

        text("Hello!", xLight, height * 0.45);
      }
    }
    );

    show(750, 0, new Runnable() {
      public void run() {
        textFont(small);


        text("Light does not give a single sh*t", xText, height * 0.6);
      }
    }
    );
    show(750, 0, new Runnable() {
      public void run() {
        text("about nothing whatsoever", xText, height * 0.66);
      }
    }
    );
    show(750, 0, new Runnable() {
      public void run() {
        text("its speed is always the same", xText, height * 0.8);
      }
    }
    );
    show(750, 0, new Runnable() {
      public void run() {
        text("ALWAYS!!!111one", xText, height * 0.86);
      }
    }
    );
  }
}