package me.retrodaredevil.solarthing;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import me.retrodaredevil.couchdb.CouchProperties;
import me.retrodaredevil.couchdb.CouchPropertiesBuilder;
import me.retrodaredevil.solarthing.util.IgnoreCheckSum;
import me.retrodaredevil.util.json.JsonFile;

import java.util.ArrayList;
import java.util.List;

class ProgramArgs {
	
	@Parameter()
	private List<String> parameters = new ArrayList<>();
	
	@Parameter(names = {"--help", "-h"}, help = true)
	private boolean help = false;
	
	@Parameter(names = { "--serial", "--serialport", "--usb", "--usbport" }, description = "Name of the USB serial port")
	private String portName = "/dev/ttyUSB0";
	
	@Parameter(names = { "--protocol" }, description = "The protocol. Almost always http unless you know what you're doing.")
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
	@Parameter(names = {"--throttle", "--throttle-factor", "--tf"}, description = "Every nth packet, data should be saved.")
	private int throttleFactor = 1;
	@Parameter(names = {"--only-instant", "--reliable", "--instant", "--oi"}, description = "Only saves packets that are 'instant'")
	private boolean onlyInstant = false;
	
	@Parameter(names = {"--unique"})
	private Integer uniqueIdsInOneHour = null;

	@Parameter(names = { "-i", "--ignore-checksum", "--ignore-check-sum", "--ignore-chksum" }, description = "Ignore the checksum in all packets")
	private boolean ignoreCheckSum = false;
	@Parameter(names = { "-c", "--cc", "--correct-check-sum", "--correct-checksum", "--correct-chksum",
			"--calc-check-sum", "--calc-checksum", "--calc-chksum",
			"--calculate-check-sum", "--calculate-checksum", "--calculate-chksum"}, description = "Ignore and correct the checksum in all packets")
	private boolean correctCheckSum = false;

	@Parameter(names = {"--file", "--path", "-f"}, description = "Set the path to save the json data to. Must be used with --local")
	private String filePath = "data.json";
	@Parameter(names = {"--local"}, description = "Save data to a local file on the system")
	private boolean local = false;
	
	@Parameter(names = {"--ct", "--connect-timeout", "--connection-timeout"})
	private int connectionTimeoutSeconds = 0;
	@Parameter(names = {"--st", "--socket-timeout"})
	private int socketTimeoutSeconds = 0;

	public ProgramArgs(String[] args){
		JCommander.newBuilder().addObject(this).build().parse(args);
	}
	public List<String> getParameters(){
		return parameters;
	}
	public boolean isHelp(){
		return help;
	}
	public boolean isUnitTest(){
		return unitTest;
	}
	public IgnoreCheckSum getIgnoreCheckSum() {
		if(correctCheckSum){
			return IgnoreCheckSum.IGNORE_AND_USE_CALCULATED;
		} else if(ignoreCheckSum){
			return IgnoreCheckSum.IGNORE;
		}
		return IgnoreCheckSum.DISABLED;
	}
	public String getFilePath(){
		return filePath;
	}
	public boolean isLocal(){
		return local;
	}
	public int getThrottleFactor(){
		return throttleFactor;
	}
	public boolean isOnlyInstant(){
		return onlyInstant;
	}
	public Integer getUniqueIdsInOneHour() {
		return uniqueIdsInOneHour;
	}
	
	public String getPortName(){
		return portName;
	}
	public CouchProperties createProperties(){
		return new CouchPropertiesBuilder(null, true, protocol, host, port, userName, password)
			.setConnectionTimeout(connectionTimeoutSeconds * 1000)
			.setSocketTimeout(socketTimeoutSeconds * 1000).build();
	}

	public void printInJson(){
		System.out.println(JsonFile.gson.toJson(this));
		
	}

}
