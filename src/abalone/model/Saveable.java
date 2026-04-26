package abalone.model;

import org.json.JSONObject;

public interface Saveable {
  public JSONObject toJSON();

  public void fromJSON(JSONObject jsonObject);
}
