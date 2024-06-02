CREATE DATABASE IF NOT EXISTS school
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;

USE school;

CREATE TABLE IF NOT EXISTS facilitators(
    id INT NOT NULL PRIMARY KEY
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS classes (
    id INT NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS students (
    id INT NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    login_id VARCHAR(50) NOT NULL,
    class_id INT NOT NULL,
    facilitator_id INT NOT NULL,
    FOREIGN KEY (class_id) REFERENCES classes(id),
    FOREIGN KEY (facilitator_id) REFERENCES facilitators(id)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

INSERT INTO facilitators (id) VALUES
(1),
(2)
ON DUPLICATE KEY UPDATE id=VALUES(id);

INSERT INTO classes (id, name) VALUES
(1, 'クラスA'),
(2, 'クラスB'),
(3, 'クラスC')
ON DUPLICATE KEY UPDATE id=VALUES(id);

INSERT INTO students (id, name, login_id, class_id, facilitator_id) VALUES
(1, '佐藤', 'foo123', 1, 1),
(2, '鈴木', 'bar456', 2, 2),
(3, '田中', 'baz789', 3, 1),
(4, '加藤', 'hoge0000', 1, 1),
(5, '太田', 'fuga1234', 2, 2),
(6, '佐々木', 'piyo5678', 3, 1),
(7, '小田原', 'fizz9999', 1, 1),
(8, 'Smith', 'buzz777', 2, 2),
(9, 'Johnson', 'fizzbuzz#123', 3, 1),
(10, 'Williams', 'xxx:42', 1, 1)
ON DUPLICATE KEY UPDATE id=VALUES(id);

