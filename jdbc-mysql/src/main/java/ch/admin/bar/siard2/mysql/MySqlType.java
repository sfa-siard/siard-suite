package ch.admin.bar.siard2.mysql;

/**
 * Contains all built-in MySql Types
 * @author Simon Jutz
 */
public enum MySqlType {
	BIT("bit"),
	MULTIBIT("bit_multi"),
	TINYINT("tinyint"),
	TINYINTU("tinyint unsigned"),
	BOOL("bool"),
	BOOLEAN("boolean"),
	SMALLINT("smallint"),
	SMALLINTU("smallint unsigned"),
	MEDIUMINT("mediumint"),
	MEDIUMINTU("mediumint unsigned"),
	INT("int"),
	INTU("int unsigned"),
	INTEGER("integer"),
	INTEGERU("integer unsigned"),
	BIGINT("bigint"),
	BIGINTU("bigint unsigned"),
	FLOAT("float"),
	FLOATU("float unsigned"),
	DOUBLE("double"),
	DOUBLEU("double unsigned"),
	DECIMAL("decimal"),
	DECIMALU("decimal unsigned"),
	DATE("date"),
	DATETIME("datetime"),
	TIMESTAMP("timestamp"),
	TIME("time"),
	YEAR("year"),
	CHAR("char"),
	VARCHAR("varchar"),
	BINARY("binary"),
	VARBINARY("varbinary"),
	TINYBLOB("tinyblob"),
	TINYTEXT("tinytext"),
	BLOB("blob"),
	TEXT("text"),
	MEDIUMBLOB("mediumblob"),
	MEDIUMTEXT("mediumtext"),
	LONGBLOB("longblob"),
	LONGTEXT("longtext"),
	ENUM("enum"),
	SET("set"),
	GEOMETRY("geometry"),
	POINT("point"),
	LINESTRING("linestring"),
	POLYGON("polygon"),
	MULTIPOINT("multipoint"),
	MULTILINESTRING("multilinestring"),
	MULTIPOLYGON("multipolygon"),
	GEOMETRYCOLLECTION("geometrycollection"),
	JSON("json");
	
	private String sTypeName = null;
	
	/**
	 * constructor
	 * @param _sTypeName the system name of the type
	 */
	MySqlType(String _sTypeName) {
		sTypeName = _sTypeName;
	} /* constructor */
	
	/**
	 * Gets the enum constant with a given type name
	 * @param _sTypeName the type name
	 * @return enum constant with the given type name
	 */
	public static MySqlType getByTypeName(String _sTypeName) {
		// workaround for failing test with mysql 8.0
		if(_sTypeName.equals("geomcollection")) {
			return GEOMETRYCOLLECTION;
		}
		
		// Special handling for BIT types with length > 1
		if(_sTypeName.startsWith("bit(")) {
			String sBitLength = _sTypeName.substring(4, _sTypeName.indexOf(')'));
			try {
				int iBitLength = Integer.parseInt(sBitLength);
				if (iBitLength > 1) {
					return MULTIBIT;
				}
				return BIT;
			} catch (NumberFormatException e) {
				// Fall through to normal handling
			}
		}
		
		MySqlType result = null;
		for(int i=0;i<MySqlType.values().length;i++) {
			MySqlType t = MySqlType.values()[i];
			if(t.getTypeName().equals(_sTypeName)) {
				result = t;
				break;
			}
		}
		return result;
	}
	
	/**
	 * Gets the type name
	 * @return the type name
	 */
	public String getTypeName() {
		return sTypeName;
	}
		
} /* enum MySqlType */
