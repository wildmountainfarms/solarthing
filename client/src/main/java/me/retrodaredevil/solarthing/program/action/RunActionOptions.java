package me.retrodaredevil.solarthing.program.action;

import com.lexicalscope.jewel.cli.Option;
import com.lexicalscope.jewel.cli.Unparsed;

import java.util.List;

public interface RunActionOptions {
	@Option(shortName = "h", longName = "help", helpRequest = true)
	boolean isHelp();

	@Option(longName = "check")
	boolean isCheck();

	@Option(longName = "json")
	boolean isJson();


	@Unparsed(name = "Positional Arguments")
	List<String> getPositionalArguments();


}
