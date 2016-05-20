package controller.database;

import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.HashMap;

public class deleteData {
	private String tabla;
	private String llaves;
	private String valores;
	private String titulo;
	private boolean imprimir;
	private Connection Conn = null;
	private String where = "";

	public deleteData(String _tabla, HashMap<String, String> _key, String _titulo, boolean _imprimir) {
		this.tabla = _tabla;
		this.titulo = _titulo;
		this.imprimir = _imprimir;
		String valor = null;
		for (String nomcol: _key.keySet()) {
			valor = _key.get(nomcol);
			if (valor.indexOf("'") > -1)
				this.where += " " + nomcol + " = " + valor + " and";
			else
				this.where += " " + nomcol + " = '" + valor + "' and";
		}
		this.where = this.where.substring(0, this.where.length() - 3);
	}
	
	public deleteData(String _tabla, HashMap<String, String> _key, String _titulo, boolean _imprimir, Connection conn) {
		this.tabla = _tabla;
		this.titulo = _titulo;
		this.imprimir = _imprimir;
		this.Conn = conn;
		String valor = null;
		for (String nomcol: _key.keySet()) {
			valor = _key.get(nomcol);
			if (valor.indexOf("'") > -1)
				this.where += " " + nomcol + " = " + valor + " and";
			else
				this.where += " " + nomcol + " = '" + valor + "' and";
		}
		this.where = this.where.substring(0, this.where.length() - 3);
	}

	public boolean exe() {
		String MyQueryString;
		if (this.where.equals("")) {
			MyQueryString = " DELETE FROM " + this.tabla + " where  " + this.llaves + " = " + this.valores;
		} else {
			MyQueryString = " DELETE FROM " + this.tabla + " where " + this.where;
		}
		if (this.imprimir) {
			System.out.println("**** " + this.titulo + " ***");
			System.out.println(MyQueryString);
		}
		Connection conn = null;
		PreparedStatement ps = null;
		Statement stm = null;
		boolean retorno = false;
		try {
			if (this.Conn == null) {
				conn = new DBConnection().get();
				conn.setAutoCommit(false);
			} else {
				conn = this.Conn;
			}
			stm = conn.createStatement();
			stm.executeUpdate(MyQueryString);
			if (this.Conn == null) {
				stm.execute("commit");
			}
			retorno = true;
		} catch (Exception e) {
			System.out.println("**** " + this.titulo + " ****");
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (this.Conn == null) {
					if (conn != null) {
						conn.close();
					}
				}
				ps = null;
				conn = null;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return retorno;
	}
}