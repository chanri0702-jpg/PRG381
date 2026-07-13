/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  BC-STUDENT
 */
CREATE TABLE addresses (
                           address     VARCHAR(255) NOT NULL,
                           area        VARCHAR(100),
                           city        VARCHAR(100) NOT NULL,
                           province    VARCHAR(14)  NOT NULL,
                           postal_code VARCHAR(4)   NOT NULL
);

CREATE TABLE person (
                        name    VARCHAR(100) NOT NULL,
                        surname VARCHAR(100) NOT NULL,
                        phone   VARCHAR(10),
                        email   VARCHAR(150) NOT NULL
);

CREATE TABLE supplier_businesses (
                                     bus_id      SERIAL PRIMARY KEY,
                                     name        VARCHAR(150) NOT NULL,
                                     description TEXT
);

CREATE TABLE supplier_offices (
                                  off_id  SERIAL PRIMARY KEY,
                                  bus_id  INT NOT NULL REFERENCES supplier_businesses(bus_id) ON DELETE CASCADE
) INHERITS (addresses);

CREATE TABLE campuses (
                          camp_id SERIAL PRIMARY KEY,
                          name    VARCHAR(150) NOT NULL
) INHERITS (addresses);

CREATE SEQUENCE employees_emp_id_seq START WITH 100000;

CREATE TABLE employees (
                           emp_id   INT PRIMARY KEY DEFAULT nextval('employees_emp_id_seq'),
                           camp_id  INT REFERENCES campuses(camp_id),
                           role     VARCHAR(50) NOT NULL CHECK (role IN ('STOREKEEPER', 'SUPERVISOR')),
                           password VARCHAR(255) NOT NULL,
                           email    VARCHAR(150) NOT NULL UNIQUE
) INHERITS (person);

ALTER SEQUENCE employees_emp_id_seq OWNED BY employees.emp_id;

CREATE TABLE supplier_employees (
                                    sup_id SERIAL PRIMARY KEY,
                                    bus_id INT REFERENCES supplier_businesses(bus_id) ON DELETE CASCADE,
                                    email  VARCHAR(150) NOT NULL UNIQUE
) INHERITS (person);

CREATE TABLE products (
                          prod_id     SERIAL PRIMARY KEY,
                          bus_id      INT NOT NULL REFERENCES supplier_businesses(bus_id) ON DELETE CASCADE,
                          name        VARCHAR(150) NOT NULL,
                          price       NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
                          description TEXT
);

CREATE TABLE orders (
                        ord_id   SERIAL PRIMARY KEY,
                        emp_id   INT REFERENCES employees(emp_id),
                        ord_date DATE DEFAULT CURRENT_DATE
);

CREATE TABLE order_products (
                                prod_id  INT REFERENCES products(prod_id) ON DELETE CASCADE,
                                ord_id   INT REFERENCES orders(ord_id) ON DELETE CASCADE,
                                quantity INT NOT NULL CHECK (quantity > 0),
                                total    NUMERIC(10, 2) NOT NULL CHECK (total >= 0),
                                PRIMARY KEY (prod_id, ord_id)
);

CREATE TABLE requests (
                          req_id      SERIAL PRIMARY KEY,
                          emp_id      INT REFERENCES employees(emp_id),
                          prod_id     INT REFERENCES products(prod_id),
                          quantity    INT NOT NULL CHECK (quantity > 0),
                          status      VARCHAR(15) NOT NULL DEFAULT 'PENDING'
                              CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'ISSUED')),
                          priority    VARCHAR(15) NOT NULL DEFAULT 'NORMAL'
                              CHECK (priority IN ('LOW', 'NORMAL', 'HIGH', 'URGENT')),
                          description TEXT,
                          req_date    DATE DEFAULT CURRENT_DATE
);

CREATE TABLE request_orders (
                                ord_id INT REFERENCES orders(ord_id) ON DELETE CASCADE,
                                req_id INT REFERENCES requests(req_id) ON DELETE CASCADE,
                                PRIMARY KEY (ord_id, req_id)
);

CREATE TABLE product_stock (
                               prod_id INT REFERENCES products(prod_id) ON DELETE CASCADE,
                               camp_id INT REFERENCES campuses(camp_id) ON DELETE CASCADE,
                               stock   INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
                               PRIMARY KEY (prod_id, camp_id)
);
