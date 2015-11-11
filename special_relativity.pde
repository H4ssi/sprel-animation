
ArrayList<Scene> scenes = new ArrayList<Scene>();

void setup() {
  size(480,480);
  textFont(loadFont("FiraSans-Regular-48.vlw"));
  textAlign(CENTER);
  
  scenes.add(new Opening());
}

void draw() {
  long m = millis();
  for(Scene scene : scenes) {
    if(m < scene.end) {
      scene.draw();
    }
  }
}

abstract class Scene {
  long end;
  
  Scene(long end) {
    this.end = end;
  }
  
  abstract void draw();
}

class Opening extends Scene {  
  Opening() {
     super(2000);
  }
  
  void draw() {
    background(0);
  
    text("Special Relativity", width / 2, height / 2); 
  
    if(millis() > 1000) {
      text("is hard", width / 2, height / 1.5); 
    }
  }
}