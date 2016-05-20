package controller.database;

import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;

public class updateData {
	private String tabla;
	private HashMap<String, String> llaves;
	private HashMap<String, String> valores;
	private String where = "";
	private String titulo;
	private boolean imprimir;
	private String columnClob = "";
	private String txtClob = "";
	private boolean clob = false;
	private Connection _conn = null;

	public updateData(String _tabla, HashMap<String, String> _llaves, HashMap<String, String> _valores, String _titulo, boolean _imprimir) {
		tabla = _tabla;
		llaves = _llaves;
		valores = _valores;
		titulo = _titulo;
		imprimir = _imprimir;
	}
	
	public updateData(String _tabla, HashMap<String, String> _llaves, HashMap<String, String> _valores, String _titulo, boolean _imprimir, Connection conn) {
		tabla = _tabla;
		llaves = _llaves;
		valores = _valores;
		titulo = _titulo;
		imprimir = _imprimir;
		_conn = conn;
	}

	public boolean exe() {
		String cad = "";
		for (String col: llaves.keySet())
			cad += col + ", ";
		
		for (String col: valores.keySet())
			cad += col + ", ";
		
		cad = cad.substring(0, cad.length() - 2);
		String valor = null;
		for (String nomcol: llaves.keySet()) {
			valor = llaves.get(nomcol);
			where += nomcol + " = " + valor + " and ";
		}
		where = where.substring(0, where.length() - 4);
		String MyQueryString = " select " + cad + " from " + tabla + " where " + where;
		if (imprimir) {
			System.out.println("**** " + titulo + " ***");
			System.out.println(MyQueryString);
		}
		Connection conn = null;
		PreparedStatement ps = null;
		Statement stm = null;
		ResultSet rset = null;
		ResultSetMetaData rmeta = null;
		boolean retorno = false;
		try {
			if (_conn == null) {
				conn = (new DBConnection()).get();
				conn.setAutoCommit(false);
			} else {
				conn = _conn;
			}
			stm = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rset = stm.executeQuery(MyQueryString);
			rmeta = rset.getMetaData();
			String valordato = null;
			if (rset.first()) {
				for (int j = 1; j <= rmeta.getColumnCount(); j++) {
					valordato = (String) valores.get(rmeta.getColumnName(j));
					if (imprimir)
						System.out.println("Procesando Columna --> " + j + " " + rmeta.getColumnName(j) + " Tipo: " + rmeta.getColumnType(j) + " valor : " + valordato);
					if (valordato != null) {
						if (valordato.trim().equals("")) {
							rset.updateNull(rmeta.getColumnName(j));
						} else {
							switch (rmeta.getColumnType(j)) {
							case -5:
								try {
									rset.updateBigDecimal(rmeta.getColumnName(j), BigDecimal.valueOf(Long.parseLong(valordato)));
								} catch (Exception ex) {
									try {
										valordato = valordato.replace(",", ".");
										rset.updateDouble(rmeta.getColumnName(j), Double.parseDouble(valordato));
									} catch (Exception ex1) {
										rset.updateNull(rmeta.getColumnName(j));
									}
								}
								break;
							case 0: // assigned null
								// por ahora nunca se espera que pase por aqu�
								break;
							case 2: // NUMERIC
							case 3: // Decimal
							case 4: // integer
							case 5: // SMALLINT
							case 6: // Float
							case 7: // REAL
								try {
									rset.updateBigDecimal(rmeta.getColumnName(j), BigDecimal.valueOf(Integer.parseInt(valordato)));
								} catch (Exception ex) {
									try {
										/*
										 * Si el separador decimal es "," lo
										 * cambia por "."
										 */
										valordato = valordato.replace(",", ".");
										rset.updateDouble(rmeta.getColumnName(j), Double.parseDouble(valordato));
									} catch (Exception ex1) {
										rset.updateNull(rmeta.getColumnName(j));
									}
								}
								break;
							case 1: // Char
							case 12: // Varchar
								try {
									rset.updateString(rmeta.getColumnName(j), valordato);
								} catch (Exception e) {
									rset.updateNull(rmeta.getColumnName(j));
								}
								break;
							case 92: // Time
								try {
									rset.updateTime(rmeta.getColumnName(j), Time.valueOf(valordato));
								} catch (Exception e) {
									rset.updateNull(rmeta.getColumnName(j));
								}
								break;
							case 93: // TimeStamp
								if (rmeta.getColumnTypeName(j).equals("DATE")) {
									try {
										if (valordato.indexOf(":") > 0) {
											rset.updateTimestamp(rmeta.getColumnName(j), Timestamp.valueOf(valordato));
										} else {
											if (valordato.indexOf("/") >= 0) {
												rset.updateDate(rmeta.getColumnName(j), java.sql.Date.valueOf(valordato.replace("/", "-")));
											} else {
												rset.updateDate(rmeta.getColumnName(j), java.sql.Date.valueOf(valordato));
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
										rset.updateNull(rmeta.getColumnName(j));
									}
								} else {
									try {
										rset.updateTimestamp(rmeta.getColumnName(j), Timestamp.valueOf(valordato));
									} catch (Exception e) {
										rset.updateNull(rmeta.getColumnName(j));
									}
								}
								break;
							case 2005: // Clob
								try { // valor datos
									clob = true;
									txtClob = valordato;
									columnClob = rmeta.getColumnName(j);
								} catch (Exception e) {
								}
								break;

							case 2004: // Blob
								break;
							case 91: // Date
								try {
									if (valordato.indexOf("/") >= 0) {
										rset.updateDate(rmeta.getColumnName(j), java.sql.Date.valueOf(valordato.replace("/", "-")));
									} else {
										rset.updateDate(rmeta.getColumnName(j), java.sql.Date.valueOf(valordato));
									}
								} catch (Exception e) {
									rset.updateNull(rmeta.getColumnName(j));
								}
								break;
							} // del switch
							valordato = "";
						}
					}
				} // for j
				rset.updateRow();
				if (_conn == null) {
					conn.commit();
				}
				if (clob) { // almacenamos los datos del clob
					System.out.println("_CLOB--->" + clob);
					if (updateClob(conn))
						retorno = true;
				} else
					retorno = true;

			}
		} catch (Exception e) {
			System.out.println("**** " + titulo + " ***");
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (_conn == null) {
					if (conn != null)
						conn.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return retorno;
	}

	private boolean updateClob(Connection conn) {
		boolean retorno = false;
		Statement stm = null;
		ResultSet rset = null;
		try {
			stm = conn.createStatement();
			String q = "update " + tabla + " set " + columnClob + "=empty_clob() where " + where;
			stm.execute(q);
			q = " select " + columnClob + " from " + tabla + " where " + where + " for update ";
			rset = stm.executeQuery(q);
			rset.next();
			Clob c = rset.getClob(1);
			Writer wout = c.setCharacterStream(1);
			wout.write(txtClob);
			wout.flush();
			wout.close();
			if (_conn == null) {
				conn.commit();
			}
			retorno = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retorno;
	}
}
