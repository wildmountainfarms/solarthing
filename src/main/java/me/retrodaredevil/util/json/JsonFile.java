package me.retrodaredevil.util.json;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class JsonFile {
	

	public static final JsonParser pa = new JsonParser();
	public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	private final File file;
	private boolean isJson = true;
	
	private JsonObject obj;
	
	/**
	 * 
	 * @param file the Json file
	 * @throws FileNotFoundException thrown if the file doesn not exist
	 */
	public JsonFile(File file) throws FileNotFoundException{
		this.file = file;
		if(!file.exists()){
			throw new FileNotFoundException("Cannot use a file that does not exist.");
		}
		try{
			load(false);
		} catch(Exception ex){
			isJson = false;
		}
	}
	// Getters and Setters
	/**
	 * 
	 * @return true if the provided file is in a readable json format and calling reload doesn't result in an error, false otherwise
	 */
	public boolean isJson(){
		return isJson && getObject() != null;
	}
	public JsonObject getObject(){
		return obj;
	}
	
	//Public Methods
	/**
	 * Loads the json file
	 * @param overwrite if this is a json file and overwrite is false, it will call reload. If it is not a json file, then overwrite must be true or an error will be thrown
	 * @throws IOException 
	 */
	public void load(boolean overwrite) throws IOException {
		if(overwrite){
			write("");
			obj = new JsonObject();
			isJson = true;
			return;
		}
		reload();
		
	}
	public void reload() { // read json here
		JsonReader reader;
		try {
			reader = new JsonReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		JsonElement el = pa.parse(reader);
		if(el.isJsonObject()){
			obj = (JsonObject) el;
		} else {
			obj = el.getAsJsonObject();
			System.err.println("Not json object for whatever reason");
		}
		
	}
	
	public void save() throws IOException{
		if(obj.isJsonNull()){
			write("");
			return;
		}
		
		String save = obj.toString();
		save = save.replaceAll("\\n", "\n");
		//System.out.println("Saving: " + save);
		write(save);
	}
	
	//Private methods
	private void write(String s) throws IOException{
		String[] split = s.split("\n");
		try(FileWriter fw = new FileWriter(file, false)){
			try(BufferedWriter bw = new BufferedWriter(fw)){
				for(String part : split){
					bw.write(part);
					bw.newLine();
				}
			}
		} 
	}
}
