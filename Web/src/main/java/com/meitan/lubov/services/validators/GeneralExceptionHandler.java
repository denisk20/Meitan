package com.meitan.lubov.services.validators;

import org.springframework.webflow.engine.FlowExecutionExceptionHandler;
import org.springframework.webflow.engine.RequestControlContext;
import org.springframework.webflow.execution.FlowExecutionException;

/**
 * Date: Nov 11, 2010
 * Time: 10:57:31 AM
 *
 * @author denisk
 */
public abstract class GeneralExceptionHandler implements FlowExecutionExceptionHandler {
	@Override
	public boolean canHandle(FlowExecutionException exception) {
		Throwable cause = exception.getCause();
		Class classToHandle = getClassToHandle();

		boolean canHandle = false;
		while (cause != null) {
			if (cause.getClass().equals(classToHandle)) {
				canHandle = true;
				break;
			}
			cause = cause.getCause();
		}

		return canHandle;
	}

	protected abstract Class getClassToHandle();

}
