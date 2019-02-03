/*
 * Copyright 2019 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.autoconfigure.storage.voltdb;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import zipkin2.storage.StorageComponent;
import zipkin2.storage.voltdb.VoltDBStorage;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(ZipkinVoltDBStorageProperties.class)
@ConditionalOnProperty(name = "zipkin.storage.type", havingValue = "voltdb")
@ConditionalOnMissingBean(StorageComponent.class)
    // This component is named .*VoltDB.* even though the package already says voltdb because
    // Spring Boot configuration endpoints only printout the simple name of the class
class ZipkinVoltDBStorageAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  StorageComponent storage(ZipkinVoltDBStorageProperties properties) {
    return properties.toBuilder().build();
  }

  @Bean
  @ConditionalOnProperty(
      value = "zipkin.storage.voltdb.scheduling.enabled", havingValue = "true", matchIfMissing = true
  )
  VoltDBScheduledTasks scheduledTasks(VoltDBStorage storage) {
    return new VoltDBScheduledTasks(storage);
  }

}
