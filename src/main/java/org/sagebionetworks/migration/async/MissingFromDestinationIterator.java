package org.sagebionetworks.migration.async;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.sagebionetworks.migration.config.Configuration;
import org.sagebionetworks.migration.utils.TypeToMigrateMetadata;
import org.sagebionetworks.repo.model.daemon.BackupAliasType;
import org.sagebionetworks.repo.model.migration.BackupTypeRangeRequest;
import org.sagebionetworks.repo.model.migration.BackupTypeResponse;

/**
 * Creates n number of backups batches for rows in the source that do not exist in the destination.
 * The number of backups required is determined by the maximum batch size.
 * 
 * Note: Each backup is created on the next() call so the caller drives the backup processed.
 */
public class MissingFromDestinationIterator implements Iterator<DestinationJob> {

	AsynchronousJobExecutor asynchronousJobExecutor;

	Iterator<BackupTypeRangeRequest> requestIterator;

	public MissingFromDestinationIterator(Configuration config, AsynchronousJobExecutor asynchronousJobExecutor,
			TypeToMigrateMetadata typeToMigrate) {
		super();
		this.asynchronousJobExecutor = asynchronousJobExecutor;
		List<BackupTypeRangeRequest> request = createBackupRequests(typeToMigrate, config.getBackupAliasType(),
				config.getMaximumBackupBatchSize(), config.getDestinationRowCountToIgnore());
		this.requestIterator = request.iterator();
	}

	@Override
	public boolean hasNext() {
		return this.requestIterator.hasNext();
	}

	@Override
	public DestinationJob next() {
		BackupTypeRangeRequest nextRequest = this.requestIterator.next();
		// create a backup for this request and wait for the results.
		BackupTypeResponse response = asynchronousJobExecutor.executeSourceJob(nextRequest, BackupTypeResponse.class);
		return new RestoreDestinationJob(nextRequest.getMigrationType(), response.getBackupFileKey());
	}

	/**
	 * Create all of the backup requests that will be required to fill in the missing rows
	 * from the destination.
	 * 
	 * @param typeToMigrate
	 * @param backupAliasType
	 * @param maxBatchSize
	 * @return
	 */
	static List<BackupTypeRangeRequest> createBackupRequests(TypeToMigrateMetadata typeToMigrate,
			BackupAliasType backupAliasType, long maxBatchSize, long destinationRowCountToIgnore) {
		List<BackupTypeRangeRequest> requests = new LinkedList<>();
		// start with the full range
		long startId = typeToMigrate.getSrcMinId();
		if (typeToMigrate.getDestMaxId() != null) {
			if(typeToMigrate.getDestCount() != null) {
				if(typeToMigrate.getDestCount() > destinationRowCountToIgnore) {
					// there are rows in the destination so start there.
					startId = typeToMigrate.getDestMaxId() + 1;
				}
			}
		}
		// Max is exclusive so the end includes + one.
		long endId = typeToMigrate.getSrcMaxId()+1;
		// create a request for each missing batch in the range.
		while (startId < endId) {
			BackupTypeRangeRequest request = new BackupTypeRangeRequest();
			request.setBatchSize(maxBatchSize);
			request.setAliasType(backupAliasType);
			request.setMigrationType(typeToMigrate.getType());
			// Note: The minimum ID is inclusive in the backup.
			request.setMinimumId(startId);
			long maxId = Math.min(startId + maxBatchSize, endId);
			// Note: The maximum ID is exclusive for the backup.
			request.setMaximumId(maxId);
			requests.add(request);
			// next starts at the end of this batch.
			startId = request.getMaximumId();
		}
		return requests;
	}

}
