/**
 * This file is part of Everit Test Runner Bundle.
 *
 * Everit Test Runner Bundle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Everit Test Runner Bundle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Everit Test Runner Bundle.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.dev.testrunner;
import java.util.List;

import org.everit.osgi.dev.testrunner.engine.TestClassResult;
import org.osgi.framework.ServiceReference;

/**
 * Via the TestManager OSGi service 3rd party tools can specify which tests should run after a bundle deployment.
 */
public interface TestManager {

    /**
     * Checking if the JVM is in development mode from the perspective of the test runner. By default, JVM is in
     * development mode if the {@link TestRunnerConstants#ENV_STOP_AFTER_TESTS} environment variable is set to true.
     * 
     * @return The development mode flag.
     */
    boolean isInDevelopmentMode();

    /**
     * Runs all tests that are found based on the service reference.
     * 
     * @param reference
     *            The service reference that points to test objects.
     * @param force
     *            Force to run test methods in development mode even if they are not annotated to do so. See
     *            {@link #setInDevelopmentMode(boolean)}.
     * 
     * @return The test results.
     */
    List<TestClassResult> runTest(ServiceReference<Object> reference, boolean force);

    /**
     * Setting the test runner to beleive that the JVM is in development mode or not. By default, JVM is in development
     * mode if the {@link TestRunnerConstants#ENV_STOP_AFTER_TESTS} environment variable is set to true.
     * 
     * @param inDevelopmentMode
     *            The development
     */
    void setInDevelopmentMode(boolean inDevelopmentMode);
}
