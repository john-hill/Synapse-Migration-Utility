package org.sagebionetworks.migration.async;

import java.util.List;

import org.sagebionetworks.repo.model.migration.MigrationType;
import org.sagebionetworks.repo.model.migration.MigrationTypeCount;

/**
 * Abstraction for an executor that fetches types counts from both the source and destination.
 *
 */
public interface TypeCountsExecutor {

	/**
	 * Execute the type counts on both the source and destination at the same time.
	 * 
	 * @param typesToMigrate
	 * @return
	 */
	ConcurrentExecutionResult<List<MigrationTypeCount>> getMigrationTypeCounts(List<MigrationType> typesToMigrate);

}
