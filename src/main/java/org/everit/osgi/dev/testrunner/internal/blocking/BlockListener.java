package org.everit.osgi.dev.testrunner.internal.blocking;

/*
 * Copyright (c) 2011, Everit Kft.
 *
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

/**
 * A {@link Blocker} can notify the {@link BlockingManager} that is should block or let starting the test runners via
 * this listener.
 */
public interface BlockListener {

    /**
     * The {@link BlockingManager} should not start the test runners yet.
     */
    void block();

    /**
     * The {@link BlockingManager} should start the test runners if no other {@link Blocker} blocks.
     */
    void unblock();
}