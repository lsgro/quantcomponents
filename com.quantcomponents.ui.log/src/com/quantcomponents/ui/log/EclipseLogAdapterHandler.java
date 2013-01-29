package com.quantcomponents.ui.log;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.log.LogService;

public class EclipseLogAdapterHandler extends Handler {
	private final ILog eclipseLogger;
	
	public EclipseLogAdapterHandler(ILog eclipseLogger) {
		this.eclipseLogger = eclipseLogger;
	}

	@Override
	public void publish(LogRecord logRecord) {
		int eclipseSeverity = mapLogRecordLevel(logRecord.getLevel());
		String className = logRecord.getSourceClassName();
		String message = logRecord.getMessage();
		Throwable e = logRecord.getThrown();
		IStatus status;
		if (e != null) {
			status = new Status(eclipseSeverity, className, message, e);
		} else {
			status = new Status(eclipseSeverity, className, message);
		}
		eclipseLogger.log(status);
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() throws SecurityException {
	}

    private int mapLogRecordLevel(Level level) {
        if (level.intValue() <= Level.INFO.intValue()) {
            return IStatus.INFO;
        } else if (level.intValue() <= Level.WARNING.intValue()) {
            return IStatus.WARNING;
        } else {
            return LogService.LOG_ERROR;
        }
    }
}
