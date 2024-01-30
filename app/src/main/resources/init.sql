CREATE TABLE if not exists  `customer1`   (
                            `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                            `email` varchar(120) DEFAULT NULL,
                            `name` varchar(11) DEFAULT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;