package abalone.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class Player implements Serializable, Saveable {
  private String name;
  private Color color;

  public Player(String name, Color color) {
    this.name = name;
    this.color = color;
  }

  public String getName() {
    return name;
  }

  public Color getColor() {
    return color;
  }

  @Override
  public JSONObject toJSON() {
    JSONObject json = new JSONObject();
    try {
      json.put("name", name);
      json.put("color", color.toString());
    } catch (JSONException e) {
      System.err.println("Error converting player to JSON: " + e.getMessage());
    }
    return json;
  }

  @Override
  public void fromJSON(JSONObject jsonObject) {
    try {
      name = jsonObject.getString("name");
      color = Color.valueOf(jsonObject.getString("color"));
    } catch (JSONException e) {
      System.err.println("Error converting player from JSON: " + e.getMessage());
    }
  }
}
