package org.esco.notification.randombeans;

import javax.inject.Inject;
import java.io.Closeable;

public abstract class AbstractCli implements Closeable {
    @Inject
    public LongHelpOption helpOption;

    @Inject
    public VerboseOption verboseOption;

    @Inject
    public ConnectionFactoryOptions connectionFactoryOptions;
}
