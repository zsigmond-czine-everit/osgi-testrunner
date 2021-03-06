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
package org.everit.osgi.dev.testrunner.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.logging.Logger;

import org.everit.osgi.dev.testrunner.TestRunnerConstants;
import org.everit.osgi.dev.testrunner.engine.TestEngine;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class TestRunnerEngineServiceTracker extends ServiceTracker<TestEngine, TestEngine> implements
        TestRunnerEngineTracker {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TestRunnerEngineServiceTracker.class.getName());

    private BundleContext bundleContext;

    private ReentrantReadWriteLock mapRWLock = new ReentrantReadWriteLock(false);

    private Map<String, List<TestEngine>> testRunnersByEngineType = new HashMap<String, List<TestEngine>>();

    public TestRunnerEngineServiceTracker(final BundleContext bundleContext) {
        super(bundleContext, TestEngine.class, null);
        this.bundleContext = bundleContext;
    }

    public TestRunnerEngineServiceTracker(final BundleContext context, final ServiceReference<TestEngine> reference,
            final ServiceTrackerCustomizer<TestEngine, TestEngine> customizer, final BundleContext bundleContext) {
        super(context, reference, customizer);
        this.bundleContext = bundleContext;
    }

    @Override
    public TestEngine addingService(final ServiceReference<TestEngine> reference) {
        Object engineType = reference.getProperty(TestRunnerConstants.SERVICE_PROPERTY_TESTRUNNER_ENGINE_TYPE);
        if (engineType == null) {
            LOGGER.warning("Registered test runner service did not have "
                    + TestRunnerConstants.SERVICE_PROPERTY_TESTRUNNER_ENGINE_TYPE
                    + " service property attached. Service will not be used even if service properties are"
                    + " modified later: " + reference.toString());
            return null;
        }
        if (!(engineType instanceof String)) {
            LOGGER.warning("Service property " + TestRunnerConstants.SERVICE_PROPERTY_TESTRUNNER_ENGINE_TYPE
                    + " of registered test runner service has a different type than String therefore it will be"
                    + " ignored even if the service property is changed later: " + reference.toString());
            return null;
        }
        String testEngine = (String) engineType;
        TestEngine runnerEngine = bundleContext.getService(reference);

        WriteLock writeLock = mapRWLock.writeLock();
        writeLock.lock();
        List<TestEngine> testRunners = testRunnersByEngineType.get(testEngine);
        if (testRunners == null) {
            testRunners = new ArrayList<TestEngine>();
            testRunnersByEngineType.put(testEngine, testRunners);
        }
        testRunners.add(runnerEngine);

        writeLock.unlock();

        return runnerEngine;
    }

    @Override
    public TestEngine getEngineByType(final String testEngineType) {
        ReadLock readLock = mapRWLock.readLock();
        readLock.lock();
        List<TestEngine> testRunners = testRunnersByEngineType.get(testEngineType);
        readLock.unlock();
        if (testRunners != null) {
            return testRunners.get(0);
        } else {
            return null;
        }

    }

    @Override
    public void modifiedService(final ServiceReference<TestEngine> reference, final TestEngine service) {
        // Do nothing
    }

    @Override
    public void removedService(final ServiceReference<TestEngine> reference, final TestEngine service) {
        Object engineType = reference.getProperty(TestRunnerConstants.SERVICE_PROPERTY_TESTRUNNER_ENGINE_TYPE);
        String testEngine = (String) engineType;

        WriteLock writeLock = mapRWLock.writeLock();
        writeLock.lock();
        List<TestEngine> testRunners = testRunnersByEngineType.get(testEngine);
        testRunners.remove(service);
        if (testRunners.size() == 0) {
            testRunnersByEngineType.remove(testEngine);
        }
        writeLock.unlock();
    }
}
