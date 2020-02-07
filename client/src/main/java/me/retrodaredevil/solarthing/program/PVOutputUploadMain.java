package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.config.options.PVOutputUploadProgramOptions;
import me.retrodaredevil.solarthing.pvoutput.service.OkHttpUtil;
import me.retrodaredevil.solarthing.pvoutput.service.PVOutputService;
import me.retrodaredevil.solarthing.pvoutput.service.RetrofitUtil;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;

public class PVOutputUploadMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(PVOutputUploadMain.class);
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	@SuppressWarnings("SameReturnValue")
	public static int startPVOutputUpload(PVOutputUploadProgramOptions options){
		LOGGER.info("Starting PV Output upload program");
		OkHttpClient client = OkHttpUtil.configure(new OkHttpClient.Builder(), options.getApiKey(), options.getSystemId())
				.build();
		Retrofit retrofit = RetrofitUtil.defaultBuilder().client(client).build();
		PVOutputService service = retrofit.create(PVOutputService.class);

		while(!Thread.currentThread().isInterrupted()){
			LOGGER.debug("Going to do stuff now.");
			try {
				Thread.sleep(5 * 60 * 1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		return 1;
	}
}
