SET @schema_name = DATABASE();

SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = @schema_name
              AND table_name = 'station'
              AND index_name = 'uk_station_station'
        ),
        'SELECT 1',
        'ALTER TABLE `station` ADD UNIQUE KEY `uk_station_station` (`station`)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = @schema_name
              AND table_name = 'station_date'
              AND index_name = 'uk_station_date_station_date'
        ),
        'SELECT 1',
        'ALTER TABLE `station_date` ADD UNIQUE KEY `uk_station_date_station_date` (`station`, `date`)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = @schema_name
              AND table_name = 'station_date'
              AND index_name = 'idx_station_date_date'
        ),
        'SELECT 1',
        'ALTER TABLE `station_date` ADD KEY `idx_station_date_date` (`date`)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = @schema_name
              AND table_name = 'user'
              AND index_name = 'uk_user_username'
        ),
        'SELECT 1',
        'ALTER TABLE `user` ADD UNIQUE KEY `uk_user_username` (`username`)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

DROP PROCEDURE IF EXISTS ensure_existing_meteo_indexes;
DELIMITER //
CREATE PROCEDURE ensure_existing_meteo_indexes()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE table_name_value VARCHAR(128);
    DECLARE cursor_tables CURSOR FOR
        SELECT table_name
        FROM information_schema.tables
        WHERE table_schema = DATABASE()
          AND table_name LIKE '%\\_meteo\\_data' ESCAPE '\\';
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cursor_tables;

    table_loop: LOOP
        FETCH cursor_tables INTO table_name_value;
        IF done THEN
            LEAVE table_loop;
        END IF;

        IF NOT EXISTS (
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = DATABASE()
              AND table_name = table_name_value
              AND index_name = 'uk_datetime'
        ) THEN
            SET @sql = CONCAT('ALTER TABLE `', table_name_value, '` ADD UNIQUE KEY `uk_datetime` (`datetime`)');
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END IF;

        IF NOT EXISTS (
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = DATABASE()
              AND table_name = table_name_value
              AND index_name = 'idx_date'
        ) THEN
            SET @sql = CONCAT('ALTER TABLE `', table_name_value, '` ADD KEY `idx_date` (`date`)');
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END IF;

        IF NOT EXISTS (
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = DATABASE()
              AND table_name = table_name_value
              AND index_name = 'idx_station'
        ) THEN
            SET @sql = CONCAT('ALTER TABLE `', table_name_value, '` ADD KEY `idx_station` (`station`)');
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END IF;
    END LOOP;

    CLOSE cursor_tables;
END //
DELIMITER ;

CALL ensure_existing_meteo_indexes();
DROP PROCEDURE IF EXISTS ensure_existing_meteo_indexes;
