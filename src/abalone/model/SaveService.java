package abalone.model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.json.*;

public class SaveService {
  public void saveToFile(String filePath, Saveable model) throws IOException {
    try (FileWriter writer = new FileWriter(filePath)) {
      writer.write(model.toJSON().toString());
    } catch (IOException e) {
      throw new IOException("Error writing JSON file", e);
    }
  }

  public void loadFromFile(String filePath, Saveable model) throws IOException {
    try (FileInputStream in = new FileInputStream(filePath)) {
      JSONTokener tokener = new JSONTokener(in);
      JSONObject json = new JSONObject(tokener);
      model.fromJSON(json);
    } catch (JSONException e) {
      throw new IOException("Error reading JSON file", e);
    } catch (FileNotFoundException e) {
      throw new IOException("File not found", e);
    }
  }
}
