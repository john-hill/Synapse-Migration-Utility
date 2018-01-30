package org.sagebionetworks.migration;

import org.sagebionetworks.migration.async.AsynchronousJobExecutor;
import org.sagebionetworks.migration.async.AsynchronousJobExecutorImpl;
import org.sagebionetworks.migration.config.Configuration;
import org.sagebionetworks.migration.config.FileProvider;
import org.sagebionetworks.migration.config.FileProviderImp;
import org.sagebionetworks.migration.config.MigrationConfigurationImpl;
import org.sagebionetworks.migration.config.SystemPropertiesProvider;
import org.sagebionetworks.migration.config.SystemPropertiesProviderImpl;
import org.sagebionetworks.migration.factory.SynapseClientFactory;
import org.sagebionetworks.migration.factory.SynapseClientFactoryImpl;

import com.google.inject.AbstractModule;

public class MigrationModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(FileProvider.class).to(FileProviderImp.class);
		bind(SystemPropertiesProvider.class).to(SystemPropertiesProviderImpl.class);
		bind(Configuration.class).to(MigrationConfigurationImpl.class);
		bind(SynapseClientFactory.class).to(SynapseClientFactoryImpl.class);
		bind(AsynchronousJobExecutor.class).to(AsynchronousJobExecutorImpl.class);
	}

}
