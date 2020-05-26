package br.unifesp.ict.seg.geniesearchapi.services.reponotes.infrastructure;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import br.unifesp.ict.seg.geniesearchapi.infrastructure.BaseRepository;

public class RepoNotesRepository extends BaseRepository {

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

	public Map<String, Integer> findAllCountTablesAndViews() {

		Map<String, Integer> map = new HashMap<String, Integer>();
		
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();

			String sql = "";
			sql += "\n select 'comments' tabela, count(*) total from comments union all";
			sql += "\n select 'entities' tabela, count(*) total from entities union all";
			sql += "\n select 'entity_metrics' tabela, count(*) total from entity_metrics union all";
			sql += "\n select 'file_metrics' tabela, count(*) total from file_metrics union all";
			sql += "\n select 'files' tabela, count(*) total from files union all";
			sql += "\n select 'imports' tabela, count(*) total from imports union all";
			sql += "\n select 'problems' tabela, count(*) total from problems union all";
			sql += "\n select 'project_metrics' tabela, count(*) total from project_metrics union all";
			sql += "\n select 'projects' tabela, count(*) total from projects union all";
			sql += "\n select 'relations' tabela, count(*) total from relations union all";
			sql += "\n select 'interface_metrics' tabela, count(*) total from interface_metrics union all";
			sql += "\n select 'interface_metrics_filter' tabela, count(*) total from interface_metrics_filter union all";
			sql += "\n select 'interface_metrics_inner' tabela, count(*) total from interface_metrics_inner union all";
			sql += "\n select 'interface_metrics_pairs' tabela, count(*) total from interface_metrics_pairs union all";
			sql += "\n select 'interface_metrics_pairs_clone_10' tabela, count(*) total from interface_metrics_pairs_clone_10 union all";
			sql += "\n select 'interface_metrics_pairs_inner' tabela, count(*) total from interface_metrics_pairs_inner union all";
			sql += "\n select 'interface_metrics_params' tabela, count(*) total from interface_metrics_params union all";
			sql += "\n select 'interface_metrics_top' tabela, count(*) total from interface_metrics_top union all";
			sql += "\n select 'interface_metrics_types' tabela, count(*) total from interface_metrics_types union all";
			sql += "\n select 'interface_metrics_test' tabela, count(*) total from interface_metrics_test union all";
			sql += "\n select 'interface_metrics_params_test' tabela, count(*) total from interface_metrics_params_test union all";
			sql += "\n select 'interface_metrics_pairs_test' tabela, count(*) total from interface_metrics_pairs_test";

			ResultSet rs = stmt.executeQuery(sql);
		
			while (rs.next()) {
				map.put(rs.getString("tabela"), rs.getInt("total"));
			}

			stmt.close();
			conn.close();

			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return map;
		}
	}
}
