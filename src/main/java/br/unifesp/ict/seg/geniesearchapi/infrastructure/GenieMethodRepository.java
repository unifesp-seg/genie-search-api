package br.unifesp.ict.seg.geniesearchapi.infrastructure;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import br.unifesp.ict.seg.geniesearchapi.domain.GenieMethod;

public class GenieMethodRepository extends BaseRepository {

	public int countAllInterfaceMetrics() throws Exception {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();

		String sql = "SELECT count(*) as total FROM interface_metrics";
		ResultSet rs = stmt.executeQuery(sql);

		int total = 0;
		while (rs.next()) {
			total = rs.getInt("total");
		}

		stmt.close();
		conn.close();

		return total;
	}
	
	public GenieMethod findByEntityId(long entityId) throws Exception {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();

		String sql = "SELECT * FROM interface_metrics where entity_id = " + entityId;
		ResultSet rs = stmt.executeQuery(sql);

		GenieMethod genieMethod = null;
		while (rs.next()) {
			genieMethod = new GenieMethod(rs);
		}

		stmt.close();
		conn.close();

		return genieMethod;
	}

	public GenieMethod findByInterfaceElements(String fqn, String params, String returnType) throws Exception {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();

		String sql = "select * from interface_metrics where fqn = '" + fqn + "' and params = '" + params + "' and return_type =  '" + returnType + "'";
		ResultSet rs = stmt.executeQuery(sql);

		GenieMethod genieMethod = null;
		while (rs.next()) {
			genieMethod = new GenieMethod(rs);
		}

		stmt.close();
		conn.close();

		return genieMethod;
	}
}
