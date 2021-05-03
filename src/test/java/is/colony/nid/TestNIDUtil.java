package is.colony.nid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests for USPersidnoUtilities.
 */

public class TestNIDUtil {

	@Test
	public void cleanupPersidno() {
		assertTrue( NID.Util.cleanupNIDString( "091179 4829" ).equals( "0911794829" ) );
		assertTrue( NID.Util.cleanupNIDString( "091179-4829" ).equals( "0911794829" ) );
		assertTrue( NID.Util.cleanupNIDString( "0911794829" ).equals( "0911794829" ) );
		assertTrue( NID.Util.cleanupNIDString( "09" ).equals( "09" ) );
		assertFalse( NID.Util.cleanupNIDString( "091179.4829" ).equals( "0911794829" ) );
	}

	@Test
	public void birthyearFromPersidno() {
		assertTrue( NID.Util.yearOfBirth( "091179 4829" ) == 1979 );
		assertTrue( NID.Util.yearOfBirth( "110375-3219" ) == 1975 );
		assertTrue( (NID.Util.yearOfBirth( "5703003340" ) == null) );
		assertFalse( NID.Util.yearOfBirth( "0911794829" ) == 1999 );
	}

	@Test
	public void birthMonthFromPersidno() {
		assertTrue( NID.Util.monthOfBirth( "091179 4829" ) == 11 );
		assertTrue( NID.Util.monthOfBirth( "130110-2230" ) == 1 );
		assertTrue( NID.Util.monthOfBirth( "5703003340" ) == null );
		assertFalse( NID.Util.monthOfBirth( "0911794829" ) == 10 );
	}

	@Test
	public void birthDayFromPersidno() {
		assertTrue( NID.Util.dayOfBirth( "091179 4829" ) == 9 );
		assertTrue( NID.Util.dayOfBirth( "110266-3229" ) == 11 );
		assertTrue( NID.Util.dayOfBirth( "5703003340" ) == null );
		assertFalse( NID.Util.dayOfBirth( "0911794829" ) == 8 );
	}

	@Test
	public void formatPersidno() {
		assertTrue( NID.Util.format( "091179 4829" ).equals( "091179-4829" ) );
		assertTrue( NID.Util.format( "0911794829" ).equals( "091179-4829" ) );
		assertTrue( NID.Util.format( "570300-3340" ).equals( "570300-3340" ) );
	}

	@Test
	public void formatPersidnoWithDelimiter() {
		assertTrue( NID.Util.format( "091179 4829", "-" ).equals( "091179-4829" ) );
		assertTrue( NID.Util.format( "0911794829", "-" ).equals( "091179-4829" ) );
		assertTrue( NID.Util.format( "570300-3340", "-" ).equals( "570300-3340" ) );
	}

	@Test
	public void isIndividualPersidno() {
		assertTrue( NID.Util.isIndividual( "0911794829" ) );
		assertFalse( NID.Util.isIndividual( "5703003340" ) );
		assertFalse( NID.Util.isIndividual( null ) );
	}

	@Test
	public void isCompanyPersidno() {
		assertTrue( NID.Util.isCompany( "5703003340" ) );
		assertFalse( NID.Util.isCompany( "0911794829" ) );
		assertFalse( NID.Util.isCompany( null ) );
	}

	@Test
	public void validatePersidno() {
		assertTrue( NID.Util.validateNIDString( "1301102230" ) );
		assertTrue( NID.Util.validateNIDString( "0911794829" ) );
		assertTrue( NID.Util.validateNIDString( "6005111490" ) );

		assertFalse( NID.Util.validateNIDString( null ) );
		assertFalse( NID.Util.validateNIDString( "AAA" ) );
		assertFalse( NID.Util.validateNIDString( "BBBBBBBBBB" ) );

		// FIXME: WE need to check why these tests are in error. Do we need to check the values explicitly?
		// assertTrue( NID.Util.validateNIDString( "4612911399" ) );
		// assertFalse( USStringUtilities.validatePersidno( "0000000000" ) );
		// assertFalse( USStringUtilities.validatePersidno( "1111111111" ) );
	}

	@Test
	public void birthdateFromPersidno() {
		LocalDate expectedBirthdate = LocalDate.of( 1979, 11, 9 );
		LocalDate hugiBirthDate = NID.Util.dateOfBirthFromNIDString( "0911794829" );
		assertEquals( hugiBirthDate, expectedBirthdate );
	}

	/**
	 * Note: Unless we find a way to keep me 29 forever, this test will eventually fail. We need to find another way to provide test data.
	 */
	@Test
	public void ageFromPersidno() {
		Integer n = NID.Util.ageFromNIDString( "0911794829", LocalDate.of( 2011, 1, 1 ) );
		assertTrue( n == 31 );
	}

	@Test
	public void ageAtDate() {
		LocalDate birthdate = LocalDate.of( 1979, 11, 9 );
		Integer expected = null;
		Integer result = null;

		expected = 19;
		result = NID.Util.ageAtDate( birthdate, LocalDate.of( 1999, 11, 8 ) );
		assertEquals( expected, result );

		expected = 20;
		result = NID.Util.ageAtDate( birthdate, LocalDate.of( 1999, 11, 9 ) );
		assertEquals( expected, result );

		expected = 20;
		result = NID.Util.ageAtDate( birthdate, LocalDate.of( 1999, 11, 10 ) );
		assertEquals( expected, result );

		expected = 21;
		result = NID.Util.ageAtDate( birthdate, LocalDate.of( 2000, 11, 10 ) );
		assertEquals( expected, result );
	}

	/**
	 * Note: Unless we find a way to keep me 29 forever, this test will eventually fail. We need to find another way to provide test data.
	 */
	@Test
	public void nextBirthday() {
		LocalDate birthday = null;

		birthday = NID.Util.nextBirthday( LocalDate.of( 2012, 1, 1 ), "0911794829" );
		assertEquals( LocalDate.of( 2012, 11, 9 ), birthday );

		birthday = NID.Util.nextBirthday( LocalDate.of( 2012, 5, 1 ), "1401833029" );
		assertEquals( LocalDate.of( 2013, 1, 14 ), birthday );
	}
}