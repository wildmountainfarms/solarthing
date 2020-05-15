package me.retrodaredevil.grafana.datasource.endpoint.root;

import javax.ws.rs.core.Response;

public interface RootResource {
	Response checkConnection();
}
