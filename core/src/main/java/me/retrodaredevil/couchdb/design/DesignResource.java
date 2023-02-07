package me.retrodaredevil.couchdb.design;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public enum DesignResource {
	// Validate
	VALIDATE_JAVASCRIPT_READONLY_AUTH("validation/readonly_auth.js"),
	VALIDATE_JAVASCRIPT_UPLOAD_ONLY("validation/upload_only.js"),

	// View
	VIEW_JAVASCRIPT_MILLIS_NULL("view/millisNull.js"),
	VIEW_JAVASCRIPT_SIMPLE_ALL_DOCS("view/simpleAllDocs.js"),
	;
	/*
	In IntelliJ to make the errors go away for those JavaScript files, go to Code > Analyze Code > Configure Current File Analysis,
	then
	 */
	/*
	TODO Add IntelliJ gradle plugin and auto configure some settings in .idea/workspace.xml
	<component name="HighlightingSettingsPerFile">
	  <setting file="file://$PROJECT_DIR$/core/src/main/resources/me/retrodaredevil/couchdb/design/validation/readonly_auth.js" root0="SKIP_HIGHLIGHTING" />
	  <setting file="file://$PROJECT_DIR$/core/src/main/resources/me/retrodaredevil/couchdb/design/validation/upload_only.js" root0="SKIP_HIGHLIGHTING" />
	  <setting file="file://$PROJECT_DIR$/core/src/main/resources/me/retrodaredevil/couchdb/design/view/millisNull.js" root0="SKIP_HIGHLIGHTING" />
	  <setting file="file://$PROJECT_DIR$/core/src/main/resources/me/retrodaredevil/couchdb/design/view/simpleAllDocs.js" root0="SKIP_HIGHLIGHTING" />
	</component>
	*/
	private final String fileName;
	@SuppressWarnings("ImmutableEnumChecker") // This enum serves as a cache, which is state, which I have decided is OK in this particular case
	private byte[] data = null;


	DesignResource(String fileName) {
		this.fileName = fileName;
	}

	public void load() {
		get();
	}

	public synchronized byte[] get() {
		if (data == null) {
			InputStream inputStream = getClass().getResourceAsStream(fileName);
			if (inputStream == null) {
				throw new IllegalStateException("Could not find resource: " + fileName);
			}
			try {
				data = new byte[inputStream.available()];
				new DataInputStream(inputStream).readFully(data);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return data;
	}
	public String getAsString(Charset charset) {
		return new String(get(), charset);
	}
	public String getAsString() {
		return getAsString(StandardCharsets.UTF_8);
	}
}
