package controller.database;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;

public class insertData {
	private String _tabla;
	private String _titulo;
	private HashMap<String, String> _valores;
	private boolean _imprimir;
	private byte[] _bytesClob;
	private boolean _clob = false;
	private String _columnClob = "";
	private String _columnKey = "";
	private String _valueKey = "";
	private Connection _conn = null;

	public insertData(String tabla, HashMap<String, String> valores, String titulo, boolean imprimir) {
		_tabla = tabla;
		_titulo = titulo;
		_valores = valores;
		_imprimir = imprimir;
	}
	
	public insertData(String tabla, HashMap<String, String> valores, String titulo, boolean imprimir, Connection conn) {
		_tabla = tabla;
		_titulo = titulo;
		_valores = valores;
		_imprimir = imprimir;
		_conn = conn;
	}

	public BigDecimal exe() {
		String cad = "";
		for (String col : _valores.keySet())
			cad += col + ", ";

		cad = cad.substring(0, cad.length() - 2);
		String MyQueryString = " select " + cad + " from " + _tabla + " where 1 = 2 ";
		if (_imprimir) {
			System.out.println("**** " + _titulo + " ***");
			System.out.println(MyQueryString);
		}

		Connection conn = null;
		PreparedStatement ps = null;
		Statement stm = null;
		ResultSet rset = null;
		ResultSetMetaData rmeta = null;
		BigDecimal retorno = null;
		try {
			if (_conn == null) {
				conn = (new DBConnection()).get();
				conn.setAutoCommit(false);
			} else
				conn = _conn;
			stm = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rset = stm.executeQuery(MyQueryString);
			rmeta = rset.getMetaData();
			rset.moveToInsertRow();

			String valordato = null;
			for (int j = 1; j <= rmeta.getColumnCount(); j++) {
				valordato = (String) _valores.get(rmeta.getColumnName(j));
				if (valordato != null) {
					if (valordato.trim().equals("")) {
						rset.updateNull(rmeta.getColumnName(j));
					} else {// pero si lo encontro asigna el valor
						if (_imprimir) {
							System.out.println("Procesando Columna --> " + j + " " + rmeta.getColumnName(j) + " Tipo: "
									+ rmeta.getColumnType(j) + " valor: " + valordato);
						}

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
						case 8: //Decimal
							if (!valordato.equals("default"))
								try {
									rset.updateBigDecimal(rmeta.getColumnName(j),
											BigDecimal.valueOf(Integer.parseInt(valordato)));
								} catch (Exception ex) {
									try {
										valordato = valordato.replace(",", ".");
										rset.updateDouble(rmeta.getColumnName(j), Double.parseDouble(valordato));
									} catch (Exception ex1) {
										rset.updateNull(rmeta.getColumnName(j));
									}
								}
							else {
								retorno = getSequence(rmeta.getColumnName(j), conn);
								rset.updateBigDecimal(rmeta.getColumnName(j), retorno);
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
									if (valordato.indexOf("/") >= 0) {
										rset.updateDate(rmeta.getColumnName(j),
												java.sql.Date.valueOf(valordato.replace("/", "-")));
									} else {
										rset.updateDate(rmeta.getColumnName(j),
												java.sql.Date.valueOf(valordato.substring(0, 10)));
									}
								} catch (Exception e) {
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
								_clob = true;
								_bytesClob = valordato.getBytes();
								_columnClob = rmeta.getColumnName(j);
							} catch (Exception e) {
							}
							break;
						case 2004: // Blob
							break;
						case 91: // Date
							try {
								if (valordato.indexOf("/") >= 0) {
									rset.updateDate(rmeta.getColumnName(j),
											java.sql.Date.valueOf(valordato.replaceAll("/", "-")));
								} else {
									rset.updateDate(rmeta.getColumnName(j), java.sql.Date.valueOf(valordato));
								}
							} catch (Exception e) {
								e.printStackTrace();
								rset.updateNull(rmeta.getColumnName(j));
							}
							break;
						} // del switch
						valordato = "";
					}
				}

			} // for j
			rset.insertRow();
			if (_conn == null) {
				conn.commit();
			}
			if (_clob) { // almacenamos los datos del clob
				if (!updateClob(conn))
					retorno = null;
			}
		} catch (Exception e) {
			System.out.println("**** " + _titulo + " ***");
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
			String q = "update " + _tabla + " set " + _columnClob + "=empty_clob() where " + _columnKey + "="
					+ _valueKey;
			stm.execute(q);
			q = " select " + _columnClob + " from " + _tabla + " where " + _columnKey + "=" + _valueKey
					+ " for update ";
			rset = stm.executeQuery(q);
			rset.next();
			Clob clob = rset.getClob(1);
			OutputStream out = clob.setAsciiStream(1);
			int length = _bytesClob.length;
			byte[] buf = _bytesClob;
			out.write(buf, 0, length);
			out.flush();
			out.close();
			conn.commit();
			retorno = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retorno;
	}

	public BigDecimal getSequence(String field, Connection conn) throws SQLException {
		String query = "SELECT nextval('" + _tabla + "_" + field + "_seq')";
		PreparedStatement pstmt = conn.prepareStatement(query);
		ResultSet rset = pstmt.executeQuery();
		if(rset.next())
			return rset.getBigDecimal(1);
		return null;
	}
}
