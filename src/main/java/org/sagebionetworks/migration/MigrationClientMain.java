package org.sagebionetworks.migration;

import java.io.IOException;

import org.sagebionetworks.client.exceptions.SynapseException;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * The main entry point for the V3 data migration process.
 *
 */
public class MigrationClientMain {

	/**
	 * The main entry for for the V3 migration client.
	 * @param args
	 * @throws IOException 
	 * @throws SynapseException 
	 * @throws JSONObjectAdapterException 
	 */
	public static void main(String[] args) throws Exception {
		Injector injector = Guice.createInjector(new MigrationModule());
		// Create the migration client.
		MigrationClient client = injector.getInstance(MigrationClient.class);
		// This call will run the entire migration process.
		boolean failed = client.migrateWithRetry();
		if (failed) {
			System.exit(-1);
		} else {
			System.exit(0);
		}
	}
	
}
