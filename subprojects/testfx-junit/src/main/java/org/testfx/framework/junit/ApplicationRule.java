/*
 * Copyright 2013-2014 SmartBear Software
 * Copyright 2014-2015 The TestFX Contributors
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence"); You may
 * not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the Licence for the
 * specific language governing permissions and limitations under the Licence.
 */
package org.testfx.framework.junit;

import javafx.stage.Stage;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;

import java.util.function.Consumer;

public class ApplicationRule extends FxRobot
        implements ApplicationFixture, TestRule {

    private final Consumer<Stage> start;

    public ApplicationRule(Consumer<Stage> start) {
        this.start = start;
    }

    @Override
    public void init() throws Exception {
        // do nothing
    }

    @Override
    public void start(Stage stage) throws Exception {
        start.accept(stage);
    }

    @Override
    public void stop() throws Exception {
        // do nothing
    }

    private void before() throws Exception {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(() -> new ApplicationAdapter(this));
    }

    private void after() throws Exception {
        FxToolkit.cleanupApplication(new ApplicationAdapter(this));
    }

    private Statement externalResource(final Statement base) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                before();
                try {
                    base.evaluate();
                } finally {
                    after();
                }
            }
        };
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return externalResource(base);
    }

}
