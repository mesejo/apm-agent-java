/*-
 * #%L
 * Elastic APM Java agent
 * %%
 * Copyright (C) 2018 - 2019 Elastic and contributors
 * %%
 * Licensed to Elasticsearch B.V. under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch B.V. licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * #L%
 */
package co.elastic.apm.agent.jms;

import co.elastic.apm.agent.bci.VisibleForAdvice;
import co.elastic.apm.agent.matcher.WildcardMatcher;
import co.elastic.apm.agent.matcher.WildcardMatcherValueConverter;
import org.stagemonitor.configuration.ConfigurationOption;
import org.stagemonitor.configuration.ConfigurationOptionProvider;
import org.stagemonitor.configuration.converter.ListValueConverter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MessagingConfiguration extends ConfigurationOptionProvider {
    private static final String MESSAGING_CATEGORY = "Messaging";
    private static final String MESSAGE_POLLING_TRANSACTION_STRATEGY = "message_polling_transaction_strategy";

    private ConfigurationOption<Strategy> messagePollingTransaction = ConfigurationOption.enumOption(Strategy.class)
        .key(MESSAGE_POLLING_TRANSACTION_STRATEGY)
        .configurationCategory(MESSAGING_CATEGORY)
        .tags("internal")
        .description("Determines whether the agent should create transactions for the polling action itself (e.g. `javax.jms.MessageConsumer#receive`), \n" +
            "attempt to create a transaction for the message handling code occurring if the polling method returns a message, \n" +
            "or both. Valid options are: `POLLING`, `HANDLING` and `BOTH`. \n" +
            "\n" +
            "This option is case-insensitive.")
        .dynamic(true)
        .buildWithDefault(Strategy.HANDLING);

    private final ConfigurationOption<List<WildcardMatcher>> ignoreMessageQueues = ConfigurationOption
        .builder(new ListValueConverter<>(new WildcardMatcherValueConverter()), List.class)
        .key("ignore_message_queues")
        .configurationCategory(MESSAGING_CATEGORY)
        .description("Used to filter out specific messaging queues/topics from being traced. \n" +
            "\n" +
            "This property should be set to an array containing one or more strings.\n" +
            "When set, sends-to and receives-from the specified queues/topic will be ignored.\n" +
            "\n" +
            WildcardMatcher.DOCUMENTATION)
        .dynamic(true)
        .buildWithDefault(Collections.<WildcardMatcher>emptyList());

    public MessagingConfiguration.Strategy getMessagePollingTransactionStrategy() {
        return messagePollingTransaction.get();
    }

    public List<WildcardMatcher> getIgnoreMessageQueues() {
        return ignoreMessageQueues.get();
    }

    @VisibleForAdvice
    public enum Strategy {
        POLLING,
        HANDLING,
        BOTH
    }
}
