package es.nortia_in.orm.gorm.query

/**
 * Token for field comparations in parsed queries
 * @author angel
 *
 */
class ComparationToken {

	/**
	 * Field to compare
	 */
	String fieldName
	
	/**
	 * Value to compare with
	 */
	Object value
	
	/**
	 * Operator. EQUAL by default 
	 */
	String operator = Comparators.EQUAL
	
	
	boolean equals(Object obj) {
		if (!(obj instanceof ComparationToken)) {
			return false
		}
		
		if (!obj) {
			return false
		}
		
		return fieldName == obj.fieldName && value == obj.value && operator == obj.operator
	}
	
	void setFieldName(String fieldName) {
		
		//Defensive programming...
		if (!fieldName && fieldName.size() <= 1) {
			this.fieldName = fieldName
			return
		}
		
		//Transform first char to lowercase
		this.fieldName = fieldName[0].toLowerCase()+fieldName[1..-1]
	}
	
	String toString() {
		return "$fieldName $operator $value"
	}
}
