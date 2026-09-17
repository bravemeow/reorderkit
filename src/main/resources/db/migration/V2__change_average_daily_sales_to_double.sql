ALTER TABLE product_variants
    ALTER COLUMN average_daily_sales_30d
        TYPE DOUBLE PRECISION
        USING average_daily_sales_30d::DOUBLE PRECISION;

ALTER TABLE reorder_history
    ALTER COLUMN average_daily_sales_30d
        TYPE DOUBLE PRECISION
        USING average_daily_sales_30d::DOUBLE PRECISION;
