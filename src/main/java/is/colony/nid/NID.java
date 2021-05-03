package is.colony.nid;

import java.time.LocalDate;

public class NID {

	/**
	 * Stores the NID as a string of ten characters.
	 */
	private final String _nidString;

	public NID( final String nidString ) {
		_nidString = nidString;
	}

	public static NID of( final String nidString ) {
		return new NID( nidString );
	}

	public boolean isIndividual() {
		return NID.Util.isIndividual( _nidString );
	}

	public boolean isCompany() {
		return NID.Util.isCompany( _nidString );
	}

	public static class Util {

		private static final String DEFAULT_DELIMITER = "-";

		private Util() {}

		/**
		 * Returns true if the persidno is an individual
		 *
		 * @param nidString The persidno to check.
		 */
		public static boolean isIndividual( String nidString ) {

			if( nidString == null ) {
				return false;
			}

			nidString = cleanupNIDString( nidString );
			int marker = Integer.parseInt( nidString.substring( 0, 1 ) );
			return marker < 4;
		}

		/**
		 * Returns true if the persidno is for a company.
		 *
		 * @param persidno The persidno to check.
		 */
		public static boolean isCompany( String persidno ) {

			if( persidno == null ) {
				return false;
			}

			persidno = cleanupNIDString( persidno );
			int marker = Integer.parseInt( persidno.substring( 0, 1 ) );
			return marker == 4 || marker == 5 || marker == 6 || marker == 7;
		}

		/**
		 * Gets the birthdate of the given individual.
		 *
		 * @param nidString The persidno to check.
		 * @return the birthdate of the individual.
		 */
		public static LocalDate dateOfBirthFromNIDString( String nidString ) {

			nidString = cleanupNIDString( nidString );

			int day = dayOfBirth( nidString );
			int month = monthOfBirth( nidString );
			int year = yearOfBirth( nidString );

			return LocalDate.of( year, month, day );
		}

		/**
		 * Derives a person's age from a persidno.
		 *
		 * @param nidString The persidno to check.
		 * @param date     The date to use as reference.
		 */
		public static Integer ageFromNIDString( final String nidString, final LocalDate date ) {

			if( nidString == null || date == null ) {
				return null;
			}

			LocalDate birthDate = dateOfBirthFromNIDString( nidString );
			return ageAtDate( birthDate, date );
		}

		/**
		 * Calculates the age of a person given a birthdate and another date.
		 *
		 * @param birthDate The birthdate of the person to check.
		 * @param date      The date at which we want to know the person's age. If null, we assume you want the current age.
		 * @return The person's age at [date]
		 */
		public static Integer ageAtDate( final LocalDate birthDate, final LocalDate date ) {

			int ageInYears = date.getYear() - birthDate.getYear();

			if( birthDate.getMonthValue() <= date.getMonthValue() ) {
				if( birthDate.getDayOfMonth() <= date.getDayOfMonth() ) {
					return ageInYears;
				}
			}

			return ageInYears - 1;
		}

		/**
		 * Validates a persidno's structure to see if it complies with the validation algorithm.
		 *
		 * @param nidString The persidno to check, should be of the format "##########"
		 */
		public static boolean validateNIDString( final String nidString ) {

			if( nidString == null ) {
				return false;
			}

			if( nidString.length() != 10 ) {
				return false;
			}

			if( !isDigitsOnly( nidString ) ) {
				return false;
			}

			int sum = Character.digit( nidString.charAt( 0 ), 10 ) * 3;
			sum = sum + Character.digit( nidString.charAt( 1 ), 10 ) * 2;
			sum = sum + Character.digit( nidString.charAt( 2 ), 10 ) * 7;
			sum = sum + Character.digit( nidString.charAt( 3 ), 10 ) * 6;
			sum = sum + Character.digit( nidString.charAt( 4 ), 10 ) * 5;
			sum = sum + Character.digit( nidString.charAt( 5 ), 10 ) * 4;
			sum = sum + Character.digit( nidString.charAt( 6 ), 10 ) * 3;
			sum = sum + Character.digit( nidString.charAt( 7 ), 10 ) * 2;

			int leftovers = (sum % 11);
			int validationNumber = 11 - leftovers;
			int vartala = Character.digit( nidString.charAt( 8 ), 10 );

			if( validationNumber == 11 ) {
				validationNumber = 0;
			}

			if( validationNumber == vartala ) {
				return true;
			}

			return false;
		}

		/**
		 * Attempts to format a persidno string to the standard used in the DB (removes dashes and spaces).
		 *
		 * @param nidString The persidno to format.
		 */
		public static String cleanupNIDString( final String nidString ) {

			if( nidString == null ) {
				return null;
			}

			String stringPersidno = nidString;

			if( stringPersidno != null && !stringPersidno.isEmpty() ) {
				stringPersidno = stringPersidno.replace( "-", "" );
				stringPersidno = stringPersidno.replace( " ", "" );
			}

			return stringPersidno;
		}

		/**
		 * Returns the year that a person was born.
		 */
		public static Integer yearOfBirth( String nidString ) {

			if( nidString == null ) {
				return null;
			}

			String stringPersidno = cleanupNIDString( nidString );

			if( !isIndividual( nidString ) ) {
				return null;
			}

			if( stringPersidno.length() != 10 ) {
				return null;
			}

			String centuryFull = null;
			String centuryMarker = stringPersidno.substring( 9, 10 );
			String year = stringPersidno.substring( 4, 6 );

			if( centuryMarker.equals( "0" ) ) {
				centuryFull = "20";
			}
			else {
				centuryFull = "1" + centuryMarker;
			}

			String fullYear = centuryFull + year;

			return Integer.valueOf( fullYear );
		}

		/**
		 * Returns the month that a person was born.
		 */
		public static Integer monthOfBirth( String nidString ) {

			if( nidString == null ) {
				return null;
			}

			nidString = cleanupNIDString( nidString );

			if( !isIndividual( nidString ) ) {
				return null;
			}

			return Integer.valueOf( nidString.substring( 2, 4 ) );
		}

		/**
		 * Returns the month that a person was born.
		 */
		public static Integer dayOfBirth( String nidString ) {

			if( nidString == null ) {
				return null;
			}

			nidString = cleanupNIDString( nidString );

			if( !isIndividual( nidString ) ) {
				return null;
			}

			return Integer.valueOf( nidString.substring( 0, 2 ) );
		}

		/**
		 * Returns a persidno, formatted by inserting a "-" after the birthdate. returns an empty string if persidno is not valid.
		 *
		 * @param persidno to format
		 * @return formatted persidno
		 */
		public static String format( String persidno ) {
			return format( persidno, DEFAULT_DELIMITER );
		}

		/**
		 * Returns a persidno, formatted by inserting the specified delimiter after the birthdate. eturns an empty string if persidno is not valid.
		 *
		 * @param persidno  to format
		 * @param delimiter delimiter between the six'th and seven'th decimals
		 * @return formatted persidno
		 */
		public static String format( String persidno, String delimiter ) {

			if( persidno == null ) {
				return "";
			}

			persidno = cleanupNIDString( persidno );

			if( persidno.length() != 10 ) {
				return "";
			}

			return persidno.substring( 0, 6 ) + delimiter + persidno.substring( 6 );
		}

		/**
		 * If invoked ON the person's birthday, it will return the current day.
		 *
		 * @param currentDate The date to use for determination.
		 * @param nidString    to deduce information from.
		 * @return The individuals next birthday, as calculated from the persidno.
		 */
		public static LocalDate nextBirthday( final LocalDate currentDate, final String nidString ) {

			if( nidString == null ) {
				return null;
			}

			Integer birthDay = dayOfBirth( nidString );
			Integer birthMonth = monthOfBirth( nidString );

			int nowDay = currentDate.getDayOfMonth();
			int nowMonth = currentDate.getMonthValue();
			int year = currentDate.getYear();

			if( nowMonth > birthMonth ) {
				year++;
			}

			if( nowMonth == birthMonth && nowDay > birthDay ) {
				year++;
			}

			return LocalDate.of( year, birthMonth, birthDay );
		}

		/**
		 * Checks if the specified String contains only digits.
		 *
		 * @param string the string to check
		 * @return true if the string contains only digits, false otherwise
		 */
		private static boolean isDigitsOnly( String string ) {
			
			for( int i = string.length(); i-- > 0; ) {
				char c = string.charAt( i );
				if( !Character.isDigit( c ) ) {
					return false;
				}
			}
			
			return true;
		}
	}
}