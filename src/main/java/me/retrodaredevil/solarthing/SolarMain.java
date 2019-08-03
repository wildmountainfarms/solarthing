package me.retrodaredevil.solarthing;

import com.ghgande.j2mod.modbus.util.SerialParameters;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.retrodaredevil.modbus.J2ModModbus;
import me.retrodaredevil.modbus.ModbusRuntimeException;
import me.retrodaredevil.solarthing.couchdb.CouchDbPacketRetriever;
import me.retrodaredevil.solarthing.couchdb.CouchDbPacketSaver;
import me.retrodaredevil.solarthing.io.IOBundle;
import me.retrodaredevil.solarthing.io.JSerialIOBundle;
import me.retrodaredevil.solarthing.io.SerialPortException;
import me.retrodaredevil.solarthing.outhouse.OuthousePacketCreator;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.HourIntervalPacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollection;
import me.retrodaredevil.solarthing.packets.collection.PacketCollectionIdGenerator;
import me.retrodaredevil.solarthing.packets.collection.PacketCollections;
import me.retrodaredevil.solarthing.packets.creation.PacketProvider;
import me.retrodaredevil.solarthing.packets.creation.TextPacketCreator;
import me.retrodaredevil.solarthing.packets.handling.*;
import me.retrodaredevil.solarthing.packets.handling.implementations.FileWritePacketHandler;
import me.retrodaredevil.solarthing.packets.handling.implementations.GsonStringPacketHandler;
import me.retrodaredevil.solarthing.packets.instance.InstanceFragmentIndicatorPacket;
import me.retrodaredevil.solarthing.packets.instance.InstanceFragmentIndicatorPackets;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePackets;
import me.retrodaredevil.solarthing.packets.security.crypto.DirectoryKeyMap;
import me.retrodaredevil.solarthing.solar.outback.MatePacketCreator49;
import me.retrodaredevil.solarthing.commands.CommandProvider;
import me.retrodaredevil.solarthing.commands.CommandProviderMultiplexer;
import me.retrodaredevil.solarthing.solar.outback.command.MateCommand;
import me.retrodaredevil.solarthing.commands.sequence.CommandSequence;
import me.retrodaredevil.solarthing.solar.renogy.rover.*;
import me.retrodaredevil.util.json.JsonFile;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import static java.util.Objects.requireNonNull;

public class SolarMain {
	/*
	TODO This solar command part of this file is not general at all. The command stuff was made for my specific use case and because of that, it would
	be hard for someone else to find use of this program without customizing it to their own needs.
	
	This is one thing we have to think about because while I want it to be easy for my own needs, I also want it to
	be customizable for others as well
	 */
	private int connectMate(ProgramArgs args, PacketCollectionIdGenerator idGenerator) {
		final InputStream in;
		final OutputStream output;
		if(args.isUnitTest()){
			in = System.in;
			output = System.out;
		} else {
			final IOBundle port;
			try {
				port = JSerialIOBundle.createPort(args.getPortName());
			} catch (SerialPortException e) {
				e.printStackTrace();
				return 1;
			}
			in = port.getInputStream();
			output = port.getOutputStream();
		}
		LatestPacketHandler latestPacketHandler = new LatestPacketHandler(true);
		List<CommandProvider<MateCommand>> commandProviders = new ArrayList<>();
		{
			InputStream fileInputStream = null;
			try {
				fileInputStream = new FileInputStream(new File("command_input.txt"));
			} catch (FileNotFoundException e) {
				System.out.println("no command input file!");
			}
			if (fileInputStream != null) {
				commandProviders.add(InputStreamCommandProvider.createFrom(fileInputStream, "command_input.txt", EnumSet.allOf(MateCommand.class)));
			}
		}
		
		final PacketHandler commandRequesterHandler;
		final PacketHandler commandFeedbackHandler;
		if(args.isLocal()){
			commandRequesterHandler = PacketHandler.Defaults.HANDLE_NOTHING;
			commandFeedbackHandler = PacketHandler.Defaults.HANDLE_NOTHING;
		} else {
			final CommandSequenceDataReceiver<MateCommand> commandSequenceDataReceiver;
			{
				CommandSequence<MateCommand> generatorShutOff = CommandSequences.createAuxGeneratorShutOff(latestPacketHandler::getLatestPacketCollection);
				Map<String, CommandSequence<MateCommand>> map = new HashMap<>();
				map.put("GEN OFF", generatorShutOff);
				commandSequenceDataReceiver = new CommandSequenceDataReceiver<>(map);
			}
			commandProviders.add(commandSequenceDataReceiver.getCommandProvider());
			
			commandRequesterHandler = new ThrottleFactorPacketHandler(new PrintPacketHandleExceptionWrapper(
				new CouchDbPacketRetriever(
					args.createProperties(),
					"commands",
					new SecurityPacketReceiver(new DirectoryKeyMap(new File("authorized")), commandSequenceDataReceiver, new DirectoryKeyMap(new File("unauthorized"))),
					true
				),
				System.err
			), 4, true);
			commandFeedbackHandler = new CouchDbPacketSaver(args.createProperties(), "command_feedback");
		}
		
		Collection<MateCommand> allowedCommands = EnumSet.of(MateCommand.AUX_OFF, MateCommand.AUX_ON, MateCommand.USE, MateCommand.DROP);
		OnDataReceive onDataReceive = new MateCommandSender(
			new CommandProviderMultiplexer<>(commandProviders),
			output,
			allowedCommands,
			new OnMateCommandSent(commandFeedbackHandler)
		);
		
		List<PacketHandler> packetHandlers = new ArrayList<>();
		{
			String latestSave = args.getLatestPacketJsonSaveLocation();
			if(latestSave != null){
				packetHandlers.add(new FileWritePacketHandler(new File(latestSave), new GsonStringPacketHandler(), false));
			}
		}
		
		packetHandlers.addAll(Arrays.asList(
			latestPacketHandler,
			new ThrottleFactorPacketHandler(
				new PrintPacketHandleExceptionWrapper(getPacketSaver(args, "solarthing"), System.err),
				args.getThrottleFactor(),
				args.isOnlyInstant(),
				commandRequesterHandler
			)
		));
		
		initReader(
			in,
			new MatePacketCreator49(args.getIgnoreCheckSum()),
			new PacketHandlerMultiplexer(packetHandlers),
			idGenerator,
			250,
			onDataReceive,
			getAdditionalPacketProvider(args)
		);
		return 0;
	}
	private int connectRover(ProgramArgs args, PacketCollectionIdGenerator idGenerator){
		List<PacketHandler> packetHandlers = new ArrayList<>();
		{
			String latestSave = args.getLatestPacketJsonSaveLocation();
			if(latestSave != null){
				packetHandlers.add(new FileWritePacketHandler(new File(latestSave), new GsonStringPacketHandler(), false));
			}
		}
		packetHandlers.add(new ThrottleFactorPacketHandler(getPacketSaver(args, "solarthing"), args.getThrottleFactor(), args.isOnlyInstant()));
		PacketHandler packetHandler = new PacketHandlerMultiplexer(packetHandlers);
		PacketProvider packetProvider = getAdditionalPacketProvider(args);
		
		SerialParameters parameters = new SerialParameters(args.getPortName(), 9600, 0, 0, 8, 1, 0, false);
		parameters.setEncoding("rtu");
		try(J2ModModbus modbus = new J2ModModbus(parameters)) {
			RoverReadTable read = new RoverModbusRead(modbus);
//			RoverWriteTable write = new RoverModbusWrite(modbus);
			try {
				while (!Thread.currentThread().isInterrupted()) {
					final long startTime = System.currentTimeMillis();
					final RoverStatusPacket packet;
					try {
						packet = RoverStatusPackets.createFromReadTable(read);
					} catch(ModbusRuntimeException e){
						e.printStackTrace();
						System.err.println("Modbus exception above!");
						Thread.sleep(1000);
						continue;
					}
					System.out.println(JsonFile.gson.toJson(packet));
					System.out.println(packet.getSpecialPowerControlE021().getFormattedInfo().replaceAll("\n", "\n\t"));
					System.out.println(packet.getSpecialPowerControlE02D().getFormattedInfo().replaceAll("\n", "\n\t"));
					System.out.println();
					List<Packet> packets = new ArrayList<>();
					packets.add(packet);
					packets.addAll(packetProvider.createPackets());
					PacketCollection packetCollection = PacketCollections.createFromPackets(packets, idGenerator);
					final long readDuration = System.currentTimeMillis() - startTime;
					System.out.println("took " + readDuration + "ms to read from Rover");
					final long saveStartTime = System.currentTimeMillis();
					try {
						packetHandler.handle(packetCollection, true);
					} catch (PacketHandleException e) {
						e.printUnableToHandle(System.err, "Couldn't save a renogy rover packet!");
					}
					final long saveDuration = System.currentTimeMillis() - saveStartTime;
					System.out.println("took " + saveDuration + "ms to handle packets");
					Thread.sleep(Math.max(200, 5000 - readDuration)); // allow 5 seconds to read from rover // assume saveDuration is very small
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		return 0;
	}
	private int connectRoverSetup(ProgramArgs args) throws IOException{
		if(args.isUnitTest()){
			File file = new File(args.getDummyFile());
			final byte[] bytes = Files.readAllBytes(file.toPath());
			String json = new String(bytes, StandardCharsets.UTF_8);
			JsonObject jsonPacket = new GsonBuilder().create().fromJson(json, JsonObject.class);
			RoverStatusPacket roverStatusPacket = RoverStatusPackets.createFromJson(jsonPacket);
			DummyRoverReadWrite readWrite = new DummyRoverReadWrite(roverStatusPacket, (fieldName, previousValue, newValue) -> {
				System.out.println(fieldName + " changed from " + previousValue + " to " + newValue);
			});
			startRoverSetup(readWrite, readWrite);
		} else {
			SerialParameters parameters = new SerialParameters(args.getPortName(), 9600, 0, 0, 8, 1, 0, false);
			parameters.setEncoding("rtu");
			try (J2ModModbus modbus = new J2ModModbus(parameters)) {
				RoverReadTable read = new RoverModbusRead(modbus);
				RoverWriteTable write = new RoverModbusWrite(modbus);
				startRoverSetup(read, write);
			}
		}
		return 0;
	}
	private void startRoverSetup(RoverReadTable read, RoverWriteTable write){
		Scanner scanner = new Scanner(System.in);
		loop: while (scanner.hasNextLine()) {
			String command = scanner.nextLine();
			String[] split = command.split(" ");
			switch(split.length){
				case 0:
					continue loop;
				case 1:
					// display data
					String request = split[0].toLowerCase();
					switch(request){
						case "batteryvoltage":
							System.out.println(read.getBatteryVoltage());
							break;
						case "maxvoltage":
							System.out.println(read.getMaxVoltage().getModeName());
							break;
						case "producttype":
							System.out.println(read.getProductType().getModeName());
							break;
						case "controllerdeviceaddress": case "deviceaddress": case "address":
							System.out.println(read.getControllerDeviceAddress());
							break;
						default:
							System.err.println(request + " is not supported!");
							break;
					}
					break;
				case 2:
					// set most data
					String toSet = split[1].toLowerCase();
					switch(split[0].toLowerCase()) {
						case "controllerdeviceaddress": case "deviceaddress": case "address":
							write.setControllerDeviceAddress(Integer.parseInt(toSet));
							break;
						case "streetlight": // on/off
							final boolean streetOn;
							if(toSet.equals("on")) {
								streetOn = true;
							} else if(toSet.equals("off")){
								streetOn = false;
							} else {
								throw new IllegalArgumentException(toSet + " not supported for streetlight on/off")
							}
							write.setStreetLightStatus(streetOn ? StreetLight.ON : StreetLight.OFF);
							break;
						case "brightness": // 0-100
							int brightness = Integer.parseInt(toSet);
							write.setStreetLightBrightnessPercent(brightness);
							break;
						case "systemvoltage":
							
							break;
						case "batterytype":
							
							break;
						case "overvoltagethresholdraw":
							
							break;
						case "chargingvoltagelimitraw":
							
							break;
						case "equalizingchargingvoltageraw":
							
							break;
						case "boostchargingvoltageraw":
							
							break;
						case "floatingchargingvoltageraw":
							
							break;
						case "boostchargingrecoveryvoltageraw":
							
							break;
						case "overdischargerecoveryvoltageraw":
							
							break;
						case "undervoltagewarninglevelraw":
							
							break;
						case "discharginglimitvoltageraw":
							
							break;
							// end of charge SOC end of discharge SOC
						case "overdischargetimedelayseconds":
							
							break;
						case "equalizingchargingtimeminutes":
							
							break;
						case "boostchargingtimeminutes":
							
							break;
						case "equalizingchargingintervaldays":
							
							break;
						case "temperaturecompensationfactor":
							
							break;
						// operating settings
						case "loadworkingmode":
							
							break;
						case "lightcontroldelayminutes":
							
							break;
						case "lightcontrolvoltage":
							
							break;
						case "ledloadcurrentsettingmilliamps":
							
							break;
						case "specialpowercontrole021raw":
							
							break;
						// sensing values
						case "sensingtimedelayseconds":
							
							break;
						case "ledloadcurrentmilliamps":
							
							break;
						case "specialpowercontrole02draw":
							
							break;
						default:
							System.err.println("Not supported!");
							break;
					}
					break;
				case 3:
					// set some data
					
					break;
				default:
					System.out.println("Only a maximum of 3 arguments are allowed!");
					continue loop;
			}
		}
	}
	private int connectOuthouse(ProgramArgs args, PacketCollectionIdGenerator idGenerator) {
		List<PacketHandler> packetHandlers = new ArrayList<>();
		{
			String latestSave = args.getLatestPacketJsonSaveLocation();
			if(latestSave != null){
				packetHandlers.add(new FileWritePacketHandler(new File(latestSave), new GsonStringPacketHandler(), false));
			}
		}
		
		packetHandlers.add(new ThrottleFactorPacketHandler(getPacketSaver(args, "outhouse"), args.getThrottleFactor(), args.isOnlyInstant()));
		initReader(
			System.in,
			new OuthousePacketCreator(),
			new PacketHandlerMultiplexer(packetHandlers),
			idGenerator,
			10,
			OnDataReceive.Defaults.NOTHING,
			getAdditionalPacketProvider(args)
		);
		return 0;
	}
	private PacketHandler getPacketSaver(ProgramArgs args, String databaseName){
		if(args.isLocal()){
			try {
				return new JsonFilePacketSaver(args.getFilePath());
			} catch (IOException e) {
				throw new RuntimeException("Incorrect file path", e);
			}
		}
		return new CouchDbPacketSaver(args.createProperties(), databaseName);
	}
	
	private void initReader(InputStream in, TextPacketCreator packetCreator, PacketHandler packetHandler, PacketCollectionIdGenerator idGenerator, long samePacketTime, OnDataReceive onDataReceive, PacketProvider additionalPacketProvider) {
		Runnable run = new SolarReader(in, packetCreator, packetHandler, idGenerator, samePacketTime, onDataReceive, additionalPacketProvider);
		try {
			while (!Thread.currentThread().isInterrupted()) {
				run.run();
				Thread.sleep(5);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	private PacketProvider getAdditionalPacketProvider(ProgramArgs args){
		final String source = args.getSourceId();
		final Integer fragment = args.getFragmentId();
		requireNonNull(source);
		return () -> {
			List<Packet> r = new ArrayList<>();
			r.add(InstanceSourcePackets.create(source));
			if(fragment != null){
				r.add(InstanceFragmentIndicatorPackets.create(fragment));
			}
			return r;
		};
	}

	public static void main(String[] args) {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
		
		ProgramArgs pArgs = new ProgramArgs(args);
		Program program = getProgram(pArgs.getParameters());
		if(pArgs.isHelp() || program == null){
			System.out.println("<command> {mate|rover|outhouse}");
			System.out.println("Help was called. Check ProgramArgs.java. Self explainatory. Sorry I'm lazy.\n" +
					"Also note, as a VM argument, you should have -Djava.library.path=/usr/lib/jni");
			System.exit(1);
			throw new AssertionError();
		}
		final PacketCollectionIdGenerator idGenerator;
		final Integer uniqueIdsInOneHour = pArgs.getUniqueIdsInOneHour();
		if(uniqueIdsInOneHour == null){
			idGenerator = PacketCollectionIdGenerator.Defaults.UNIQUE_GENERATOR;
		} else {
			if(uniqueIdsInOneHour <= 0){
				System.err.println("--unique must be > 0 or not specified!");
				System.exit(1);
				throw new AssertionError();
			}
			idGenerator = new HourIntervalPacketCollectionIdGenerator(uniqueIdsInOneHour, new Random().nextInt());
		}
		try {
			int status = 1;
			if(program == Program.MATE) {
				status = new SolarMain().connectMate(pArgs, idGenerator);
			} else if(program == Program.ROVER){
				status = new SolarMain().connectRover(pArgs, idGenerator);
			} else if(program == Program.OUTHOUSE){
				status = new SolarMain().connectOuthouse(pArgs, idGenerator);
			} else if(program == Program.ROVER_SETUP){
				status = new SolarMain().connectRoverSetup(pArgs);
			} else {
				System.out.println("Specify mate|rover|outhouse");
			}
			System.exit(status);
		} catch (Exception t) {
			t.printStackTrace();

			pArgs.printInJson();

			System.exit(1);
		}
	}
	private static Program getProgram(List<String> args) {
		if (args.size() == 0) {
			return null;
		}
		switch (args.get(0).toLowerCase()) {
			case "solar": case "mate":
				return Program.MATE;
			case "rover":
				return Program.ROVER;
			case "rover-setup":
				return Program.ROVER_SETUP;
			case "outhouse":
				return Program.OUTHOUSE;
		}
		return null;
	}
	private enum Program {
		MATE,
		ROVER,
		ROVER_SETUP,
		OUTHOUSE
	}
}
