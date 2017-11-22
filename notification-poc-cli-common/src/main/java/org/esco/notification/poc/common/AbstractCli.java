package org.esco.notification.poc.common;

import org.esco.notification.poc.common.options.ConnectionFactoryOptions;
import org.esco.notification.poc.common.options.LongHelpOption;
import org.esco.notification.poc.common.options.VerboseOption;

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
