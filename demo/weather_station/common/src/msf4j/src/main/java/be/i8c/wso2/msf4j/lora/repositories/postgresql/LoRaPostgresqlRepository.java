/*
  * Copyright (c) 2017, i8c N.V. (Integr8 Consulting; http://www.i8c.be)
  * All Rights Reserved.
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package be.i8c.wso2.msf4j.lora.repositories.postgresql;

import be.i8c.wso2.msf4j.lora.models.common.SensorRecord;
import be.i8c.wso2.msf4j.lora.repositories.LoRaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * This is a implementation of LoRaRepository using Spring Data JPA which uses the postgresql as back-end database.
 * It's used to insert the lora packet into postgresql database.
 *
 * Note: This class will only be injected when you run with VM argument: -Dspring.profiles.active=postgresql
 * @author yanglin
 */

@Profile("postgresql")
@Repository
public interface LoRaPostgresqlRepository extends LoRaRepository, CrudRepository<SensorRecord, Long>
{

}
