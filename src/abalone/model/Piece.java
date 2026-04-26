package abalone.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Piece implements Saveable {
    private Color color;

    public Piece(Color color) {
        this.color = color;
    }

    public Piece(JSONObject jsonObject) {
        fromJSON(jsonObject);
    }

    public Color getColor() {
        return color;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("color", color.toString());
        } catch (JSONException e) {
            System.err.println("Error converting piece to JSON: " + e.getMessage());
        }
        return json;
    }

    @Override
    public void fromJSON(JSONObject jsonObject) {
        try {
            color = Color.valueOf(jsonObject.getString("color"));
        } catch (JSONException e) {
            System.err.println("Error converting piece from JSON: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return color.name();
    }
}
