package me.retrodaredevil.solarthing.program;

import me.retrodaredevil.solarthing.SolarThingConstants;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.program.action.RunActionMain;
import me.retrodaredevil.solarthing.program.subprogram.analyze.AnalyzeMain;
import me.retrodaredevil.solarthing.program.check.CheckMain;
import me.retrodaredevil.solarthing.program.subprogram.run.RunMain;

import java.time.Instant;

@UtilityClass
public final class SolarMain {
	private SolarMain(){ throw new UnsupportedOperationException(); }


	private static int outputVersion() {
		JarUtil.Data data = JarUtil.getData();
		Instant lastModified = data.getLastModifiedInstantOrNull();
		String commitHash = SolarThingEnvironment.getGitCommitHash();
		String ref = SolarThingEnvironment.getRef();
		System.out.println("SolarThing made by Lavender Shannon\n" +
				"Jar: " + data.getJarFileNameOrNull() + "\n" +
				"Jar last modified: " + (lastModified == null ? "unknown" : lastModified.toString()) + "\n" +
				(commitHash == null ? "" : "Commit hash: " + commitHash + "\n") +
				(ref == null ? "" : "Ref: " + ref + "\n") +
				"Java version: " + System.getProperty("java.version"));
		return 0;
	}

	private static int determineMainSubprogram(String[] args) {
		if (args.length == 0) {
			System.err.println("Usage: solarthing [command] [args]\n\n" +
					"Commands:\n" +
					"  run [options]\n" +
					"  version\n" +
					"  check --port <serial port> [--type <type>]\n" +
					"  action [file]\n" +
					"  analyze [options]");
			return SolarThingConstants.EXIT_CODE_INVALID_OPTIONS;
		}
		String firstArg = args[0];
		String[] subArgs = new String[args.length - 1];
		System.arraycopy(args, 1, subArgs, 0, args.length - 1);
		switch (firstArg) {
			case "run":
				return RunMain.runMain(subArgs);
			case "version":
				return outputVersion();
			case "action":
				return RunActionMain.runAction(subArgs);
			case "check":
				return CheckMain.doCheck(subArgs);
			case "analyze":
				return AnalyzeMain.analyze(subArgs);
			default:
				System.err.println("Unknown argument: " + firstArg + "\n" +
						"Previous SolarThing versions allowed the running of SolarThing without the 'run' prefix.\n" +
						"Adding 'run' may fix your problem.");
				return 1;
		}
	}

	public static void main(String[] args) {
		System.exit(determineMainSubprogram(args));
	}
}
