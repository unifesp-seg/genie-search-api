package br.unifesp.ict.seg.geniesearchapi.infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;

import br.unifesp.ict.seg.geniesearchapi.infrastructure.util.GenieSearchAPIConfig;

public abstract class BaseRepository {

	public final Connection getConnection() throws Exception {
		String url = GenieSearchAPIConfig.getDatabaseURL();
		String user = GenieSearchAPIConfig.getDatabaseUser();
		String pass = GenieSearchAPIConfig.getDatabasePassword();

		Connection conn = DriverManager.getConnection(url, user, pass);

		return conn;
	}
}
