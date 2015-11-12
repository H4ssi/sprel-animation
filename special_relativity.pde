
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

  abstract void draw(int m);

  Integer start;
  void draw() {
    if (start == null) {
      start = millis();
    }

    draw(millis() - start);
  }
}

class Opening extends Scene {  

  void draw(int m) {
    background(0);

    text("Special Relativity", width / 2, height / 2); 

    if (m > 1000) {
      text("is hard", width / 2, height / 1.5);
    }

    if (m > 2000) {
      end = true;
    }
  }
}

class LightIntro extends Scene {
  void draw(int m) {
    background(0);

    int xLight = 100;
    int xText = width / 2;

    fill(255, 255, 0);
    ellipse(xLight, height * 0.5, 20, 20);


    if (m > 500) {
      text("This is light", xText, height * 0.2);
    }

    textFont(tiny);

    if (750 < m && m < 3000) {
      text("Hello!", xLight, height * 0.45);
    }

    textFont(small);

    if (m > 1000) {
      text("Light does not give a single sh*t", xText, height * 0.6);
    }
    if (m > 2000) {
      text("about nothing whatsoever", xText, height * 0.66);
    }
    if (m > 3500) {
      text("its speed is always the same", xText, height * 0.8);
    }
    if (m > 4500) {
      text("ALWAYS!!!111one", xText, height * 0.86);
    }
  }
}