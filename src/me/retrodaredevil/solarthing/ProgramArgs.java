package me.retrodaredevil.solarthing;

import org.lightcouch.CouchDbProperties;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import me.retrodaredevil.solarthing.util.json.JsonFile;

public class ProgramArgs {
	@Parameter(names = {"--help", "-h"}, help = true)
	private boolean help = false;
	
	@Parameter(names = { "--serial", "--serialport", "--usb", "--usbport" }, description = "Name of the USB serial port")
	private String portName = "/dev/ttyUSB0";
	
	@Parameter(names = {"--name", "--database"}, description = "The name of the database")
	private String databaseName = "solarthing";
	
	@Parameter(names = {}, description = "The protocol. Almost always http unless you know what you're doing.")
	private String protocol = "http";
	@Parameter(names = {"--host"})
	private String host = "127.0.0.1";
	@Parameter(names = {"-p", "--port"}, description = "The port number.")
	private int port = 5984;
	
	
	@Parameter(names = {"--user", "-u"}, description = "The username for the database")
	private String userName = "admin";
	@Parameter(names = {"--passwd", "--password"}, description = "The password for the database", password = true)
	private String password = "relax";
	@Parameter(names = {"--unit-test", "--unit"}, description = "If present, will use terminal input")
	private boolean unitTest = false;
	
	
	private CouchDbProperties databaseProperties;
	
	public ProgramArgs(String[] args){
		JCommander.newBuilder().addObject(this).build().parse(args);
		if(help){
			
			
			System.out.println("Help was called. Check ProgramArgs.java. Self explainatory. Sorry I'm lazy.\n" + 
			"Also note, as a VM argument, you should have -Djava.library.path=/usr/lib/jni");
			System.exit(1);
		}
		
		databaseProperties = new CouchDbProperties(databaseName, true, protocol, host, port, userName, password);
		
	}
	public boolean isUnitTest(){
		return unitTest;
	}
	
	public String getPortName(){
		return portName;
	}
	public CouchDbProperties getProperties(){
		return databaseProperties;
	}
	/**
	 * Entry point to test ProgramArgs class
	 * @throws Exception 
	 */
	public static void main(String[] args){
		ProgramArgs arguments = new ProgramArgs(args);
		arguments.printInJson();
	}
	public void printInJson(){
		System.out.println(JsonFile.gson.toJson(this));
		
	}
}
