package br.unifesp.ict.seg.geniesearchapi.infrastructure;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import br.unifesp.ict.seg.geniesearchapi.domain.GenieMethod;

public class GenieMethodRepository extends BaseRepository {

	public boolean checkConnection() {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();

			String sql = "select 1 from dual";
			ResultSet rs = stmt.executeQuery(sql);

			int total = 0;
			while (rs.next()) {
				total = rs.getInt("1");
			}

			stmt.close();
			conn.close();

			return total == 1;
		} catch (Exception e) {
			return false;
		}
	}

	public int countAllInterfaceMetrics() {
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public GenieMethod findByEntityId(long entityId) {
		GenieMethod genieMethod = new GenieMethod(entityId);
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();

			String sql = "SELECT * FROM interface_metrics where entity_id = " + entityId;
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				genieMethod = new GenieMethod(rs);
			}

			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			return genieMethod;
		}

		return genieMethod;
	}

	public GenieMethod findByInterfaceElements(String fqn, String params, String returnType) {
		GenieMethod genieMethod = null;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();

			String sql = "select * from interface_metrics where fqn = '" + fqn + "' and params = '" + params + "' and return_type =  '" + returnType + "'";
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				genieMethod = new GenieMethod(rs);
			}

			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			return genieMethod;
		}

		return genieMethod;
	}
}
