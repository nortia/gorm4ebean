package es.nortia_in.orm.pk

import java.text.DecimalFormat
import java.text.SimpleDateFormat

import org.codehaus.groovy.runtime.GStringImpl;

import es.nortia_in.orm.gorm.EBeanGormException

/**
 * Base class for  pk component
 * @author angel
 *
 */
abstract class PkComponent {

	Class type

	String format

	int length

	/**
	 * Retrieve actual current lenght
	 * @return
	 */
	int getCurrentLenght() {
		return length ?: format?.size() ?:0
	}

	void setType(Class type) {
		assert type
		this.type = type

		//Booleans are ever 1 lenght
		if (type in [Boolean.class, boolean.class]) {
			length = 1
		}
	}

	public def validate(String pk) {

		if (!pk) {
			return null
		}

		//Check size
		if (pk.size() < getCurrentLenght()) {
			return null
		}

		def toValidate = pk[0..getCurrentLenght()-1]

		boolean ok = true
		//The property is a Date value
		if (Date.isAssignableFrom(type)) {
			if (!format) {
				throw new EBeanGormException("Composite Pk property date value must define a format")
			}

			try{
				
				def sdf = new SimpleDateFormat(format)
				sdf.setLenient(false)
				def parsedDate = sdf.parse(toValidate)
				
				//Check strict parsing by reformatting date and verifying that formatted date is equals to original date
				if (toValidate != sdf.format(parsedDate)) {
					ok = false
				}
				
			}catch(IllegalArgumentException e){
				// if string date can not be parsed document id format is wrong
				ok = false
			}
		} else

		//The property is a Number value
		if (Number.isAssignableFrom(type) || (type in [int.class, long.class, byte.class, double.class, float.class])) {
			if (!format) {
				throw new EBeanGormException("Composite Pk property number value must define a format")
			}

			// checks sequence format
			if(toValidate.size() != format.size() || !toValidate.matches(/\d+/)){
				ok = false
			}
		}

		if (!ok) {
			return null
		}


		return pk - toValidate
	}

	public String getValue(Object entity) {

		def value = doGetValue(entity)
		def result = ""

		//The property is a Date value
		if (value instanceof Date) {
			if (!format) {
				throw new EBeanGormException("Composite Pk property date value must define a format")
			}
			value = new SimpleDateFormat(format).format(value)
		} else

		//The property is a Number value
		if (value instanceof Number) {
			if (!format) {
				throw new EBeanGormException("Composite Pk property number value must define a format")
			}
			value = new DecimalFormat(format).format(value)
	
			
		} else
		
		//The value is a boolean
		if (value instanceof Boolean) {
			value = value ? "1" : "0"
		}

		
		//Configure lenght value
		if (!length) {
			length = format?.size() ?:0
		}
		
		//The property is a String value
		if (value?.class in [String, GStringImpl]) {
			if (length) {
				//Shorten value
				if (value.size() > length) {
					result = value[0..length-1]
				} else {
					//Extend value
					result = value.padLeft(length)
				}
				
			} else {
				result = value
			}
			
			// if type is numeric, replace blanks with zeros
			if(Number.isAssignableFrom(type)){
				result = result.replace(" ", "0")
			}
			
		} else {
			result = value
		}

		return result
	}

	abstract def doGetValue(Object entity)

}
