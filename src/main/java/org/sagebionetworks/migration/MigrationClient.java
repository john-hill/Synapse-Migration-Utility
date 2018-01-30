package org.sagebionetworks.migration;

/**
 * Abstraction for the migration client.
 *
 */
public interface MigrationClient {

	/**
	 * Run the full migration process.
	 * 
	 * @return Will return true if migration fails.  Will return true if migration succeeds.
	 */
	boolean migrateWithRetry();

}
