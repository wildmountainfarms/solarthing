package me.retrodaredevil.solarthing.program.action;

import com.lexicalscope.jewel.cli.Option;
import com.lexicalscope.jewel.cli.Unparsed;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public interface RunActionOptions {
	@Option(shortName = "h", longName = "help", helpRequest = true)
	boolean isHelp();

	@Option(longName = "check")
	boolean isCheck();

	@Option(longName = "json")
	boolean isJson();


	@Unparsed(name = "Positional Arguments")
	@Nullable List<String> getPositionalArguments();


}
