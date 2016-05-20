package controller.database;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashMap;
import java.util.ArrayList;

public class getData {
	private String _query;
	private String _titulo;
	private boolean _imprimir;
	private String _fileBlob = "";
	private Connection _conn = null, Conn = null;
	private PreparedStatement ps = null;
	private Statement stm = null;
	private ResultSet rset = null;
	private ResultSetMetaData rmetada = null;
	private ArrayList<HashMap<String, String>> valores = new ArrayList<HashMap<String, String>>();
	private int bufSize = 4 * 8192;

	public getData(String query, String titulo, boolean imprimir) {
		_query = query;
		_titulo = titulo;
		_imprimir = imprimir;
	}
	
	public ArrayList<HashMap<String, String>> exe() {
		Calendar ahora = Calendar.getInstance();
		try {
			if (_imprimir) {
				System.out.println("********* " + _titulo + " *********");
				System.out.println(_query);
				System.out.println("**************************************************");
			}
			if (_conn == null) {
				Conn = (new DBConnection()).get();
				Conn.setAutoCommit(false);
			} else {
				Conn = _conn;
			}

			stm = Conn.createStatement();
			rset = stm.executeQuery(_query);
			rmetada = rset.getMetaData();
			String valor = "";
			if (_imprimir) {
				System.out.println("*********  columnas **************");
				for (int j = 1; j <= rmetada.getColumnCount(); j++)
					System.out.println("Procesando -> " + j + " Columna " + rmetada.getColumnName(j) + " Tipo " + rmetada.getColumnType(j));
			}

			while (rset.next()) {
				HashMap<String, String> data = new HashMap<String, String>();
				for (int j = 1; j <= rmetada.getColumnCount(); j++) {
					switch (rmetada.getColumnType(j)) {
					case 0: // assigned null
						data.put(rmetada.getColumnName(j), "");
						break;
					case 2: // NUMERIC
					case 3: // Decimal
					case 4: // integer
					case 5: // SMALLINT
					case 6: // Float
					case 7: // REAL
					case 8:
						try {
							valor = rset.getBigDecimal(rmetada.getColumnName(j)).toString();
						} catch (Exception ex2) {
							ex2.printStackTrace();
							try {
								valor = String.valueOf(rset.getDouble(rmetada.getColumnName(j)));
							} catch (Exception ex2a) {
								valor = "";
							}
						}
						data.put(rmetada.getColumnName(j), valor);
						break;
					case 1: // Char
					case 12: // Varchar
						try {
							valor = rset.getString(rmetada.getColumnName(j)).toString();
						} catch (Exception ex3) {
							valor = "";
						}
						data.put(rmetada.getColumnName(j), valor);
						break;
					case 92: // Time
						try {
							valor = rset.getTime(rmetada.getColumnName(j)).toString();
						} catch (Exception ex4) {
							valor = "";
						}
						data.put(rmetada.getColumnName(j), valor);
						break;
					case 93: // TimeStamp
						try {
							if (rmetada.getColumnTypeName(j).equals("DATE")) {
								valor = rset.getDate(rmetada.getColumnName(j)).toString();
							} else {
								valor = rset.getTimestamp(rmetada.getColumnName(j)).toString();
							}
						} catch (Exception ex5) {
							valor = "";
						}
						data.put(rmetada.getColumnName(j), valor);
						break;
					case 2005: // Clob
						StringBuffer dataClob = new StringBuffer();
						try {
							Clob clob = rset.getClob(rmetada.getColumnName(j));
							Reader rclob = clob.getCharacterStream();
							int car = 0;
							char[] buf = new char[bufSize];
							while ((car = rclob.read(buf)) != -1)
								dataClob.append(buf, 0, car);
						} catch (Exception e) {
							dataClob.append(" ");
							System.out.println(e.getMessage());
						}
						data.put(rmetada.getColumnName(j), dataClob.toString());
						break;
					case 2004: // Blob
						if (_fileBlob != null || !_fileBlob.trim().equals("")) {
							try {
								Blob blob = rset.getBlob(rmetada.getColumnName(j));
								InputStream blobStream = blob.getBinaryStream();
								FileOutputStream fileOutStream = new FileOutputStream(_fileBlob);
								byte[] buffer = new byte[bufSize];
								int nbytes = 0;
								while ((nbytes = blobStream.read(buffer)) != -1)
									fileOutStream.write(buffer, 0, nbytes);
								fileOutStream.close();
							} catch (FileNotFoundException ex1) {
								_fileBlob = null;
							} catch (IOException ex1) {
								_fileBlob = null;
							} catch (Exception ex1) {
								_fileBlob = null;
							}
						}
						if (_fileBlob != null)
							data.put(rmetada.getColumnName(j), _fileBlob);
						break;
					case 91: // Date
						try {
							valor = rset.getDate(rmetada.getColumnName(j)).toString();
						} catch (Exception ex5) {
							valor = "";
						}
						data.put(rmetada.getColumnName(j), valor);
						break;
					}
				}
				valores.add(data);
			}
			if (_imprimir) {
				Calendar ahora_final = Calendar.getInstance();
				System.out.println("********* TIEMPO DE EJECUCIÓN : " + (ahora_final.getTimeInMillis() - ahora.getTimeInMillis()) + " ms.");
			}
			return (valores);
		} catch (Exception e) {
			try {
				e.printStackTrace();
				return (null);
			} catch (Exception exerr) {
				exerr.printStackTrace();
				return (null);
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
					ps = null;
				}
				if (_conn == null) {
					Conn.close();
					Conn = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}