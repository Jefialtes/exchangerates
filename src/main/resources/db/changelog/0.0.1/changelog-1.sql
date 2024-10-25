DROP TABLE IF EXISTS exchange_rate;
DROP TABLE IF EXISTS currency;

CREATE TABLE currency
    (
        id              SERIAL PRIMARY KEY,
        currency_code   VARCHAR(3),
        update_time     INTEGER
    );

CREATE TABLE exchange_rate
    (
        id                          SERIAL PRIMARY KEY,
        changeable_currency_id      BIGINT NOT NUll,
        currency_code               VARCHAR(3),
        rate                        DECIMAL(20, 6),
        FOREIGN KEY (changeable_currency_id) REFERENCES currency(id) ON DELETE CASCADE
    );

--INSERT INTO currency (id, currency_code)
--VALUES  (1, 'EUR'),
--        (2, 'USD');
--
--INSERT INTO exchange_rate (id, changeable_currency_id, currency_code, rate)
--VALUES  (1, 1, 'AED', 3.95884),
--        (2, 1, 'AFN', 72.069568),
--        (3, 2, 'ANG', 1.942155),
--        (4, 2, 'AOA', 982.970949),
--        (5, 2, 'AWG', 1.940074);